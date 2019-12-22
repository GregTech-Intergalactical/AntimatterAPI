package muramasa.gtu.api.container;

import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerMultiMachine extends ContainerMachine {

    public ContainerMultiMachine(TileEntityMultiMachine tile, PlayerInventory playerInv, MenuHandler menuHandler, int windowId) {
        super(tile, playerInv, menuHandler, windowId);
    }
}
