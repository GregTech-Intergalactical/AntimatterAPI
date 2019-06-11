package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import net.minecraft.inventory.IInventory;

public class ContainerHatch extends ContainerMachine {

    public ContainerHatch(TileEntityHatch tile, IInventory playerInv) {
        super(tile, playerInv);
    }
}
