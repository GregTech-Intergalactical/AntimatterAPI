package muramasa.antimatter.client;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.container.ContainerBasicMachine;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.gui.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientData {
    public final static MenuScreens.ScreenConstructor SCREEN_BASIC = AntimatterAPI.register(MenuScreens.ScreenConstructor.class, "basic", Ref.ID, (MenuScreens.ScreenConstructor)(a, b, c) -> new ScreenBasicMachine((ContainerBasicMachine) a, b, c));
    public final static MenuScreens.ScreenConstructor SCREEN_MACHINE = AntimatterAPI.register(MenuScreens.ScreenConstructor.class, "machine", Ref.ID, (MenuScreens.ScreenConstructor)(a, b, c) -> new ScreenMachine((ContainerMachine) a, b, c));
    public final static MenuScreens.ScreenConstructor SCREEN_MULTI = AntimatterAPI.register(MenuScreens.ScreenConstructor.class, "multi", Ref.ID, (MenuScreens.ScreenConstructor)(a, b, c) -> new ScreenMultiMachine((ContainerMultiMachine) a, b, c));
    public final static MenuScreens.ScreenConstructor SCREEN_COVER = AntimatterAPI.register(MenuScreens.ScreenConstructor.class, "cover", Ref.ID, (MenuScreens.ScreenConstructor)(a, b, c) -> new ScreenCover((ContainerCover) a, b, c));
    public final static MenuScreens.ScreenConstructor SCREEN_DEFAULT = AntimatterAPI.register(MenuScreens.ScreenConstructor.class, "default", Ref.ID, (MenuScreens.ScreenConstructor)(a, b, c) -> new AntimatterContainerScreen(a, b, c));

    public static void init(){
    }
}
