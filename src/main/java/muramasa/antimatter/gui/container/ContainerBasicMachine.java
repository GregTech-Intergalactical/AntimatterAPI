package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerBasicMachine<T extends TileEntityMachine<T>> extends ContainerMachine<T> {

    public ContainerBasicMachine(T tile, PlayerInventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> handler, int windowId) {
        super(tile, playerInv, handler, windowId);
    }
}
