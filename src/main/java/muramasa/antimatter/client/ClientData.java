package muramasa.antimatter.client;

import muramasa.antimatter.gui.container.*;
import muramasa.antimatter.gui.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;

public class ClientData {
    public final static MenuScreens.ScreenConstructor SCREEN_BASIC = (a, b, c) -> new ScreenBasicMachine((ContainerBasicMachine) a, b, c);
    public final static MenuScreens.ScreenConstructor SCREEN_MACHINE = (a, b, c) -> new ScreenMachine((ContainerMachine) a, b, c);
    public final static MenuScreens.ScreenConstructor SCREEN_MULTI = (a, b, c) -> new ScreenMultiMachine((ContainerMultiMachine) a, b, c);
    public final static MenuScreens.ScreenConstructor SCREEN_HATCH = (a, b, c) -> new ScreenHatch((ContainerHatch) a, b, c);
    public final static MenuScreens.ScreenConstructor SCREEN_COVER = (a, b, c) -> new ScreenCover((ContainerCover) a, b, c);
    public final static MenuScreens.ScreenConstructor SCREEN_DEFAULT = (a, b, c) -> new AntimatterContainerScreen(a, b, c);
}
