package muramasa.gregtech.api.gui.server;

import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.inventory.IInventory;

public class ContainerMultiMachine extends ContainerBasicMachine {

    public ContainerMultiMachine(TileEntityMultiMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }
}
