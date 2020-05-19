package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerHatchMachine extends ContainerMachine {

    public ContainerHatchMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandlerMachine menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
