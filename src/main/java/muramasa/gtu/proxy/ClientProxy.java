package muramasa.gtu.proxy;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.util.SoundType;
import muramasa.gtu.client.events.BlockHighlightHandler;
import muramasa.gtu.client.events.TooltipHandler;
import muramasa.gtu.client.render.GTModelLoader;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedBlock;
import muramasa.gtu.client.render.bakedmodels.BakedItem;
import muramasa.gtu.client.render.models.ModelFluidCell;
import muramasa.gtu.client.render.models.ModelMachine;
import muramasa.gtu.client.render.models.ModelPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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

import java.util.HashMap;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {

    static Minecraft MC = Minecraft.getMinecraft();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(BlockHighlightHandler.class);
        MinecraftForge.EVENT_BUS.register(RenderGameOverlayEvent.class);
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
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/liquid_still"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/liquid_flowing"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/gas_still"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/gas_flowing"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/plasma_still"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/plasma_flowing"));

        //Register Material Item textures
        GregTechAPI.all(MaterialItem.class).forEach(i -> i.getMaterial().getSet().getItemTextures(i.getPrefix()).forEach(r -> e.getMap().registerSprite(r)));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).onModelRegistration();
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).onModelRegistration();
        });

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

        //Generate Material Item TextureSet models
        HashMap<String, IBakedModel> PREFIX_SET_MAP = new HashMap<>();
        GregTechAPI.all(Prefix.class).forEach(p -> TextureSet.getAll().forEach(t -> {
            if (p.getType() == 0) {
                IModel model = new ItemLayerModel(t.getItemTextures(p));
                IBakedModel baked = new BakedItem(model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelUtils.getTextureGetter()));
                PREFIX_SET_MAP.put("item_".concat(p.getId().concat("_").concat(t.getId())), baked);
            } else if (p.getType() == 1) {
                if (p == Prefix.Block) {
                    IModel model = ModelUtils.tex(ModelUtils.MODEL_BASIC, "0", t.getBlockTexture(p));
                    IBakedModel baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
                    PREFIX_SET_MAP.put("block_".concat(p.getId().concat("_").concat(t.getId())), baked);
                }
                //TODO 1.14
                /* else if (p == Prefix.Ore) {
                    IModel model = ModelUtils.tex(ModelUtils.MODEL_LAYERED, new String[]{"0", "1"}, t.getBlockTextures(p));
                    IBakedModel baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
                    PREFIX_SET_MAP.put("block_".concat(p.getId().concat("_").concat(t.getId())), baked);
                }*/
            }
        }));

        //Inject models for Material Items
        GregTechAPI.all(MaterialItem.class).forEach(i -> {
            ModelResourceLocation model = new ModelResourceLocation(Ref.MODID + ":" + i.getPrefix().getId() + "_" + i.getMaterial().getId() + "#inventory");
            IBakedModel baked = PREFIX_SET_MAP.get("item_".concat(i.getPrefix().getId().concat("_").concat(i.getMaterial().getSet().getId())));
            e.getModelRegistry().putObject(model, baked);
        });

        GregTechAPI.all(BlockStorage.class).forEach(b -> {
            ModelResourceLocation normal = new ModelResourceLocation(Ref.MODID + ":block_" + b.getMaterial().getId() + "#normal");
            ModelResourceLocation inventory = new ModelResourceLocation(Ref.MODID + ":block_" + b.getMaterial().getId() + "#inventory");
            e.getModelRegistry().putObject(normal, PREFIX_SET_MAP.get("block_".concat(Prefix.Block.getId().concat("_").concat(b.getMaterial().getSet().getId()))));
            e.getModelRegistry().putObject(inventory, new BakedBlock(PREFIX_SET_MAP.get("block_".concat(Prefix.Block.getId().concat("_").concat(b.getMaterial().getSet().getId())))));
        });

        //GregTechAPI.all(BlockOre.class).forEach(b -> {
            //TODO 1.14. Ores need to use BlockStates in 1.12, and will be flattened in 1.14
        //});
    }
}
