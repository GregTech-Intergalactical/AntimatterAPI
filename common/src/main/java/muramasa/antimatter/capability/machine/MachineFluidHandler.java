package muramasa.antimatter.capability.machine;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.fluid.FluidHandlerNullSideWrapper;
import muramasa.antimatter.capability.fluid.FluidHandlerSidedWrapper;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import tesseract.FluidPlatformUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;
import static muramasa.antimatter.machine.MachineFlag.GUI;

public class MachineFluidHandler<T extends BlockEntityMachine<T>> extends FluidHandler<T> implements Dispatch.Sided<FluidContainer> {

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
        this(tile, 32000, 1000 * (250 + tile.getMachineTier().getIntegerId()));
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
                boolean success = false;
                Predicate<ItemStack> predicate = s -> MachineItemHandler.insertIntoOutput(ih.getCellOutputHandler(), cellSlot, s, true).isEmpty();
                Consumer<ItemStack> consumer = s -> {
                    MachineItemHandler.insertIntoOutput(ih.getCellOutputHandler(), cellSlot, s, false);
                    MachineItemHandler.extractFromInput(ih.getCellInputHandler(), cellSlot, 1, false);
                };
                if (FluidPlatformUtils.fillItemFromContainer(Utils.ca(1, cell), this.getCellAccessibleTanks(), predicate, consumer)){
                    success = true;
                    lastCellSlot = cellSlot;
                } else if (FluidPlatformUtils.emptyItemIntoContainer(Utils.ca(1, cell), this.getCellAccessibleTanks(), predicate, consumer)){
                    success = true;
                    lastCellSlot = cellSlot;
                }
                return success;
            }).orElse(false);
        } else {
            filledLastTick = false;
        }
        fillingCell = false;
    }

    protected FluidTanks getCellAccessibleTanks(){
        return this.getAllTanks();
    }

    protected boolean checkValidFluid(FluidHolder fluid) {
        if (tile.has(GENERATOR)) {
            IRecipe recipe = tile.getMachineType().getRecipeMap(tile.getMachineTier()).find(new ItemStack[0], new FluidHolder[]{fluid}, Tier.ULV, r -> true);
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
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        if (!tile.recipeHandler.map(t -> t.accepts(fluid)).orElse(true)) return 0;
        return super.insertFluid(fluid, simulate);
    }

    @Override
    public FluidContainer copy() {
        return this;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        super.onMachineEvent(event, data);
        if (event instanceof SlotType<?>) {
            if (event == SlotType.CELL_IN || event == SlotType.CELL_OUT) {
                if (data[0] instanceof Integer) tryFillCell((Integer) data[0], -1);
            } else if (event == SlotType.FL_IN || event == SlotType.FL_OUT) {
                if (data[0] instanceof Integer) tryFillCell((Integer) data[0], -1);
                if (this.tile.getMachineType().renderContainerLiquids()) {
                    tile.sidedSync(true);
                }
            }
        }
    }

    public boolean canOutputsFit(FluidHolder[] outputs) {
        return getSpaceForOutputs(outputs) >= outputs.length;
    }

    public int getSpaceForOutputs(FluidHolder[] outputs) {
        int matchCount = 0;
        if (getOutputTanks() != null) {
            for (FluidHolder output : outputs) {
                if (fillOutput(output, true) == output.getFluidAmount()) {
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    public void addOutputs(FluidHolder... fluids) {
        if (getOutputTanks() == null) {
            return;
        }
        if (fluids != null) {
            for (FluidHolder input : fluids) {
                fillOutput(input, false);
            }
        }
    }

    public int getTankForTag(TagKey<Fluid> tag, int min) {
        FluidHolder[] inputs = this.getInputs();
        for (int i = min; i < inputs.length; i++) {
            FluidHolder input = inputs[i];
            if (input.getFluid().builtInRegistryHolder().is(tag)) {
                return i;
            }
        }
        return -1;
    }

    @NotNull
    public FluidHolder consumeTaggedInput(TagKey<Fluid> input, long amount, boolean simulate) {
        FluidTanks inputs = getInputTanks();
        if (inputs == null) {
            return FluidHooks.emptyFluid();
        }
        int id = getTankForTag(input, 0);
        if (id == -1) return FluidHooks.emptyFluid();
        return inputs.extractFluid(FluidHooks.newFluidHolder(inputs.getFluidInTank(id).getFluid(), amount, null), simulate);
    }

    @NotNull
    public List<FluidHolder> consumeAndReturnInputs(List<FluidIngredient> inputs, boolean simulate) {
        if (getInputTanks() == null) {
            return Collections.emptyList();
        }
        List<FluidHolder> consumed = new ObjectArrayList<>();
        List<FluidIngredient> fluidIngredients = new ObjectArrayList<>();
        if (inputs != null) {
            for (FluidIngredient input : inputs) {
                List<FluidHolder> inner = input.drain(this, true, true);
                if (inner.stream().mapToLong(FluidHolder::getFluidAmount).sum() != input.getAmount()) {
                    return Collections.emptyList();
                } else {
                    fluidIngredients.add(input);
                    consumed.addAll(inner);
                }
            }
        }
        if (!simulate){
            fluidIngredients.forEach(f -> f.drain(this, true, false));
        }
        return consumed;
    }

    public FluidHolder[] exportAndReturnOutputs(FluidHolder... outputs) {
        if (getOutputTanks() == null) {
            return new FluidHolder[0];
        }
        List<FluidHolder> notExported = new ObjectArrayList<>();
        long result;
        for (int i = 0; i < outputs.length; i++) {
            result = insertFluid(outputs[i], false);
            if (result == 0) notExported.add(outputs[i]); //Valid space was not found
            else outputs[i] = Utils.ca(result, outputs[i]); //Fluid was partially exported
        }
        return notExported.toArray(new FluidHolder[0]);
    }

    @Override
    public boolean canOutput(Direction direction) {
        if (tile.getFacing().get3DDataValue() == direction.get3DDataValue() && !tile.getMachineType().allowsFrontIO())
            return false;
        return super.allowsExtraction();
    }

    @Override
    public boolean canInput(FluidHolder fluid, Direction direction) {
        return true;
    }

    @Override
    public boolean canInput(Direction direction) {
        if (tile.getFacing().get3DDataValue() == direction.get3DDataValue() && !tile.getMachineType().allowsFrontIO())
            return false;
        return super.allowsInsertion();
    }

    @Override
    public int getPriority(Direction direction) {
        return tile.coverHandler.map(c -> c.get(direction).getPriority(FluidContainer.class)).orElse(0);
    }

    @Override
    public Optional<? extends FluidContainer> forNullSide() {
        return Optional.of(new FluidHandlerNullSideWrapper(this));
    }

    @Override
    public Optional<FluidContainer> forSide(Direction side) {
        return Optional.of(new FluidHandlerSidedWrapper(this, tile.coverHandler.map(c -> c).orElse(null), side));
    }

    public PlatformFluidHandler getGuiHandler() {
        return new PlatformFluidHandler() {
            @Override
            public long insertFluid(FluidHolder fluid, boolean simulate) {
                return MachineFluidHandler.this.insertFluid(fluid, simulate);
            }

            @Override
            public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
                return MachineFluidHandler.this.extractFluid(fluid, simulate);
            }

            @Override
            public int getTankAmount() {
                return MachineFluidHandler.this.getSize();
            }

            @NotNull
            @Override
            public FluidHolder getFluidInTank(int tank) {
                return MachineFluidHandler.this.getFluidInTank(tank);
            }

            @Override
            public List<FluidHolder> getFluidTanks() {
                return MachineFluidHandler.this.getFluids();
            }

            @Override
            public long getTankCapacity(int tank) {
                return MachineFluidHandler.this.getTankCapacity(tank);
            }

            @Override
            public boolean supportsInsertion() {
                return MachineFluidHandler.this.allowsInsertion();
            }

            @Override
            public boolean supportsExtraction() {
                return MachineFluidHandler.this.allowsExtraction();
            }
        };
    }
}
