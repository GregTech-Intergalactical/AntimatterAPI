package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class CoverOutput extends CoverInput {

    private boolean ejectItems = false;
    private boolean ejectFluids = false;
    private final TileEntityMachine<?> tile;

    public CoverOutput(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
        this.tile = (TileEntityMachine<?>) source.getTile();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (handler.getTile().getLevel().isClientSide) return;
        if (tile.getLevel().getGameTime() % 100 == 0) {
            if (shouldOutputFluids())
                processFluidOutput();
            if (shouldOutputItems())
                processItemOutput();
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // refresh(instance);
    }

    public void manualOutput() {
        if (shouldOutputFluids())
            processFluidOutput();
        if (shouldOutputItems())
            processItemOutput();
    }

    public boolean shouldOutputItems() {
        return this.ejectItems;
    }

    public boolean shouldOutputFluids() {
        return this.ejectFluids;
    }

    // TODO: Not even sure if needed.
    // @OnlyIn(Dist.CLIENT)
    public void setEjects(boolean fluid, boolean item) {
        ejectItems = item;
        ejectFluids = fluid;
    }

    @Override
    public void deserialize(CompoundNBT nbt) {
        super.deserialize(nbt);
        this.ejectItems = nbt.getBoolean("ei");
        this.ejectFluids = nbt.getBoolean("ef");
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT nbt = super.serialize();
        nbt.putBoolean("ei", this.ejectItems);
        nbt.putBoolean("ef", this.ejectFluids);
        return nbt;
    }

    protected void processItemOutput() {
        TileEntity adjTile = Utils.getTile(tile.getLevel(), tile.getBlockPos().relative(this.side));
        if (adjTile == null)
            return;
        adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.side.getOpposite())
                .ifPresent(adjHandler -> {
                    tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputHandler(), adjHandler, false));
                });
    }

    protected void processFluidOutput() {
        TileEntity adjTile = Utils.getTile(handler.getTile().getLevel(), handler.getTile().getBlockPos().relative(this.side));
        if (adjTile == null)
            return;
        adjTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.side.getOpposite())
                .ifPresent(adjHandler -> {
                    tile.fluidHandler.ifPresent(h -> FluidUtil.tryFluidTransfer(adjHandler, h.getOutputTanks(), 1000, true));
                });
    }

    @Override
    public void onGuiEvent(IGuiEvent event, PlayerEntity player) {
        if (event.getFactory() == GuiEvents.ITEM_EJECT) {
            ejectItems = !ejectItems;
            processItemOutput();
            Utils.markTileForNBTSync(tile);
        }
        if (event.getFactory() == GuiEvents.FLUID_EJECT) {
            ejectFluids = !ejectFluids;
            processFluidOutput();
            Utils.markTileForNBTSync(tile);
        }
    }

    @Override
    public void onMachineEvent(TileEntityMachine<?> tile, IMachineEvent event, int... data) {
        // TODO: Tesseract stuff?
        if (event == MachineEvent.ITEMS_OUTPUTTED && ejectItems) {
            processItemOutput();
        } else if (event == MachineEvent.FLUIDS_OUTPUTTED && ejectFluids) {
            processFluidOutput();
        }
    }
}
