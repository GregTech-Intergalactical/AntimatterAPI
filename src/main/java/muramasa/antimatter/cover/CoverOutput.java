package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class CoverOutput extends CoverInput {

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
        instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_ITEM, false);
        instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_FLUID, false);
  //      refresh(instance);
    }

    @Override
    public void onUpdate(CoverStack<?> instance, Direction side) {
        super.onUpdate(instance, side);
        if (instance.getTile().getWorld().getGameTime() % 100 == 0) {
            if (shouldOutputFluids(instance)) processFluidOutput(instance, (TileEntityMachine) instance.getTile());
            if (shouldOutputItems(instance)) processItemOutput(instance, (TileEntityMachine) instance.getTile());
        }
    }

    @Override
    public void onRemove(CoverStack<?> instance, Direction side) {
        super.onRemove(instance,side);
        //refresh(instance);
    }

    public void manualOutput(CoverStack<?> instance) {
        if (shouldOutputFluids(instance)) processFluidOutput(instance, (TileEntityMachine) instance.getTile());
        if (shouldOutputItems(instance)) processItemOutput(instance, (TileEntityMachine) instance.getTile());
    }

    public boolean shouldOutputItems(CoverStack<?> instance) {
        return instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_ITEM);
    }

    public boolean shouldOutputFluids(CoverStack<?> instance) {
        return instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_FLUID);
    }

    public void refresh(CoverStack<?> instance) {
        super.refresh(instance);
    }
    //TODO: Not even sure if needed.
    @OnlyIn(Dist.CLIENT)
    public void setEjects(CoverStack<?> instance, boolean fluid,boolean item) {
        instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_ITEM, item);
        instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_FLUID, fluid);
    }

    protected void processItemOutput(CoverStack<?> instance, TileEntityMachine tile) {
        Direction outputDir = tile.getOutputFacing();
        TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
        if (adjTile == null) return;
        adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
            tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputHandler(), adjHandler,false));
        });
    }

    protected void processFluidOutput(CoverStack<?> instance, TileEntityMachine tile) {
        Direction outputDir = tile.getOutputFacing();
        TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
        if (adjTile == null) return;
        adjTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
            tile.fluidHandler.ifPresent(h -> Utils.transferFluids(h.getOutputTanks(), adjHandler));
        });
    }

    @Override
    public void onMachineEvent(CoverStack<?> instance, TileEntityMachine tile, IMachineEvent event, int... data) {
        if (event == GuiEvent.ITEM_EJECT) {
            instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_ITEM, !instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_ITEM));
            processItemOutput(instance,tile);
            return;
        }
        if (event == GuiEvent.FLUID_EJECT) {
            instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_FLUID, !instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_FLUID));
            processFluidOutput(instance,tile);
            return;
        }
        //TODO: Tesseract stuff?
        if (event == MachineEvent.ITEMS_OUTPUTTED && instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_ITEM)) {
            processItemOutput(instance,tile);
        } else if (event == MachineEvent.FLUIDS_OUTPUTTED && instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_FLUID)) {
            processFluidOutput(instance,tile);
        }
    }
}
