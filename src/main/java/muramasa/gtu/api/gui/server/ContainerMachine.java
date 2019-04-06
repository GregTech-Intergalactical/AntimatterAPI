package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.gui.SlotData;
import muramasa.gtu.api.gui.slot.SlotInput;
import muramasa.gtu.api.gui.slot.SlotOutput;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.inventory.IInventory;

public class ContainerMachine extends ContainerBase {

    public ContainerMachine(TileEntityMachine tile, IInventory playerInv) {
        super(playerInv);
        addSlots(tile);
    }

    protected void addSlots(TileEntityMachine tile) {
        MachineItemHandler itemHandler = tile.getItemHandler();
        if (itemHandler == null) return;

        int inputIndex = 0, outputIndex = 0, cellIndex = 0;
        for (SlotData slot : tile.getType().getGui().getSlots(tile.getTier())) {
            switch (slot.type) {
                case IT_IN:
                    addSlotToContainer(new SlotInput(itemHandler.getInputHandler(), inputIndex++, slot.x, slot.y));
                    break;
                case IT_OUT:
                    addSlotToContainer(new SlotOutput(itemHandler.getOutputHandler(), outputIndex++, slot.x, slot.y));
                    break;
                case CELL_IN:
                    addSlotToContainer(new SlotInput(itemHandler.getCellHandler(), cellIndex++, slot.x, slot.y));
                    break;
                case CELL_OUT:
                    addSlotToContainer(new SlotOutput(itemHandler.getCellHandler(), cellIndex++, slot.x, slot.y));
                    break;
            }
        }
    }
}
