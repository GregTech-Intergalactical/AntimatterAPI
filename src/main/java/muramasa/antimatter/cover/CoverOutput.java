package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.api.IRefreshable;
import tesseract.api.gt.IGTNode;

public class CoverOutput extends CoverInput {

    //TODO: Store output state in Cover or Machine?
    static String KEY_OUTPUT = "out";

    public CoverOutput() {
        super();
    }

    @Override
    public String getId() {
        return "output";
    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
        instance.getNbt().putBoolean(KEY_OUTPUT, false);
        refresh(instance);
    }

    @Override
    public void onRemove(CoverStack<?> instance, Direction side) {
        super.onRemove(instance,side);
        refresh(instance);
    }

    @Override
    public void onMachineEvent(CoverStack<?> instance, TileEntityMachine tile, IMachineEvent event, int... data) {
        //TODO: Refactor? <- YES!
        if (event == GuiEvent.ITEM_EJECT) {
            instance.getNbt().putBoolean(KEY_OUTPUT, !instance.getNbt().getBoolean(KEY_OUTPUT));
            return;
        }
        if (event == MachineEvent.ITEMS_OUTPUTTED && instance.getNbt().getBoolean(KEY_OUTPUT)) {
            Direction outputDir = tile.getOutputFacing();
            TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
            if (adjTile == null) return;
            adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
                tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputHandler(), adjHandler));
            });
        } else if (event == MachineEvent.FLUIDS_OUTPUTTED) {
            Direction outputDir = tile.getOutputFacing();
            TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
            if (adjTile == null) return;
            adjTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
                tile.fluidHandler.ifPresent(h -> Utils.transferFluids(h.getOutputTanks(), adjHandler));
            });
        }
    }
}
