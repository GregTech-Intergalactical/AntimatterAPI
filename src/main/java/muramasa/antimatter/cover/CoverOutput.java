package muramasa.antimatter.cover;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class CoverOutput extends Cover {

    //TODO: Store output state in Cover or Machine?
    static String KEY_OUTPUT = "out";

    public  CoverOutput() {
        super();
        AntimatterAPI.register(Cover.class, getId(), this);
    }

    @Override
    public String getId() {
        return "output";
    }

    @Override
    public void onPlace(CoverInstance<?> instance, Direction side) {
        super.onPlace(instance, side);
        instance.getNbt().putBoolean(KEY_OUTPUT, false);
    }

    @Override
    public void onMachineEvent(CoverInstance instance, TileEntityMachine tile, IMachineEvent event) {
        //TODO: Refactor?
        if (event == MachineEvent.ITEMS_OUTPUTTED && instance.getNbt().getBoolean(KEY_OUTPUT)) {
            Direction outputDir = tile.getOutputFacing();
            TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
            if (adjTile == null) return;
            adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
                tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputWrapper(), adjHandler));
            });
        } else if (event == MachineEvent.FLUIDS_OUTPUTTED) {
            Direction outputDir = tile.getOutputFacing();
            TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
            if (adjTile == null) return;
            adjTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
                tile.fluidHandler.ifPresent(h -> Utils.transferFluids(h.getOutputWrapper(), adjHandler));
            });
        }
    }
}
