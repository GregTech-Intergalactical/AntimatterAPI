package muramasa.itech.api.gui.container;

import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.inventory.IInventory;

public class ContainerHatch extends ContainerMachine {

    public ContainerHatch(TileEntityMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }
}
