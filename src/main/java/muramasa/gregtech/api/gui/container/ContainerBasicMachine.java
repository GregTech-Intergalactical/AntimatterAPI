package muramasa.gregtech.api.gui.container;

import muramasa.gregtech.api.machines.MachineState;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBasicMachine extends ContainerMachine {

    private TileEntityBasicMachine tile;

    private int lastProgress = -1;
    private int lastState = -1;

    public ContainerBasicMachine(TileEntityBasicMachine tile, IInventory playerInv) {
        super(tile, playerInv);
        this.tile = tile;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int curProgress = tile.getCurProgress();
        int curState = tile.getMachineState().getId();
        for (IContainerListener listener : listeners) {
            if (curProgress != lastProgress) {
                int progress = (int)(((float)curProgress / (float)tile.getMaxProgress()) * Short.MAX_VALUE);
                listener.sendWindowProperty(this, 0, progress);
                lastProgress = curProgress;
            }
            if (curState != lastState) {
                listener.sendWindowProperty(this, 1, curState);
                lastState = curState;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == 0) {
            tile.setClientProgress((float)data / (float)Short.MAX_VALUE);
        } else if (id == 1) {
            tile.setMachineState(MachineState.VALUES[data]);
        }
    }
}
