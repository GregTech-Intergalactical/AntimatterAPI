package muramasa.itech.api.gui.container;

import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBasicMachine extends ContainerMachine {

    private int lastProgress = -1;

    public ContainerBasicMachine(TileEntityMachine tile, IInventory playerInv) {
        super(tile, playerInv);
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
        super.updateProgressBar(id, data);
        if (id == 0) {
            tile.setClientProgress((float)data / (float)Short.MAX_VALUE);
        }
    }
}
