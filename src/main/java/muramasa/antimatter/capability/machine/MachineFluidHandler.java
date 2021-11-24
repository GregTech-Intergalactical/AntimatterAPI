package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.fluid.FluidHandlerNullSideWrapper;
import muramasa.antimatter.capability.fluid.FluidHandlerSidedWrapper;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.Tesseract;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;
import static muramasa.antimatter.machine.MachineFlag.GUI;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class MachineFluidHandler<T extends TileEntityMachine<T>> extends FluidHandler<T> implements Dispatch.Sided<IFluidHandler> {

    private boolean fillingCell = false;
    private boolean filledLastTick = false;
    private int lastCellSlot = 0;

    public MachineFluidHandler(T tile, int capacity, int pressure) {
        this(tile, capacity, pressure, tile.has(GUI) ? tile.getMachineType().getSlots(SlotType.FL_IN, tile.getMachineTier()).size() : 0,
                tile.has(GUI) ? tile.getMachineType().getSlots(SlotType.FL_OUT, tile.getMachineTier()).size() : 0);
    }

    public MachineFluidHandler(T tile, int capacity, int pressure, int inputCount, int outputCount) {
        super(tile, capacity, pressure, inputCount, outputCount);
    }

    public MachineFluidHandler(T tile) {
        this(tile, 8000 * (1 + tile.getMachineTier().getIntegerId()), 1000 * (250 + tile.getMachineTier().getIntegerId()));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (filledLastTick) {
            tryFillCell(lastCellSlot, -1);
        }
    }

    public void fillCell(int cellSlot, int maxFill) {
        if (fillingCell) return;
        fillingCell = true;
        if (getInputTanks() != null) {
            filledLastTick = tile.itemHandler.map(ih -> {
                if (ih.getCellInputHandler() == null) {
                    return false;
                }
                ItemStack cell = ih.getCellInputHandler().getStackInSlot(cellSlot);
                if (cell.isEmpty()) {
                    return false;
                }
                ItemStack toActOn = cell.copy();
                toActOn.setCount(1);
                return toActOn.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(cfh -> {
                    final int actualMax = maxFill == -1 ? cfh.getTankCapacity(0) : maxFill;
                    ItemStack checkContainer = toActOn.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(t -> {
                        if (t.getFluidInTank(0).isEmpty()) {
                            t.fill(FluidUtil.tryFluidTransfer(t, this.getAllTanks(), actualMax, false), EXECUTE);
                        } else {
                            t.drain(actualMax, EXECUTE);
                        }
                        return t.getContainer();
                    }).orElse(null/* throw exception */);
                    if (!MachineItemHandler.insertIntoOutput(ih.getCellOutputHandler(), cellSlot, checkContainer, true).isEmpty())
                        return false;

                    FluidStack stack;
                    if (cfh.getFluidInTank(0).isEmpty()) {
                        stack = FluidUtil.tryFluidTransfer(cfh, this.getAllTanks(), actualMax, true);
                    } else {
                        stack = FluidUtil.tryFluidTransfer(this.getAllTanks(), cfh, actualMax, true);
                    }
                    if (!stack.isEmpty()) {
                        ItemStack insert = cfh.getContainer();
                        insert.setCount(1);
                        MachineItemHandler.insertIntoOutput(ih.getCellOutputHandler(), cellSlot, insert, false);
                        MachineItemHandler.extractFromInput(ih.getCellInputHandler(), cellSlot, 1, false);
                        lastCellSlot = cellSlot;
                        return true;
                    }
                    return false;
                }).orElse(false);
            }).orElse(false);
        } else {
            filledLastTick = false;
        }
        fillingCell = false;
    }

    protected boolean checkValidFluid(FluidStack fluid) {
        if (tile.has(GENERATOR)) {
            Recipe recipe = tile.getMachineType().getRecipeMap().find(new ItemStack[0], new FluidStack[]{fluid}, r -> true);
            if (recipe != null) {
                return true;
            }
        }
        return true;
    }

    protected void tryFillCell(int slot, int maxFill) {
        if (tile.itemHandler.map(MachineItemHandler::getCellCount).orElse(0) > 0) {
            fillCell(slot, maxFill);
        }
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        if (!tile.recipeHandler.map(t -> t.accepts(stack)).orElse(true)) return 0;
        return super.fill(stack, action);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        super.onMachineEvent(event, data);
        if (event instanceof ContentEvent) {
            switch ((ContentEvent) event) {
                case ITEM_CELL_CHANGED:
                    if (data[0] instanceof Integer) tryFillCell((Integer) data[0], -1);
                    break;
                case FLUID_INPUT_CHANGED:
                case FLUID_OUTPUT_CHANGED:
                    if (data[0] instanceof Integer) tryFillCell((Integer) data[0], -1);
                    if (this.tile.getMachineType().renderContainerLiquids()) {
                        tile.sidedSync(true);
                    }
                    break;
            }
        }
    }

    public boolean canOutputsFit(FluidStack[] outputs) {
        return getSpaceForOutputs(outputs) >= outputs.length;
    }

    public int getSpaceForOutputs(FluidStack[] outputs) {
        int matchCount = 0;
        if (getOutputTanks() != null) {
            for (FluidStack output : outputs) {
                if (fillOutput(output, SIMULATE) == output.getAmount()) {
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    public void addOutputs(FluidStack... fluids) {
        if (getOutputTanks() == null) {
            return;
        }
        if (fluids != null) {
            for (FluidStack input : fluids) {
                fillOutput(input, EXECUTE);
            }
        }
    }

    @Nonnull
    public List<FluidStack> consumeAndReturnInputs(List<FluidStack> inputs, boolean simulate) {
        if (getInputTanks() == null) {
            return Collections.emptyList();
        }
        List<FluidStack> notConsumed = new ObjectArrayList<>();
        FluidStack result;
        if (inputs != null) {
            for (FluidStack input : inputs) {
                result = drainInput(input, simulate ? SIMULATE : EXECUTE);
                if (result != FluidStack.EMPTY) {
                    if (result.getAmount() != input.getAmount()) { //Fluid was partially consumed
                        notConsumed.add(Utils.ca(input.getAmount() - result.getAmount(), input));
                    }
                } else {
                    notConsumed.add(input); //Fluid not present in input tanks
                }
            }
        }
        return notConsumed;
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        if (getOutputTanks() == null) {
            return new FluidStack[0];
        }
        List<FluidStack> notExported = new ObjectArrayList<>();
        int result;
        for (int i = 0; i < outputs.length; i++) {
            result = fill(outputs[i], EXECUTE);
            if (result == 0) notExported.add(outputs[i]); //Valid space was not found
            else outputs[i] = Utils.ca(result, outputs[i]); //Fluid was partially exported
        }
        return notExported.toArray(new FluidStack[0]);
    }

    @Override
    public boolean canOutput(Direction direction) {
        if (tile.getFacing().get3DDataValue() == direction.get3DDataValue() && !tile.getMachineType().allowsFrontCovers())
            return false;
        return super.canOutput();
    }

    @Override
    public boolean canInput(FluidStack fluid, Direction direction) {
        return true;
    }

    @Override
    public boolean canInput(Direction direction) {
        if (tile.getFacing().get3DDataValue() == direction.get3DDataValue() && !tile.getMachineType().allowsFrontCovers())
            return false;
        return super.canInput();
    }


    @Override
    public LazyOptional<? extends IFluidHandler> forNullSide() {
        return LazyOptional.of(() -> new FluidHandlerNullSideWrapper(this));
    }

    @Override
    public LazyOptional<IFluidHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new FluidHandlerSidedWrapper(this, tile.coverHandler.map(c -> c).orElse(null), side));
    }

    @Override
    public void refresh() {
        Tesseract.FLUID.refreshNode(tile.getLevel(), tile.getBlockPos().asLong());
    }

    public IFluidHandler getGuiHandler() {
        return new IFluidHandler() {

            @Override
            public int getTanks() {
                return MachineFluidHandler.this.getTanks();
            }

            @Nonnull
            @Override
            public FluidStack getFluidInTank(int tank) {
                return MachineFluidHandler.this.getFluidInTank(tank);
            }

            @Override
            public int getTankCapacity(int tank) {
                return MachineFluidHandler.this.getTankCapacity(tank);
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return MachineFluidHandler.this.isFluidValid(tank, stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                int ret = MachineFluidHandler.this.fill(resource, action);
                return ret;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                FluidStack ret = MachineFluidHandler.this.drain(resource, action);
                return ret.isEmpty() ? MachineFluidHandler.this.drainInput(resource, action) : ret;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                FluidStack ret = MachineFluidHandler.this.drain(maxDrain, action);
                return ret.isEmpty() ? MachineFluidHandler.this.drainInput(maxDrain, action) : ret;
            }
        };
    }
}
