package muramasa.antimatter.proxy;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.material.MaterialType;
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
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientHandler implements IProxyHandler {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        AntimatterModelManager.setup();
        AntimatterAPI.all(AntimatterModelLoader.class).forEach(l -> ModelLoaderRegistry.registerLoader(l.getLoc(), l));
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

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
