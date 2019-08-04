package muramasa.gtu.api.cover;

import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CoverOutput extends Cover {

    @Override
    public String getId() {
        return "output";
    }

    @Override
    public void onMachineEvent(TileEntityMachine tile, MachineEvent event) {
        if (event == MachineEvent.ITEM_OUTPUT) {
            EnumFacing outputFacing = tile.getOutputFacing();
            TileEntity adjTile = tile.getWorld().getTileEntity(tile.getPos().offset(outputFacing));
            if (adjTile == null) return;
            if (adjTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFacing.getOpposite())) {
                IItemHandler machineInventory = tile.getItemHandler().getOutputHandler();
                IItemHandler adjInventory = adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFacing.getOpposite());
                for (int i = 0; i < adjInventory.getSlots(); i++) {
                    if (i >= machineInventory.getSlots()) break;
                    ItemStack toInsert = machineInventory.extractItem(i, machineInventory.getStackInSlot(i).getCount(), true);
                    if (ItemHandlerHelper.insertItem(adjInventory, toInsert, true).isEmpty()) {
                        ItemHandlerHelper.insertItem(adjInventory, machineInventory.extractItem(i, machineInventory.getStackInSlot(i).getCount(), false), false);
                    }
                }
            }
        }
    }
}
