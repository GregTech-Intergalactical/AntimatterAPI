package muramasa.itech.api.gui.container;

import muramasa.itech.api.gui.slot.SlotInput;
import muramasa.itech.api.gui.slot.SlotOutput;
import muramasa.itech.api.capability.implementations.MachineStackHandler;
import muramasa.itech.api.machines.objects.SlotData;
import muramasa.itech.common.tileentities.TileEntityMachine;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMachine extends ContainerBase {

    private TileEntityMachine tile;
    private MachineStackHandler stackHandler;

    private int lastProgress = -1;

    public ContainerMachine(TileEntityMachine tile, IInventory playerInv) {
        super(tile.getStackHandler().getSlots(), playerInv);
        this.tile = tile;
        this.stackHandler = tile.getStackHandler();
        addSlots();
    }

    private void addSlots() {
        SlotData[] slots = tile.getMachineType().getSlotData();
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            if (tile.getCurProgress() != lastProgress) {
                int progress = (int)(((float)tile.getCurProgress() / (float)tile.getMaxProgress()) * Short.MAX_VALUE);
                listener.sendWindowProperty(this, 0, progress);
                lastProgress = tile.getCurProgress();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            tile.setClientProgress((float)data / (float)Short.MAX_VALUE);
        }
    }
}
