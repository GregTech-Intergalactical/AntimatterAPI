package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.gui.GuiUpdateType;
import muramasa.gtu.api.gui.SlotData;
import muramasa.gtu.api.gui.slot.SlotInput;
import muramasa.gtu.api.gui.slot.SlotOutput;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.common.network.GregTechNetwork;
import net.minecraft.inventory.IInventory;

public class ContainerMachine extends ContainerBase {

    protected TileEntityMachine tile;
    private int lastState = -1;

    public ContainerMachine(TileEntityMachine tile, IInventory playerInv) {
        super(tile.getType().getGui().enablePlayerSlots() ? playerInv : null);
        addSlots(tile);
        this.tile = tile;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int curState = tile.getMachineState().ordinal();
        if (Math.abs(curState - lastState) >= GuiUpdateType.MACHINE_STATE.getUpdateThreshold()) {
            listeners.forEach(l -> l.sendWindowProperty(this, GuiUpdateType.MACHINE_STATE.ordinal(), curState));
            lastState = curState;
        }
        if (tile.getFluidHandler() != null && ((tile.getFluidHandler().getInputWrapper() != null && tile.getFluidHandler().getInputWrapper().dirty) || (tile.getFluidHandler().getOutputWrapper() != null && tile.getFluidHandler().getOutputWrapper().dirty))) {
            if (tile.getFluidHandler().getInputWrapper() != null) tile.getFluidHandler().getInputWrapper().dirty = false;
            if (tile.getFluidHandler().getOutputWrapper() != null) tile.getFluidHandler().getOutputWrapper().dirty = false;
            GregTechNetwork.syncMachineTanks(tile);
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == GuiUpdateType.MACHINE_STATE.ordinal()) {
            tile.setMachineState(MachineState.VALUES[data]);
        }
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
