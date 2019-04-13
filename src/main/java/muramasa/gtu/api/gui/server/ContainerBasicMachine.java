package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.gui.GuiUpdateType;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBasicMachine extends ContainerMachine {

    private int lastProgress = -1, lastState = -1;

    public ContainerBasicMachine(TileEntityBasicMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int curProgress = tile.getCurProgress();
        int curState = tile.getMachineState().getId();
        if (Math.abs(curProgress - lastProgress) >= GuiUpdateType.PROGRESS.getUpdateThreshold()) {
            int progress = (int)(((float)curProgress / (float)tile.getMaxProgress()) * Short.MAX_VALUE);
            listeners.forEach(l -> l.sendWindowProperty(this, GuiUpdateType.PROGRESS.ordinal(), progress));
            lastProgress = curProgress;
        }
        if (Math.abs(curState - lastState) >= GuiUpdateType.MACHINE_STATE.getUpdateThreshold()) {
            listeners.forEach(l -> l.sendWindowProperty(this, GuiUpdateType.MACHINE_STATE.ordinal(), curState));
            lastState = curState;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == GuiUpdateType.PROGRESS.ordinal()) {
            tile.setClientProgress((float)data / (float)Short.MAX_VALUE);
        } else if (id == GuiUpdateType.MACHINE_STATE.ordinal()) {
            tile.setMachineState(MachineState.VALUES[data]);
        }
    }
}
