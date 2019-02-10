package muramasa.gregtech.api.gui.container;

import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.inventory.IInventory;

public class ContainerMultiMachine extends ContainerMachine {

    public ContainerMultiMachine(TileEntityMachine tile, IInventory playerInv) {
        super(playerInv);
    }
}
