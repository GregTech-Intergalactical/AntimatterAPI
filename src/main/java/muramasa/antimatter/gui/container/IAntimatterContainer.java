package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.GuiInstance;
import net.minecraft.inventory.container.Container;

public interface IAntimatterContainer {
    GuiInstance source();

    default void init() {
        source().init();
    }
}
