package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.fluid.FluidHandlerNullSideWrapper;
import muramasa.antimatter.capability.fluid.FluidHandlerSidedWrapper;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;
import static muramasa.antimatter.machine.MachineFlag.GUI;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class MachineFluidHandler<T extends TileEntityMachine<T>> extends FluidHandler<T> implements Dispatch.Sided<IFluidHandler> {

    private boolean fillingCell = false;
    protected boolean filledLastTick = false;
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

    public void fillCell(int cellSlot, long maxFill) {
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
                return TesseractCapUtils.getFluidHandlerItem(toActOn).map(cfh -> {
                    final long actualMax = maxFill == -1 ? cfh.getTankCapacityInDroplets(0) : maxFill;
                    ItemStack checkContainer = TesseractCapUtils.getFluidHandlerItem(toActOn).map(t -> {
                        if (t.getFluidInTank(0).isEmpty()) {
                            t.fillDroplets(FluidPlatformUtils.tryFluidTransfer(t, this.getCellAccessibleTanks(), (AntimatterPlatformUtils.isFabric() ? actualMax : (int) actualMax), false), EXECUTE);
                        } else {
                            t.drain(actualMax, EXECUTE);
                        }
                        return t.getContainer();
                    }).orElse(null/* throw exception */);
                    if (!MachineItemHandler.insertIntoOutput(ih.getCellOutputHandler(), cellSlot, checkContainer, true).isEmpty())
                        return false;

                    FluidStack stack;
                    if (cfh.getFluidInTank(0).isEmpty()) {
                        stack = FluidPlatformUtils.tryFluidTransfer(cfh, this.getCellAccessibleTanks(), (AntimatterPlatformUtils.isFabric() ? actualMax : (int) actualMax), true);
                    } else {
                        stack = FluidPlatformUtils.tryFluidTransfer(this.getCellAccessibleTanks(), cfh, (AntimatterPlatformUtils.isFabric() ? actualMax : (int) actualMax), true);
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

    protected FluidTanks getCellAccessibleTanks(){
        return this.getAllTanks();
    }

    protected boolean checkValidFluid(FluidStack fluid) {
        if (tile.has(GENERATOR)) {
            IRecipe recipe = tile.getMachineType().getRecipeMap().find(new ItemStack[0], new FluidStack[]{fluid}, Tier.ULV, r -> true);
            if (recipe != null) {
                return true;
            }
        }
        return true;
    }

    protected void tryFillCell(int slot, long maxFill) {
        if (tile.itemHandler.map(MachineItemHandler::getCellCount).orElse(0) > 0) {
            fillCell(slot, maxFill);
        }
    }

    @Override
    public long fillDroplets(FluidStack stack, FluidAction action) {
        if (!tile.recipeHandler.map(t -> t.accepts(stack)).orElse(true)) return 0;
        return super.fillDroplets(stack, action);
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
                if (fillOutput(output, SIMULATE) == output.getRealAmount()) {
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

    public int getTankForTag(TagKey<Fluid> tag, int min) {
        FluidStack[] inputs = this.getInputs();
        for (int i = min; i < inputs.length; i++) {
            FluidStack input = inputs[i];
            if (input.getFluid().builtInRegistryHolder().is(tag)) {
                return i;
            }
        }
        return -1;
    }

    @Nonnull
    public FluidStack consumeTaggedInput(TagKey<Fluid> input, int amount, boolean simulate) {
        FluidTanks inputs = getInputTanks();
        if (inputs == null) {
            return FluidStack.EMPTY;
        }
        int id = getTankForTag(input, 0);
        if (id == -1) return FluidStack.EMPTY;
        return inputs.drain(new FluidStack(inputs.getFluidInTank(id).getFluid(), amount), simulate ? SIMULATE : EXECUTE);
    }

    @Nonnull
    public List<FluidStack> consumeAndReturnInputs(List<FluidIngredient> inputs, boolean simulate) {
        if (getInputTanks() == null) {
            return Collections.emptyList();
        }
        List<FluidStack> consumed = new ObjectArrayList<>();
        boolean ret = true;
        if (inputs != null) {
            for (FluidIngredient input : inputs) {
                List<FluidStack> inner = input.drain(this, true, simulate);
                if (inner.stream().mapToLong(FluidStack::getRealAmount).sum() != input.getAmount()) {
                    ret = false;
                } else {
                    consumed.addAll(inner);
                }
            }
        }
        return consumed;
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        if (getOutputTanks() == null) {
            return new FluidStack[0];
        }
        List<FluidStack> notExported = new ObjectArrayList<>();
        long result;
        for (int i = 0; i < outputs.length; i++) {
            result = fillDroplets(outputs[i], EXECUTE);
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
            public long getTankCapacityInDroplets(int tank) {
                return MachineFluidHandler.this.getTankCapacityInDroplets(tank);
            }

            @Override
            public long fillDroplets(FluidStack stack, FluidAction action) {
                return MachineFluidHandler.this.fillDroplets(stack, action);
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
                return MachineFluidHandler.this.fill(resource, action);
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                FluidStack ret = MachineFluidHandler.this.drain(resource, action);
                return ret.isEmpty() ? MachineFluidHandler.this.drainInput(resource, action) : ret;
            }

            @Override
            public FluidStack drain(int amount, FluidAction action) {
                return drain((long) amount * TesseractGraphWrappers.dropletMultiplier, action);
            }

            @Nonnull
            @Override
            public FluidStack drain(long maxDrain, FluidAction action) {
                FluidStack ret = MachineFluidHandler.this.drain(maxDrain, action);
                return ret.isEmpty() ? MachineFluidHandler.this.drainInput(maxDrain, action) : ret;
            }
        };
    }
}
