package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.slot.SlotEnergy;
import muramasa.antimatter.gui.slot.SlotInput;
import muramasa.antimatter.gui.slot.SlotOutput;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.GuiEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;

import java.util.List;

public abstract class ContainerMachine extends AntimatterContainer {

    protected TileEntityMachine tile;

    public ContainerMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandlerMachine menuHandler, int windowId) {
        super(menuHandler.getContainerType(), windowId, playerInv, tile.getMachineType().getGui().getSlots(tile.getMachineTier()).size());
        addSlots(tile);
        tile.setClientProgress(0);
        if (tile.getMachineType().getGui().enablePlayerSlots()) addPlayerSlots();
        //TODO: Generic over classes.
        this.tile = tile;

        trackIntArray(getMachineData());
    }

    public TileEntityMachine getTile() {
        return tile;
    }

    protected IIntArray getMachineData() {
        return tile.getContainerData();
    }



    /*@Override
    public void detectAndSendChanges() {
        //int curState = tile.getMachineState().ordinal();
        super.detectAndSendChanges();
        tile.fluidHandler.ifPresent(h -> {
            if ((h.getInputWrapper() != null && h.getInputWrapper().dirty) || (h.getOutputWrapper() != null && h.getOutputWrapper().dirty)) {
                if (h.getInputWrapper() != null) h.getInputWrapper().dirty = false;
                if (h.getOutputWrapper() != null) h.getOutputWrapper().dirty = false;
                GregTechNetwork.syncMachineTanks(tile);
            }
        });
    }*/

  /*  @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == GuiEvent.MACHINE_STATE.ordinal()) {
            tile.setMachineState(MachineState.VALUES[data]);
        }
    }*/

    protected void addSlots(TileEntityMachine tile) {
        tile.itemHandler.ifPresent(h -> {
            int inputIndex = 0, outputIndex = 0, cellIndex = 0, chargeIndex = 0;
            List<SlotData> lst = tile.getMachineType().getGui().getSlots(tile.getMachineTier());
            for (SlotData slot : lst) {
                switch (slot.type) {
                    case IT_IN:
                        addSlot(new SlotInput(h.getInputWrapper(), inputIndex++, slot.x, slot.y));
                        break;
                    case IT_OUT:
                        addSlot(new SlotOutput(h.getOutputWrapper(), outputIndex++, slot.x, slot.y));
                        break;
                    case CELL_IN:
                        addSlot(new SlotInput(h.getCellWrapper(), cellIndex++, slot.x, slot.y));
                        break;
                    case CELL_OUT:
                        addSlot(new SlotOutput(h.getCellWrapper(), cellIndex++, slot.x, slot.y));
                        break;
                    case ENERGY:
                        addSlot(new SlotEnergy(h.getChargeWrapper(), chargeIndex++, slot.x, slot.y));
                        break;
                }
            }
        });
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.of(tile.getWorld(), tile.getPos()), player, tile.getBlockState().getBlock());
    }
}
