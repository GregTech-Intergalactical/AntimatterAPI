package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.gui.GuiEvent;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBasicMachine extends ContainerMachine {

    private int lastProgress = -1;

    public ContainerBasicMachine(TileEntityBasicMachine tile, IInventory playerInv) {
        super(tile, playerInv);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int curProgress = tile.getCurProgress();
        if (Math.abs(curProgress - lastProgress) >= GuiEvent.PROGRESS.getUpdateThreshold()) {
            int progress = (int) (((float) curProgress / (float) tile.getMaxProgress()) * Short.MAX_VALUE);
            listeners.forEach(l -> l.sendWindowProperty(this, GuiEvent.PROGRESS.ordinal(), progress));
            lastProgress = curProgress;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == GuiEvent.PROGRESS.ordinal()) {
            ((TileEntityBasicMachine) tile).setClientProgress((float)data / (float)Short.MAX_VALUE);
        }
    }
}
