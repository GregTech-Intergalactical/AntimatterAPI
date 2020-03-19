package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockMachine;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.registration.IColorHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler implements IProxyHandler {

    private static Minecraft MC = Minecraft.getInstance();

    public ClientHandler() {

    }

    public static void init() {
//        GregTechAPI.onEvent(RegistrationEvent.DATA_READY, () -> {
//            GregTechAPI.all(MaterialItem.class).forEach(i -> {
//                //"assets/gtu/models/item/" + i.getId() + ".json"
//                GTI_RESOURCES.getPack().addModel(Ref.MODID, "item", i.getId(), GregTechItemModelProvider.layered(GTModelBuilder.getItemBuilder(), i.getMaterial().getSet().getTextures(i.getType())));
//            });
//
//
//            ItemModelBuilder itemBuilder = new ItemModelBuilder(new ResourceLocation("dummy"), GTModelBuilder.EXISTING_FILE_HELPER);
//            BlockModelBuilder blockBuilder = new BlockModelBuilder(new ResourceLocation("dummy"), GTModelBuilder.EXISTING_FILE_HELPER);
//            GregTechBlockStateProvider stateProvider = new GregTechBlockStateProvider(new DataGenerator(null, Collections.emptyList()), GTModelBuilder.EXISTING_FILE_HELPER);
//            GregTechAPI.all(BlockStone.class).forEach(b -> {
//                blockBuilder.parent(stateProvider.getExistingFile(stateProvider.mcLoc("block/cube_all"))).texture("all", b.getType().getTexture());
//                GTI_RESOURCES.getPack().addModel(Ref.MODID, "block", b.getId(), blockBuilder);
//
//                VariantBlockStateBuilder stateBuilder = stateProvider.getVariantBuilder(b);
//                stateBuilder.partialState().setModels(new ConfiguredModel(blockBuilder));
//                GTI_RESOURCES.getPack().addState(Ref.MODID, b.getId(), stateBuilder);
//
//                itemBuilder.parent(stateProvider.getExistingFile(b.getRegistryName()));
//                GTI_RESOURCES.getPack().addModel(Ref.MODID, "item", b.getId(), itemBuilder);
//            });
//        });
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        ModelLoaderRegistry.registerLoader(AntimatterModelLoader.MAIN.getLoc(), AntimatterModelLoader.MAIN);
        ModelLoaderRegistry.registerLoader(AntimatterModelLoader.DYNAMIC.getLoc(), AntimatterModelLoader.DYNAMIC);
        AntimatterAPI.all(MenuHandler.class).forEach(h -> ScreenManager.registerFactory(h.getContainerType(), h::getScreen));
        AntimatterAPI.all(BlockMachine.class).forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
        AntimatterAPI.all(BlockOre.class).forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
        AntimatterAPI.all(BlockStorage.class).stream().filter(b -> b.getType() == MaterialType.FRAME).forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
    }

    @SubscribeEvent
    public static void onItemColorHandler(ColorHandlerEvent.Item e) {
        AntimatterAPI.all(Item.class).forEach(i -> {
            if (i instanceof IColorHandler && ((IColorHandler) i).registerColorHandlers()) e.getItemColors().register((stack, x) -> ((IColorHandler) i).getItemColor(stack, null, x), i);
        });
        AntimatterAPI.all(Block.class).forEach(b -> {
            if (b instanceof IColorHandler && ((IColorHandler) b).registerColorHandlers()) e.getItemColors().register((stack, x) -> ((IColorHandler) b).getItemColor(stack, b, x), b.asItem());
        });
    }

    @SubscribeEvent
    public static void onBlockColorHandler(ColorHandlerEvent.Block e) {
        AntimatterAPI.all(Block.class).forEach(b -> {
            if (b instanceof IColorHandler) e.getBlockColors().register(((IColorHandler) b)::getBlockColor, b);
        });
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre e) {
        //TODO check correct map
        e.addSprite(ModelUtils.ERROR);
        //Apparently forge does not load fluid textures automatically
        //TODO is this needed in 1.14/1.15?
//        AntimatterAPI.all(TextureSet.class).forEach(s -> {
//            e.addSprite(s.getTexture(MaterialType.LIQUID, 0));
//            e.addSprite(s.getTexture(MaterialType.GAS, 0));
//            e.addSprite(s.getTexture(MaterialType.PLASMA, 0));
//        });
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent e) {
        //ModelUtils.buildDefaultModels();
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        //ModelUtils.buildDefaultModels();
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
