package muramasa.antimatter.gui.container;

import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerInventory;

public class ContainerBasicMachine extends ContainerMachine {

    private int lastProgress = -1;

    public ContainerBasicMachine(TileEntityMachine tile, PlayerInventory playerInv, MenuHandler handler, int windowId) {
        super(tile, playerInv, handler, windowId);
    }

//    @Override
//    public void detectAndSendChanges() {
//        super.detectAndSendChanges();
//        int curProgress = tile.getCurProgress();
//        if (Math.abs(curProgress - lastProgress) >= GuiEvent.PROGRESS.getUpdateThreshold()) {
//            int progress = (int) (((float) curProgress / (float) tile.getMaxProgress()) * Short.MAX_VALUE);
//            listeners.forEach(l -> l.sendWindowProperty(this, GuiEvent.PROGRESS.ordinal(), progress));
//            lastProgress = curProgress;
//        }
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void updateProgressBar(int id, int data) {
//        super.updateProgressBar(id, data);
//        if (id == GuiEvent.PROGRESS.ordinal()) {
//            ((TileEntityRecipeMachine) tile).setClientProgress((float)data / (float)Short.MAX_VALUE);
//        }
//    }
}
