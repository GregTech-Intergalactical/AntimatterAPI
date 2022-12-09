package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;

import javax.annotation.Nullable;

public class CoverOutput extends CoverInput {

    private boolean ejectItems = false;
    private boolean ejectFluids = false;

    public CoverOutput(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
        if (source.getTile() instanceof TileEntityFakeBlock){
            setEjects(true, true);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (handler.getTile().getLevel().isClientSide) return;
        if (handler.getTile().getLevel().getGameTime() % 100 == 0) {
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
    // @Environment(EnvType.CLIENT)
    public void setEjects(boolean fluid, boolean item) {
        ejectItems = item;
        ejectFluids = fluid;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        this.ejectItems = nbt.getBoolean("ei");
        this.ejectFluids = nbt.getBoolean("ef");
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putBoolean("ei", this.ejectItems);
        nbt.putBoolean("ef", this.ejectFluids);
        return nbt;
    }

    protected void processItemOutput() {
        BlockEntity adjTile = Utils.getTile(handler.getTile().getLevel(), handler.getTile().getBlockPos().relative(this.side));
        if (adjTile == null)
            return;
        TesseractCapUtils.getItemHandler(adjTile, this.side.getOpposite())
                .ifPresent(adjHandler -> {
                    TesseractCapUtils.getItemHandler(handler.getTile(), this.side).ifPresent(h -> Utils.transferItems(h, adjHandler, false));
                });
    }

    protected void processFluidOutput() {
        BlockEntity adjTile = Utils.getTile(handler.getTile().getLevel(), handler.getTile().getBlockPos().relative(this.side));
        if (adjTile == null)
            return;
        TesseractCapUtils.getFluidHandler(adjTile, this.side.getOpposite())
                .ifPresent(adjHandler -> {
                    TesseractCapUtils.getFluidHandler(handler.getTile(), this.side).ifPresent(h -> FluidPlatformUtils.tryFluidTransfer(adjHandler, h, 1000 * TesseractGraphWrappers.dropletMultiplier, true));
                });
    }

    @Override
    public void onGuiEvent(IGuiEvent event, Player player) {
        if (event.getFactory() == GuiEvents.ITEM_EJECT) {
            ejectItems = !ejectItems;
            processItemOutput();
            Utils.markTileForNBTSync(handler.getTile());
        }
        if (event.getFactory() == GuiEvents.FLUID_EJECT) {
            ejectFluids = !ejectFluids;
            processFluidOutput();
            Utils.markTileForNBTSync(handler.getTile());
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
