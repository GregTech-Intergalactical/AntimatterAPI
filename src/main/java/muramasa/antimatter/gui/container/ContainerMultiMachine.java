package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerMultiMachine extends ContainerMachine {

    public ContainerMultiMachine(TileEntityMultiMachine tile, PlayerInventory playerInv, MenuHandler menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
