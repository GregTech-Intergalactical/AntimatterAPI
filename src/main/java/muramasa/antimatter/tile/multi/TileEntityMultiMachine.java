package muramasa.antimatter.tile.multi;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MultiMachineEnergyHandler;
import muramasa.antimatter.capability.machine.MultiMachineFluidHandler;
import muramasa.antimatter.capability.machine.MultiMachineItemHandler;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TileEntityMultiMachine<T extends TileEntityMultiMachine<T>> extends TileEntityBasicMultiMachine<T> implements IInfoRenderer<InfoRenderWidget.MultiRenderWidget> {

    protected long EUt;

    //TODO: Sync multiblock state(if it is formed), otherwise the textures might bug out. Not a big deal.
    public TileEntityMultiMachine(Machine<?> type) {
        super(type);
        if (type.has(ITEM) || type.has(CELL)) {
            itemHandler.set(() -> new MultiMachineItemHandler<>((T) this));
        }
        if (type.has(ENERGY)) {
            energyHandler.set(() -> new MultiMachineEnergyHandler<>((T) this));
        }
        if (type.has(FLUID)) {
            fluidHandler.set(() -> new MultiMachineFluidHandler<>((T) this));
        }
    }

    @Override
    public Tier getPowerLevel() {
        return energyHandler.map(t -> ((MultiMachineEnergyHandler<T>) t).getAccumulatedPower()).orElse(super.getPowerLevel());
    }

    @Override
    public void afterStructureFormed() {
        this.result.components.forEach((k, v) -> v.forEach(c -> {
            c.onStructureFormed(this);
        }));
        //Handlers.
        this.itemHandler.ifPresent(handle -> {
            ((MultiMachineItemHandler<T>) handle).onStructureBuild();
        });
        this.energyHandler.ifPresent(handle -> {
            ((MultiMachineEnergyHandler<T>) handle).onStructureBuild();
        });
        this.fluidHandler.ifPresent(handle -> {
            ((MultiMachineFluidHandler<T>) handle).onStructureBuild();
        });
    }

    public void onStructureInvalidated() {
        this.result.components.forEach((k, v) -> v.forEach(c -> {
            c.onStructureInvalidated(this);
        }));
        this.itemHandler.ifPresent(handle -> ((MultiMachineItemHandler<T>) handle).invalidate());
        this.energyHandler.ifPresent(handle -> ((MultiMachineEnergyHandler<T>) handle).invalidate());
        this.fluidHandler.ifPresent(handle -> ((MultiMachineFluidHandler<T>) handle).invalidate());
    }

    @Override
    public void onGuiEvent(IGuiEvent event, PlayerEntity playerEntity) {
        super.onGuiEvent(event, playerEntity);
        /*if (event == GuiEvent.MULTI_ACTIVATE) {
            checkStructure();
            recipeHandler.ifPresent(MachineRecipeHandler::checkRecipe);
        }*/
    }

    /**
     * Returns list of items across all input hatches. Merges equal filters empty
     **/
    public ItemStack[] getStoredItems() {
        if (!has(MachineFlag.ITEM)) return new ItemStack[0];
        List<ItemStack> all = new ObjectArrayList<>();
        for (IComponentHandler hatch : getComponents("hatch_item_input")) {
            hatch.getItemHandler().ifPresent(h -> Utils.mergeItems(all, h.getInputList()));
        }
        System.out.println(all.toString());
        return all.toArray(new ItemStack[0]);
    }

    /**
     * Returns list of fluids across all input hatches. Merges equal filters empty
     **/
    public FluidStack[] getStoredFluids() {
        if (!has(MachineFlag.FLUID)) return new FluidStack[0];
        List<FluidStack> all = new ObjectArrayList<>();
        for (IComponentHandler hatch : getComponents("hatch_fluid_input")) {
            hatch.getFluidHandler().ifPresent(h -> Utils.mergeFluids(all, Arrays.asList(h.getInputs())));
        }
        System.out.println(all.toString());
        return all.toArray(new FluidStack[0]);
    }

    /**
     * Returns the total energy stored across all energy hatches
     **/
    public long getStoredEnergy() {
        long total = 0;
        for (IComponentHandler hatch : getComponents("hatch_energy")) {
            if (hatch.getEnergyHandler().isPresent())
                total += hatch.getEnergyHandler().map(MachineEnergyHandler::getEnergyStored).orElse(0);
        }
        return total;
    }

    /**
     * Consumes inputs from all input hatches. Assumes Utils.doItemsMatchAndSizeValid has been used
     **/
    public void consumeItems(ItemStack[] items) {
        if (items == null) return;
        for (IComponentHandler hatch : getComponents("hatch_item_input")) {
            if (hatch.getItemHandler().isPresent()) {
                ItemStack[] finalItems = items;
                items = hatch.getItemHandler().map(ih -> ih.consumeAndReturnInputs(finalItems.clone())).orElse(new ItemStack[0]);
                if (items.length == 0) break;
            }
        }
        if (items.length > 0) System.out.println("DID NOT CONSUME ALL: " + Arrays.toString(items));
    }

    /**
     * Consumes inputs from all input hatches. Assumes Utils.doFluidsMatchAndSizeValid has been used
     **/
    public void consumeFluids(FluidStack[] inp) {
        if (inp == null) return;
        List<FluidStack> fluids = Arrays.asList(inp);
        if (fluids.size() == 0) return;
        for (IComponentHandler hatch : getComponents("hatch_fluid_input")) {
            if (hatch.getFluidHandler().isPresent()) {
                List<FluidStack> finalFluids = fluids;
                fluids = hatch.getFluidHandler().map(fh -> fh.consumeAndReturnInputs(finalFluids, false)).orElse(Collections.emptyList());
                if (fluids.size() == 0) break;
            }
        }
        if (fluids.size() > 0) System.out.println("DID NOT CONSUME ALL: " + Arrays.toString(fluids.toArray()));
    }

    /**
     * Consumes energy from all energy hatches. Assumes enough energy is present in hatches
     **/
    public void consumeEnergy(long energy) {
        if (energy <= 0) return;
        for (IComponentHandler hatch : getComponents("hatch_energy")) {
            if (hatch.getEnergyHandler().isPresent()) {
                long finalEnergy = energy;
                energy -= hatch.getEnergyHandler().map(eh -> eh.extract(finalEnergy, false)).orElse(0L);
                if (energy == 0) break;
            }
        }
    }

    /**
     * Export items to hatches regardless of space. Assumes canOutputsFit has been used
     **/
    public void outputItems(ItemStack[] items) {
        if (items == null) return;
        for (IComponentHandler hatch : getComponents("hatch_item_output")) {
            if (hatch.getItemHandler().isPresent()) {
                ItemStack[] finalItems = items;
                items = hatch.getItemHandler().map(ih -> ih.exportAndReturnOutputs(finalItems.clone())).orElse(new ItemStack[0]); //WHY CLONE?!!?
                if (items.length == 0) break;
            }
        }
        if (items.length > 0) System.out.println("HATCH OVERFLOW: " + Arrays.toString(items));
    }

    /**
     * Export fluids to hatches regardless of space. Assumes canOutputsFit has been used
     **/
    public void outputFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
            if (hatch.getFluidHandler().isPresent()) {
                FluidStack[] finalFluids = fluids;
                fluids = hatch.getFluidHandler().map(fh -> fh.exportAndReturnOutputs(finalFluids.clone())).orElse(new FluidStack[0]);
                if (fluids.length == 0) break;
            }
        }
        if (fluids.length > 0) System.out.println("HATCH OVERFLOW: " + Arrays.toString(fluids));
    }

    /**
     * Tests if items can fit across all output hatches
     **/
    public boolean canItemsFit(ItemStack[] items) {
        if (items == null) return true;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents("hatch_item_output")) {
            if (hatch.getItemHandler().isPresent()) {
                matchCount += hatch.getItemHandler().map(ih -> ih.getSpaceForOutputs(items)).orElse(0);
            }
        }
        return matchCount >= items.length;
    }

    /**
     * Tests if fluids can fit across all output hatches
     **/
    public boolean canFluidsFit(FluidStack[] fluids) {
        if (fluids == null) return true;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
            if (hatch.getFluidHandler().isPresent()) {
                matchCount += hatch.getFluidHandler().map(fh -> fh.getSpaceForOutputs(fluids)).orElse(0);
            }
        }
        return matchCount >= fluids.length;
    }

    @Override
    public int getMaxInputVoltage() {
        List<IComponentHandler> hatches = getComponents("hatch_energy");
        return hatches.size() >= 1 ? hatches.stream().mapToInt(t -> t.getEnergyHandler().map(eh -> eh.getInputAmperage() * eh.getInputVoltage()).orElse(0)).sum() : Ref.V[0];
    }

    public WidgetSupplier getInfoWidget() {
        return InfoRenderWidget.MultiRenderWidget.build().setPos(10, 10);
    }

    @Override
    public int drawInfo(InfoRenderWidget.MultiRenderWidget instance, MatrixStack stack, FontRenderer renderer, int left, int top) {
        renderer.drawString(stack, this.getDisplayName().getString(), left, top, 16448255);
        if (getMachineState() != MachineState.ACTIVE) {
            renderer.drawString(stack, "Inactive.", left, top + 8, 16448255);
            return 16;
        } else if (instance.drawActiveInfo()) {
            renderer.drawString(stack, "Progress: " + instance.currentProgress + "/" + instance.maxProgress, left, top + 8, 16448255);
            renderer.drawString(stack, "Overclock: " + instance.overclock, left, top + 16, 16448255);
            renderer.drawString(stack, "EU/t: " + instance.euT, left, top + 24, 16448255);
            return 32;
        }
        return 8;
    }
}
