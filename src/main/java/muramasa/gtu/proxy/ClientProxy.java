package muramasa.gtu.proxy;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.util.SoundType;
import muramasa.gtu.client.events.BlockHighlightHandler;
import muramasa.gtu.client.events.RenderGameOverlayHandler;
import muramasa.gtu.client.events.TooltipHandler;
import muramasa.gtu.client.render.GTModelLoader;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedItem;
import muramasa.gtu.client.render.models.ModelFluidCell;
import muramasa.gtu.client.render.models.ModelMachine;
import muramasa.gtu.client.render.models.ModelPipe;
import muramasa.gtu.client.render.models.ModelRock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {

    private static Minecraft MC = Minecraft.getMinecraft();

    public static Object2ObjectOpenHashMap<String, IBakedModel> TYPE_SET_MAP = new Object2ObjectOpenHashMap<>();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(BlockHighlightHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderGameOverlayHandler.class);
        MinecraftForge.EVENT_BUS.register(TooltipHandler.class);
        ModelLoaderRegistry.registerLoader(new GTModelLoader());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IColorHandler) MC.getItemColors().registerItemColorHandler((stack, x) -> ((IColorHandler) i).getItemColor(stack, null, x), i);
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IColorHandler) {
                MC.getBlockColors().registerBlockColorHandler(((IColorHandler) b)::getBlockColor, b);
                MC.getItemColors().registerItemColorHandler((stack, x) -> ((IColorHandler) b).getItemColor(stack, b, x), Item.getItemFromBlock(b));
            }
        });
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        //NOOP
    }

    @Override
    public void playSound(SoundType type) {
        MC.player.playSound(type.getEvent(), type.getVolume(), type.getPitch());
        //TODO GregTechNetwork.NETWORK.sendToAllAround(new SoundMessage(type.getInternalId()), new NetworkRegistry.TargetPoint(MC.world.provider.getDimension(), MC.player.posX, MC.player.posY, MC.player.posZ, Ref.TOOL_SOUND_RANGE));
    }

    @Override
    public void sendDiggingPacket(BlockPos pos) {
        MC.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, MC.objectMouseOver.sideHit));
    }

    @Override
    public String trans(String unlocalized) {
        return I18n.format(unlocalized);
    }

    @SubscribeEvent
    public static void onRegisterTexture(TextureStitchEvent.Pre e) {
        //Apparently forge does not load fluid textures automatically
        GregTechAPI.all(TextureSet.class).forEach(s -> {
            e.getMap().registerSprite(s.getTexture(MaterialType.LIQUID, 0));
            e.getMap().registerSprite(s.getTexture(MaterialType.GAS, 0));
            e.getMap().registerSprite(s.getTexture(MaterialType.PLASMA, 0));
        });

        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).onTextureStitch(e.getMap());
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).onTextureStitch(e.getMap());
        });
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).onModelRegistration();
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).onModelRegistration();
        });

        ModelRock modelRock = new ModelRock();
        GTModelLoader.register("block_rock", modelRock);

        ModelMachine modelMachine = new ModelMachine();
        GTModelLoader.register("block_machine", modelMachine);

        ModelPipe modelPipe = new ModelPipe();
        GTModelLoader.register("block_pipe", modelPipe);

        ModelFluidCell modelFluidCell = new ModelFluidCell();
        GTModelLoader.register("fluid_cell", modelFluidCell);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        ModelUtils.onModelBake(e);

        IModel model;
        IBakedModel baked;
        for (MaterialType t : GregTechAPI.all(MaterialType.class)) {
            for (TextureSet s : GregTechAPI.all(TextureSet.class)) {
                if (t == MaterialType.BLOCK) {
                    model = ModelUtils.tex(ModelUtils.MODEL_BASIC, "0", s.getTexture(t, 0));
                    baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
                    TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
                } else if (t == MaterialType.FRAME) {
                    model = ModelUtils.tex(ModelUtils.MODEL_BASIC, "0", s.getTexture(t, 0));
                    baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
                    TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
                } else if (t == MaterialType.ORE) {
                    //TODO 1.14
                    //model = ModelUtils.tex(ModelUtils.MODEL_LAYERED, new String[]{"0", "1"}, s.getBlockTextures(t));
                    //baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
                    //TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
                } else {
                    model = new ItemLayerModel(ImmutableList.of(s.getTexture(t, 0), s.getTexture(t, 1)));
                    baked = new BakedItem(model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelUtils.getTextureGetter()));
                    TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
                }
            }
        }

        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).onModelBake(e.getModelRegistry());
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).onModelBake(e.getModelRegistry());
        });
    }
}
