package muramasa.gregtech.api.gui.server;

import muramasa.gregtech.api.tileentities.TileEntityMachine;
import net.minecraft.inventory.IInventory;

public class ContainerHatch extends ContainerMachine {

    public ContainerHatch(TileEntityMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }
}
