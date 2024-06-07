package muramasa.antimatter.cover;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.blockentity.BlockEntityBase;
import muramasa.antimatter.blockentity.BlockEntityCache;
import muramasa.antimatter.blockentity.BlockEntityFakeBlock;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;

public class CoverOutput extends CoverInput {

    private boolean ejectItems = false;
    private boolean ejectFluids = false;

    private boolean allowInput = false;

    public CoverOutput(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
        if (source.getTile() instanceof BlockEntityFakeBlock){
            setEjects(true, true);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (handler.getTile().getLevel().isClientSide) return;
        if (shouldOutputFluids())
            processFluidOutput();
        if (shouldOutputItems())
            processItemOutput();
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

    public void setEjects(boolean fluid, boolean item) {
        ejectItems = item;
        ejectFluids = fluid;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        this.ejectItems = nbt.getBoolean("ei");
        this.ejectFluids = nbt.getBoolean("ef");
        this.allowInput = nbt.getBoolean("ai");
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putBoolean("ei", this.ejectItems);
        nbt.putBoolean("ef", this.ejectFluids);
        nbt.putBoolean("ai", allowInput);
        return nbt;
    }

    @Override
    public InteractionResult onInteract(Player player, InteractionHand hand, Direction side, @org.jetbrains.annotations.Nullable AntimatterToolType type) {
        if (type != null && type.getTag() == AntimatterDefaultTools.SCREWDRIVER.getTag()){
            allowInput = !allowInput;
            String suffix = allowInput ? "allow" : "no";
            player.sendMessage(Utils.translatable("antimatter.tooltip.cover.output." + suffix + "_input"), player.getUUID());
            return InteractionResult.SUCCESS;
        }
        return super.onInteract(player, hand, side, type);
    }

    public boolean doesAllowInput() {
        return allowInput;
    }

    int processing = 0;
    protected void processItemOutput() {
        BlockEntity adjTile;
        if (handler.getTile() instanceof BlockEntityBase<?> base){
            adjTile = base.getCachedBlockEntity(this.side);
        } else {
            adjTile = handler.getTile().getLevel().getBlockEntity(handler.getTile().getBlockPos().relative(this.side));
        }
        if (adjTile == null)
            return;
        if (processing > 0) return;
        processing++;
        TesseractCapUtils.INSTANCE.getItemHandler(adjTile, this.side.getOpposite())
                .ifPresent(adjHandler -> {
                    TesseractCapUtils.INSTANCE.getItemHandler(handler.getTile(), this.side).ifPresent(h -> Utils.transferItems(h, adjHandler, false, i -> {
                        return !(this.handler.getTile() instanceof BlockEntityMachine<?> machine) || machine.itemHandler.map(f -> f.canItemBeAutoOutput(i)).orElse(true);
                    }));
                });
        processing--;
    }

    protected void processFluidOutput() {
        BlockEntity adjTile;
        if (handler.getTile() instanceof BlockEntityBase<?> base){
            adjTile = base.getCachedBlockEntity(this.side);
        } else {
            adjTile = handler.getTile().getLevel().getBlockEntity(handler.getTile().getBlockPos().relative(this.side));
        }
        if (processing > 0) return;
        processing++;
        FluidHooks.safeGetBlockFluidManager(adjTile, this.side.getOpposite())
                .ifPresent(adjHandler -> {
                    FluidHooks.safeGetBlockFluidManager(handler.getTile(), this.side).ifPresent(h -> tryFluidTransfer(adjHandler, h, Integer.MAX_VALUE * TesseractGraphWrappers.dropletMultiplier, true));
                });
        processing--;
    }

    public void tryFluidTransfer(PlatformFluidHandler fluidDestination, PlatformFluidHandler fluidSource, long maxAmount, boolean doTransfer) {
        for (int i = 0; i < fluidSource.getTankAmount(); i++) {
            FluidHolder fluid = fluidSource.getFluidInTank(i);
            if (this.handler.getTile() instanceof BlockEntityMachine<?> machine && machine.fluidHandler.map(f -> !f.canFluidBeAutoOutput(fluid)).orElse(false)){
                continue;
            }
            FluidPlatformUtils.INSTANCE.tryFluidTransfer(fluidDestination, fluidSource, fluid.copyWithAmount(Math.min(fluid.getFluidAmount(), maxAmount)), doTransfer);
        }
    }

    @Override
    public void onGuiEvent(IGuiEvent event, Player player) {
        if (event.getFactory() == GuiEvents.ITEM_EJECT) {
            ejectItems = !ejectItems;
            if (ejectItems) processItemOutput();
            Utils.markTileForNBTSync(handler.getTile());
        }
        if (event.getFactory() == GuiEvents.FLUID_EJECT) {
            ejectFluids = !ejectFluids;
            if (ejectFluids) processFluidOutput();
            Utils.markTileForNBTSync(handler.getTile());
        }
    }

    @Override
    public void onMachineEvent(IGuiHandler tile, IMachineEvent event, int... data) {
        if (event == MachineEvent.ITEMS_OUTPUTTED && ejectItems) {
            processItemOutput();
        } else if (event == MachineEvent.FLUIDS_OUTPUTTED && ejectFluids) {
            processFluidOutput();
        }
    }

    @Override
    public <T> boolean blocksInput(Class<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        return !allowInput;
    }
}
