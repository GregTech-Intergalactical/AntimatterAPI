package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
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
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler implements IProxyHandler {

    @SuppressWarnings("ConstantConditions")
    public ClientHandler() {
        if (Minecraft.getInstance() != null) { //Null with runData
            Minecraft.getInstance().getResourcePackList().addPackFinder(Ref.PACK_FINDER);
            AntimatterModelManager.init();
            AntimatterAPI.all(AntimatterModelLoader.class).forEach(l -> ModelLoaderRegistry.registerLoader(l.getLoc(), l));
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    public static void setup(FMLClientSetupEvent e) {
        AntimatterAPI.all(MenuHandler.class, h -> ScreenManager.registerFactory(h.getContainerType(), h));
        AntimatterAPI.runLaterClient(() -> {
            AntimatterAPI.all(BlockMachine.class, b -> RenderTypeLookup.setRenderLayer(b, RenderType.getCutout()));
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
