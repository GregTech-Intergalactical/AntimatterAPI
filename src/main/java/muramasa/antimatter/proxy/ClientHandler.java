package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.client.ScreenSetup;
import muramasa.antimatter.cover.CoverNone;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.container.ContainerHatch;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.gui.screen.*;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.registration.IColorHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static muramasa.antimatter.Data.COVERNONE;

public class ClientHandler implements IProxyHandler {

    @SuppressWarnings("ConstantConditions")
    public ClientHandler() {
        if (Minecraft.getInstance() != null) { //Null with runData
            //Minecraft.getInstance().getResourcePackList().addPackFinder(Ref.PACK_FINDER);
            //Minecraft.getInstance().getResourcePackList().addPackFinder(Ref.SERVER_PACK_FINDER);
            AntimatterModelManager.init();
            AntimatterAPI.all(AntimatterModelLoader.class).forEach(l -> ModelLoaderRegistry.registerLoader(l.getLoc(), l));
        }
        /* Client event listeners. */
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ClientHandler::onItemColorHandler);
        eventBus.addListener(ClientHandler::onBlockColorHandler);
        eventBus.addListener(ClientHandler::onModelRegistry);
        eventBus.addListener(AntimatterTextureStitcher::onTextureStitch);
        ScreenSetup.<ContainerMachine, ScreenBasicMachine<ContainerMachine>>setScreenMapping(Data.BASIC_MENU_HANDLER, ScreenBasicMachine::new);
        ScreenSetup.<ContainerCover, ScreenCover<ContainerCover>>setScreenMapping(Data.COVER_MENU_HANDLER, ScreenCover::new);
        ScreenSetup.<ContainerMultiMachine, ScreenMultiMachine<ContainerMultiMachine>>setScreenMapping(Data.MULTI_MENU_HANDLER, ScreenMultiMachine::new);
        ScreenSetup.<ContainerHatch, ScreenHatch<ContainerHatch>>setScreenMapping(Data.HATCH_MENU_HANDLER, ScreenHatch::new);
        ScreenSetup.<ContainerMachine, ScreenSteamMachine<ContainerMachine>>setScreenMapping(Data.STEAM_MENU_HANDLER, ScreenSteamMachine::new);

        AntimatterTextureStitcher.addStitcher(event -> AntimatterAPI.all(ICover.class).forEach(cover -> {
            if (cover instanceof CoverNone || cover == COVERNONE) return;
            for (ResourceLocation r : cover.getTextures()) {
                event.accept(r);
            }
        }));
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static void setup(FMLClientSetupEvent e) {
        /* Register screens. */
        AntimatterAPI.runLaterClient(() -> AntimatterAPI.all(MenuHandler.class, h -> ScreenManager.registerFactory(h.getContainerType(), ScreenSetup.get(h))));
        /* Set up render types. */
        AntimatterAPI.runLaterClient(() -> {
            AntimatterAPI.all(BlockMachine.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockMultiMachine.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockOre.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(BlockStorage.class).stream().filter(b -> b.getType() == Data.FRAME).forEach(b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
            AntimatterAPI.all(AntimatterFluid.class).forEach(f -> {
                RenderTypeLookup.setRenderLayer(f.getFluid(), RenderType.getTranslucent());
                RenderTypeLookup.setRenderLayer(f.getFlowingFluid(), RenderType.getTranslucent());
            });
        });
    }

    public static void onItemColorHandler(ColorHandlerEvent.Item e) {
        for (Item item : AntimatterAPI.all(Item.class)) {
            if (item instanceof IColorHandler && ((IColorHandler) item).registerColorHandlers()) {
                e.getItemColors().register((stack, i) -> ((IColorHandler) item).getItemColor(stack, null, i), item);
            }
        }
        for (Block block : AntimatterAPI.all(Block.class)) {
            if (block instanceof IColorHandler && ((IColorHandler) block).registerColorHandlers()) {
                e.getItemColors().register((stack, i) -> ((IColorHandler) block).getItemColor(stack, null, i), block.asItem());
            }
        }
    }

    public static void onBlockColorHandler(ColorHandlerEvent.Block e) {
        for (Block block : AntimatterAPI.all(Block.class)) {
            if (block instanceof IColorHandler) e.getBlockColors().register(((IColorHandler) block)::getBlockColor, block);
        }
    }

    public static void onModelRegistry(ModelRegistryEvent e) {

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
