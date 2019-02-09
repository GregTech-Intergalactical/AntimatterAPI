package muramasa.gregtech.api.gui.container;

import muramasa.gregtech.api.gui.slot.SlotCell;
import muramasa.gregtech.api.gui.slot.SlotInput;
import muramasa.gregtech.api.gui.slot.SlotOutput;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.SlotData;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerMachine extends ContainerBase {

    protected TileEntityMachine tile;
    private int cellStart;

    public ContainerMachine(TileEntityMachine tile, IInventory playerInv) {
        super(tile.getMachineType().getSlotCount() + (tile.getMachineType().hasFlag(MachineFlag.FLUID_INPUT) ? 1 : 0), playerInv);
        this.tile = tile;
        IItemHandler stackHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (stackHandler != null) {
            addSlots(stackHandler);
        }
        if (tile instanceof TileEntityBasicMachine && tile.getMachineType().hasFlag(MachineFlag.FLUID_INPUT)) {
            addCellSlots(((TileEntityBasicMachine) tile).getCellHandler());
        }
    }

    public void addSlots(IItemHandler stackHandler) {
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

    public void addCellSlots(IItemHandler stackHandler) {
        cellStart = inventorySlots.size();
        addSlotToContainer(new SlotCell(stackHandler, 0, 35, 63));
        addSlotToContainer(new SlotCell(stackHandler, 1, 125, 63));
    }
}
