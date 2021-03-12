package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.gui.MenuHandler;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;

import java.util.Map;

public class ScreenSetup {

    private static Map<MenuHandler, ScreenManager.IScreenFactory> factoryMap = new Object2ObjectOpenHashMap<>();

    public static <T extends Container, U extends Screen & IHasContainer<T>> void setScreenMapping(MenuHandler<T> mh, ScreenManager.IScreenFactory<T,U> factory) {
        factoryMap.put(mh, factory);
    }

    public static <T extends Container, U extends Screen & IHasContainer<T>> ScreenManager.IScreenFactory<T,U> get(MenuHandler handler) {
        return (ScreenManager.IScreenFactory<T,U>) factoryMap.get(handler);
    }

}
