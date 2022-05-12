package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import net.minecraft.world.entity.player.Inventory;

public class ContainerHatch<T extends TileEntityHatch<T>> extends ContainerMachine<T> {

    public ContainerHatch(T tile, Inventory playerInv, MenuHandlerMachine<T, ContainerMachine<T>> menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
