package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.GuiInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerListener;

import java.util.Set;

public interface IAntimatterContainer {
    GuiInstance source();
    Set<ServerPlayer> listeners();
}
