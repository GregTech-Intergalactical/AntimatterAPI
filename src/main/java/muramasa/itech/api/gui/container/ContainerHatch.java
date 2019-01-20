package muramasa.itech.api.gui.container;

import muramasa.itech.common.tileentities.multi.TileEntityHatch;
import net.minecraft.inventory.IInventory;

public class ContainerHatch extends ContainerBase {

    public ContainerHatch(TileEntityHatch tile, IInventory playerInv) {
        super(tile.getStackHandler().getSlots(), playerInv);
    }
}
