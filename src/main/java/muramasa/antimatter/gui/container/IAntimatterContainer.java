package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.GuiInstance;
import net.minecraft.inventory.container.IContainerListener;

import java.util.Set;

public interface IAntimatterContainer {
    GuiInstance source();
    Set<IContainerListener> listeners();
}
