package muramasa.gtu.api.gui.server;

import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;

public class ContainerHatch extends ContainerMachine {

    private ArrayList<Integer> lastFluidIds;

    public ContainerHatch(TileEntityHatch tile, IInventory playerInv) {
        super(tile, playerInv);
        if (tile.getType().hasFlag(MachineFlag.FLUID)) lastFluidIds = new ArrayList<>();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
//        if (lastFluidIds == null) return;
//        ArrayList<Integer> curFluidIds = tile.getFluidHandler().getInputIds();
//        if (!curFluidIds.containsAll(lastFluidIds)) {
//            FluidStack[] inputs = tile.getFluidHandler().getInputs();
//            if (inputs.length < 1) return;
//            System.out.println("updating clients");
//            for (IContainerListener listener : listeners) {
//                listener.sendWindowProperty(this, GuiUpdateType.FLUID.ordinal(), Utils.getIdByFluid(inputs[0].getFluid()));
//            }
//        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
//        super.updateProgressBar(id, data);
//        if (id == GuiUpdateType.FLUID.ordinal()) {
//            tile.getFluidHandler().getInputWrapper().setFirstValidOrEmptyTank(new FluidStack(Utils.getFluidById(data), 1000));
//        }
    }
}
