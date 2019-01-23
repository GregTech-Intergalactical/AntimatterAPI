package muramasa.itech.api.gui.container;

import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.inventory.IInventory;

public class ContainerMultiMachine extends ContainerBase {

    private TileEntityMultiMachine tile;

    public ContainerMultiMachine(TileEntityMultiMachine tile, IInventory playerInv) {
        super(0, playerInv);
        this.tile = tile;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
    }
}
