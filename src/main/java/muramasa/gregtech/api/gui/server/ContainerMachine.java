package muramasa.gregtech.api.gui.server;

import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.gui.slot.SlotCell;
import muramasa.gregtech.api.gui.slot.SlotInput;
import muramasa.gregtech.api.gui.slot.SlotOutput;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.gui.SlotData;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;

public class ContainerMachine extends ContainerBase {

    public ContainerMachine(TileEntityMachine tile, IInventory playerInv) {
        super(playerInv);
        Machine machine = tile.getType();
        MachineItemHandler itemHandler = tile.getItemHandler();
        if (itemHandler == null) return;

        addSlots(machine.getGui().getSlots(), tile.getItemHandler());
        if (machine.hasFlag(MachineFlag.FLUID)) {
            addCellSlots(tile.getItemHandler());
        }
    }

    public void addSlots(ArrayList<SlotData> slots, MachineItemHandler itemHandler) {
        int inputIndex = 0, outputIndex = 0;
        for (SlotData slot : slots) {
            if (slot.type == SlotType.IT_IN) { //Input Slot
                addSlotToContainer(new SlotInput(itemHandler.getInputHandler(), inputIndex++, slot.x, slot.y));
            } else if (slot.type == SlotType.IT_OUT) { //Output Slot
                addSlotToContainer(new SlotOutput(itemHandler.getOutputHandler(), outputIndex++, slot.x, slot.y));
            }
        }
    }

    public void addCellSlots(MachineItemHandler itemHandler) {
        addSlotToContainer(new SlotCell(itemHandler.getCellHandler(), 0, 35, 63));
        addSlotToContainer(new SlotCell(itemHandler.getCellHandler(), 1, 125, 63));
    }
}
