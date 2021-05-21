package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerMultiMachine<T extends TileEntityBasicMultiMachine<T>> extends ContainerMachine<T> {

    public ContainerMultiMachine(T tile, PlayerInventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
