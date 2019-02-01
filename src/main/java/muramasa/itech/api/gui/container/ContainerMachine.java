package muramasa.itech.api.gui.container;

import muramasa.itech.api.gui.slot.SlotInput;
import muramasa.itech.api.gui.slot.SlotOutput;
import muramasa.itech.api.machines.SlotData;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerMachine extends ContainerBase {

    protected TileEntityMachine tile;
    protected IItemHandler stackHandler;

    public ContainerMachine(TileEntityMachine tile, IInventory playerInv) {
        super(tile.getMachineType().getSlotCount(), playerInv);
        this.tile = tile;
        this.stackHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (stackHandler != null) {
            addSlots();
        }
    }

    public void addSlots() {
        SlotData[] slots = tile.getMachineType().getSlots();
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].type == 0) { //Input Slot
                addSlotToContainer(new SlotInput(stackHandler, i, slots[i].x, slots[i].y));
            } else if (slots[i].type == 1) { //Output Slot
                addSlotToContainer(new SlotOutput(stackHandler, i, slots[i].x, slots[i].y));
            } else if (slots[i].type == 3) { //Render Slot

            } else if (slots[i].type == 4) {
//                addSlotToContainer(new SlotRenderFluid(slots[i].d == 0 ? tile.tank.getFluid() : tile.tank.getFluid(), tile.stackHandler, i, slots[i].x, slots[i].y));
//                addSlotToContainer(new SlotRenderFluid(tile.tank.getFluid(), tile.stackHandler, i, slots[i].x, slots[i].y));
            }
        }
    }
}
