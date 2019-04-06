package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.inventory.IInventory;

public class ContainerHatch extends ContainerMachine {

    public ContainerHatch(TileEntityMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }
}
