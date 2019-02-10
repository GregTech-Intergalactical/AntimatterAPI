package muramasa.gregtech.api.gui.container;

import muramasa.gregtech.api.capability.impl.MachineStackHandler;
import muramasa.gregtech.api.gui.slot.SlotCell;
import muramasa.gregtech.api.gui.slot.SlotInput;
import muramasa.gregtech.api.gui.slot.SlotOutput;
import muramasa.gregtech.api.machines.SlotData;
import net.minecraft.inventory.IInventory;

public class ContainerMachine extends ContainerBase {

    private int cellStart;

    public ContainerMachine(IInventory playerInv) {
        super(playerInv);
    }

    public void addSlots(SlotData[] slots, MachineStackHandler stackHandler) {
        int inputIndex = 0, outputIndex = 0;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].type == 0) { //Input Slot
                addSlotToContainer(new SlotInput(stackHandler.getInputHandler(), inputIndex++, slots[i].x, slots[i].y));
            } else if (slots[i].type == 1) { //Output Slot
                addSlotToContainer(new SlotOutput(stackHandler.getOutputHandler(), outputIndex++, slots[i].x, slots[i].y));
            } else if (slots[i].type == 3) { //Render Slot

            } else if (slots[i].type == 4) {
//                addSlotToContainer(new SlotRenderFluid(slots[i].d == 0 ? tile.tank.getFluid() : tile.tank.getFluid(), tile.stackHandler, i, slots[i].x, slots[i].y));
//                addSlotToContainer(new SlotRenderFluid(tile.tank.getFluid(), tile.stackHandler, i, slots[i].x, slots[i].y));
            }
        }
    }

    public void addCellSlots(SlotData[] slots, MachineStackHandler stackHandler) {
        cellStart = inventorySlots.size();
        addSlotToContainer(new SlotCell(stackHandler.getCellHandler(), 0, 35, 63));
        addSlotToContainer(new SlotCell(stackHandler.getCellHandler(), 1, 125, 63));
    }
}
