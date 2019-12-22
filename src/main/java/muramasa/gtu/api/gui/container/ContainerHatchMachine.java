package muramasa.gtu.api.gui.container;

import muramasa.gtu.api.gui.MenuHandler;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerHatchMachine extends ContainerMachine {

    public ContainerHatchMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandler menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
