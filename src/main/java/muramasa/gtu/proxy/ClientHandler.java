package muramasa.gtu.proxy;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.gui.MenuHandler;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.util.SoundType;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler implements IProxyHandler {

    private static Minecraft MC = Minecraft.getInstance();
    private static ModelBakery BAKERY;

//    public static Object2ObjectOpenHashMap<String, IBakedModel> TYPE_SET_MAP = new Object2ObjectOpenHashMap<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::setup);

        GregTechAPI.all(MenuHandler.class).forEach(h -> ScreenManager.registerFactory(h.getContainerType(), h::getScreen));
    }

    public static void setup(FMLClientSetupEvent e) {
        //MinecraftForge.EVENT_BUS.register(BlockHighlightHandler.class);
        //MinecraftForge.EVENT_BUS.register(RenderGameOverlayHandler.class);
        //MinecraftForge.EVENT_BUS.register(TooltipHandler.class);
        //ModelLoaderRegistry.registerLoader(new GTModelLoader());
    }

    @SubscribeEvent
    public static void onItemColorHandler(ColorHandlerEvent.Item e) {
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IColorHandler) e.getItemColors().register((stack, x) -> ((IColorHandler) i).getItemColor(stack, null, x), i);
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IColorHandler) e.getItemColors().register((stack, x) -> ((IColorHandler) b).getItemColor(stack, b, x), b.asItem());
        });
    }

    public static void onBlockColorHandler(ColorHandlerEvent.Block e) {
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IColorHandler) e.getBlockColors().register(((IColorHandler) b)::getBlockColor, b);
        });
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre e) {
        //Apparently forge does not load fluid textures automatically
        GregTechAPI.all(TextureSet.class).forEach(s -> {
            e.addSprite(s.getTexture(MaterialType.LIQUID, 0));
            e.addSprite(s.getTexture(MaterialType.GAS, 0));
            e.addSprite(s.getTexture(MaterialType.PLASMA, 0));
        });

        Set<ResourceLocation> textures = new HashSet<>();
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).getTextures(textures);
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).getTextures(textures);
        });
        textures.forEach(e::addSprite);
    }

    @SubscribeEvent
    public static void onModelRegistration(ModelRegistryEvent e) {
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).onModelRegistration();
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).onModelRegistration();
        });

//        ModelRock modelRock = new ModelRock();
//        GTModelLoader.register("block_rock", modelRock);
//
//        ModelMachine modelMachine = new ModelMachine();
//        GTModelLoader.register("block_machine", modelMachine);
//
//        ModelPipe modelPipe = new ModelPipe();
//        GTModelLoader.register("block_pipe", modelPipe);
//
//        ModelFluidCell modelFluidCell = new ModelFluidCell();
//        GTModelLoader.register("fluid_cell", modelFluidCell);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        BAKERY = e.getModelLoader();
        ModelUtils.buildDefaultModels();
//        IModel model;
//        IBakedModel baked;
//        for (MaterialType t : GregTechAPI.all(MaterialType.class)) {
//            for (TextureSet s : GregTechAPI.all(TextureSet.class)) {
//                if (t == MaterialType.BLOCK) {
//                    model = ModelUtils.tex(ModelUtils.MODEL_BASIC, "0", s.getTexture(t, 0));
//                    baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
//                    TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
//                } else if (t == MaterialType.FRAME) {
//                    model = ModelUtils.tex(ModelUtils.MODEL_BASIC, "0", s.getTexture(t, 0));
//                    baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
//                    TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
//                } else if (t == MaterialType.ORE) {
//                    //TODO 1.14
//                    //model = ModelUtils.tex(ModelUtils.MODEL_LAYERED, new String[]{"0", "1"}, s.getBlockTextures(t));
//                    //baked = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
//                    //TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
//                } else {
//                    model = new ItemLayerModel(ImmutableList.of(s.getTexture(t, 0), s.getTexture(t, 1)));
//                    baked = new BakedItem(model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelUtils.getTextureGetter()));
//                    TYPE_SET_MAP.put(t.getId().concat("_").concat(s.getId()), baked);
//                }
//            }
//        }
        GregTechAPI.ITEMS.forEach(i -> {
            if (i instanceof IModelOverride) ((IModelOverride) i).onModelBake(e, e.getModelRegistry());
        });
        GregTechAPI.BLOCKS.forEach(b -> {
            if (b instanceof IModelOverride) ((IModelOverride) b).onModelBake(e, e.getModelRegistry());
        });

        e.getModelRegistry().put(new ModelResourceLocation("gtu:basic_fire_brick"), new TextureData().base(new Texture("minecraft", "dirt")).bakeAsBlock());

        System.out.println("bake done");
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public ModelBakery getModelBakery() {
        return BAKERY;
    }

    @Override
    public void playSound(SoundType type) {
        MC.player.playSound(type.getEvent(), type.getVolume(), type.getPitch());
        //TODO GregTechNetwork.NETWORK.sendToAllAround(new SoundMessage(type.getInternalId()), new NetworkRegistry.TargetPoint(MC.world.provider.getDimension(), MC.player.posX, MC.player.posY, MC.player.posZ, Ref.TOOL_SOUND_RANGE));
    }

    @Override
    public void sendDiggingPacket(BlockPos pos) {
        //TODO MC.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, MC.objectMouseOver.sideHit));
    }
}
