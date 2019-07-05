package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.inventory.IInventory;

public class ContainerMultiMachine extends ContainerBasicMachine {

    private int last

    public ContainerMultiMachine(TileEntityMultiMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

    }
}
