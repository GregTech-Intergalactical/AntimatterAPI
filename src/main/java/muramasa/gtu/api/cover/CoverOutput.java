package muramasa.gtu.api.cover;

import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CoverOutput extends Cover {

    @Override
    public String getId() {
        return "output";
    }

    @Override
    public void onUpdate(TileEntity tile) {
        if ((tile.getWorld().getTotalWorldTime() % 20) != 0) return;
        if (!(tile instanceof TileEntityMachine)) return;
        TileEntityMachine machine = (TileEntityMachine) tile;
        EnumFacing outputFacing = machine.getOutputFacing();
        TileEntity adjTile = tile.getWorld().getTileEntity(tile.getPos().offset(outputFacing.getOpposite()));
        System.out.println(tile.getPos().offset(outputFacing));
        if (adjTile == null) return;
        if (adjTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFacing.getOpposite())) {
            IItemHandler machineInventory = machine.getItemHandler().getOutputHandler();
            IItemHandler adjInventory = adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFacing.getOpposite());
            for (int i = 0; i < adjInventory.getSlots(); i++) {
                if (i >= machineInventory.getSlots()) break;
                adjInventory.insertItem(i, machineInventory.extractItem(i, machineInventory.getStackInSlot(i).getCount(), false), false);
            }
        }
    }
}
