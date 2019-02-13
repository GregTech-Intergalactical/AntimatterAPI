package muramasa.gregtech.api.gui.container;

import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.gui.slot.SlotCell;
import muramasa.gregtech.api.gui.slot.SlotInput;
import muramasa.gregtech.api.gui.slot.SlotOutput;
import muramasa.gregtech.api.machines.Slot;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;

public class ContainerMachine extends ContainerBase {

    public ContainerMachine(IInventory playerInv) {
        super(playerInv);
    }

    public void addSlots(ArrayList<Slot> slots, MachineItemHandler stackHandler) {
        int inputIndex = 0, outputIndex = 0;
        for (Slot slot : slots) {
            if (slot.type == 0) { //Input Slot
                addSlotToContainer(new SlotInput(stackHandler.getInputHandler(), inputIndex++, slot.x, slot.y));
            } else if (slot.type == 1) { //Output Slot
                addSlotToContainer(new SlotOutput(stackHandler.getOutputHandler(), outputIndex++, slot.x, slot.y));
            }
        }
    }

    public void addCellSlots(MachineItemHandler stackHandler) {
        addSlotToContainer(new SlotCell(stackHandler.getCellHandler(), 0, 35, 63));
        addSlotToContainer(new SlotCell(stackHandler.getCellHandler(), 1, 125, 63));
    }
}
