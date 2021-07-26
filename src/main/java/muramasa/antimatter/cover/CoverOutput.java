package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidUtil;
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
        if (!instance.getNbt().contains(Ref.KEY_MACHINE_EJECT_ITEM))
            instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_ITEM, false);
        if (!instance.getNbt().contains(Ref.KEY_MACHINE_EJECT_FLUID))
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
        if (shouldOutputFluids(instance)) processFluidOutput(instance, (TileEntityMachine<?>) instance.getTile());
        if (shouldOutputItems(instance)) processItemOutput(instance, (TileEntityMachine<?>) instance.getTile());
    }

    public boolean shouldOutputItems(CoverStack<?> instance) {
        return instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_ITEM);
    }

    public boolean shouldOutputFluids(CoverStack<?> instance) {
        return instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_FLUID);
    }

    //TODO: Not even sure if needed.
    //@OnlyIn(Dist.CLIENT)
    public void setEjects(CoverStack<?> instance, boolean fluid,boolean item) {
        instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_ITEM, item);
        instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_FLUID, fluid);
    }

    protected void processItemOutput(CoverStack<?> instance, TileEntityMachine<?> tile) {
        Direction outputDir = instance.getFacing();
        TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
        if (adjTile == null) return;
        adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
            tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputHandler(), adjHandler,false));
        });
    }

    protected void processFluidOutput(CoverStack<?> instance, TileEntityMachine<?> tile) {
        Direction outputDir = instance.getFacing();
        TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
        if (adjTile == null) return;
        adjTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
            tile.fluidHandler.ifPresent(h -> FluidUtil.tryFluidTransfer(adjHandler, h.getOutputTanks(), 1000, true));
        });
    }

    @Override
    public void onGuiEvent(CoverStack<?> instance, IGuiEvent event, PlayerEntity player, int... data) {
        if (event == GuiEvent.ITEM_EJECT) {
            instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_ITEM, !instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_ITEM));
            processItemOutput(instance, (TileEntityMachine<?>) instance.getTile());
            Utils.markTileForNBTSync(instance.getTile());
        }
        if (event == GuiEvent.FLUID_EJECT) {
            instance.getNbt().putBoolean(Ref.KEY_MACHINE_EJECT_FLUID, !instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_FLUID));
            processFluidOutput(instance, (TileEntityMachine<?>) instance.getTile());
            Utils.markTileForNBTSync(instance.getTile());
        }
    }

    @Override
    public void onMachineEvent(CoverStack<?> instance, TileEntityMachine<?> tile, IMachineEvent event, int... data) {
        //TODO: Tesseract stuff?
        if (event == MachineEvent.ITEMS_OUTPUTTED && instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_ITEM)) {
            processItemOutput(instance,tile);
        } else if (event == MachineEvent.FLUIDS_OUTPUTTED && instance.getNbt().getBoolean(Ref.KEY_MACHINE_EJECT_FLUID)) {
            processFluidOutput(instance,tile);
        }
    }
}
