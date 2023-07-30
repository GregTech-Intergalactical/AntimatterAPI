package muramasa.antimatter.tile.multi;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.ControllerComponentHandler;
import muramasa.antimatter.capability.machine.MultiMachineEnergyHandler;
import muramasa.antimatter.capability.machine.MultiMachineFluidHandler;
import muramasa.antimatter.capability.machine.MultiMachineItemHandler;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.heat.IHeatHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.CELL;
import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class TileEntityMultiMachine<T extends TileEntityMultiMachine<T>> extends TileEntityBasicMultiMachine<T> implements IInfoRenderer<InfoRenderWidget.MultiRenderWidget> {

    protected long EUt;
    protected List<IHeatHandler> heatHandlers = Collections.emptyList();

    //TODO: Sync multiblock state(if it is formed), otherwise the textures might bug out. Not a big deal.
    public TileEntityMultiMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    public List<IHeatHandler> getHeatHandlers() {
        return heatHandlers;
    }

    @Override
    public Tier getPowerLevel() {
        return energyHandler.map(t -> ((MultiMachineEnergyHandler<T>) t).getAccumulatedPower()).orElse(super.getPowerLevel());
    }

    @Override
    public void afterStructureFormed() {
        this.components.forEach((k, v) -> v.forEach(c -> {
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
        var heats = this.components.get("components");
        if (heats != null) {
            this.heatHandlers = heats.stream().map(IComponentHandler::getHeatHandler).filter(Optional::isPresent).map(Optional::get).toList();
        } else {
            this.heatHandlers = Collections.emptyList();
        }
    }

    @Override
    public void onStructureInvalidated() {
        this.components.forEach((k, v) -> v.forEach(c -> c.onStructureInvalidated(this)));
        this.itemHandler.ifPresent(handle -> ((MultiMachineItemHandler<T>) handle).invalidate());
        this.energyHandler.ifPresent(handle -> ((MultiMachineEnergyHandler<T>) handle).invalidate());
        this.fluidHandler.ifPresent(handle -> ((MultiMachineFluidHandler<T>) handle).invalidate());
        this.heatHandlers = Collections.emptyList();
    }

    @Override
    public void onGuiEvent(IGuiEvent event, Player playerEntity) {
        super.onGuiEvent(event, playerEntity);
        /*if (event == GuiEvent.MULTI_ACTIVATE) {
            checkStructure();
            recipeHandler.ifPresent(MachineRecipeHandler::checkRecipe);
        }*/
    }

//    /**
//     * Returns list of items across all input hatches. Merges equal filters empty
//     **/
//    public ItemStack[] getStoredItems() {
//        if (!has(MachineFlag.ITEM)) return new ItemStack[0];
//        List<ItemStack> all = new ObjectArrayList<>();
//        for (IComponentHandler hatch : getComponents("hatch_item_input")) {
//            hatch.getItemHandler().ifPresent(h -> Utils.mergeItems(all, h.getInputList()));
//        }
//        System.out.println(all.toString());
//        return all.toArray(new ItemStack[0]);
//    }
//
//    /**
//     * Returns list of fluids across all input hatches. Merges equal filters empty
//     **/
//    public FluidStack[] getStoredFluids() {
//        if (!has(MachineFlag.FLUID)) return new FluidStack[0];
//        List<FluidStack> all = new ObjectArrayList<>();
//        for (IComponentHandler hatch : getComponents("hatch_fluid_input")) {
//            hatch.getFluidHandler().ifPresent(h -> Utils.mergeFluids(all, Arrays.asList(h.getInputs())));
//        }
//        System.out.println(all.toString());
//        return all.toArray(new FluidStack[0]);
//    }
//
//    /**
//     * Returns the total energy stored across all energy hatches
//     **/
//    public long getStoredEnergy() {
//        long total = 0;
//        for (IComponentHandler hatch : getComponents("hatch_energy")) {
//            if (hatch.getEnergyHandler().isPresent())
//                total += hatch.getEnergyHandler().map(MachineEnergyHandler::getEnergyStored).orElse(0);
//        }
//        return total;
//    }
//
//    /**
//     * Consumes inputs from all input hatches. Assumes Utils.doItemsMatchAndSizeValid has been used
//     **/
//    public void consumeItems(ItemStack[] items) {
//        if (items == null) return;
//        for (IComponentHandler hatch : getComponents("hatch_item_input")) {
//            if (hatch.getItemHandler().isPresent()) {
//                ItemStack[] finalItems = items;
//                items = hatch.getItemHandler().map(ih -> ih.consumeAndReturnInputs(finalItems.clone())).orElse(new ItemStack[0]);
//                if (items.length == 0) break;
//            }
//        }
//        if (items.length > 0) System.out.println("DID NOT CONSUME ALL: " + Arrays.toString(items));
//    }
//
//    /**
//     * Consumes inputs from all input hatches. Assumes Utils.doFluidsMatchAndSizeValid has been used
//     **/
//    public void consumeFluids(FluidStack[] inp) {
//        if (inp == null) return;
//        List<FluidStack> fluids = Arrays.asList(inp);
//        if (fluids.size() == 0) return;
//        for (IComponentHandler hatch : getComponents("hatch_fluid_input")) {
//            if (hatch.getFluidHandler().isPresent()) {
//                List<FluidStack> finalFluids = fluids;
//                fluids = hatch.getFluidHandler().map(fh -> fh.consumeAndReturnInputs(finalFluids, false)).orElse(Collections.emptyList());
//                if (fluids.size() == 0) break;
//            }
//        }
//        if (fluids.size() > 0) System.out.println("DID NOT CONSUME ALL: " + Arrays.toString(fluids.toArray()));
//    }
//
//    /**
//     * Export items to hatches regardless of space. Assumes canOutputsFit has been used
//     **/
//    public void outputItems(ItemStack[] items) {
//        if (items == null) return;
//        for (IComponentHandler hatch : getComponents("hatch_item_output")) {
//            if (hatch.getItemHandler().isPresent()) {
//                ItemStack[] finalItems = items;
//                items = hatch.getItemHandler().map(ih -> ih.exportAndReturnOutputs(finalItems.clone())).orElse(new ItemStack[0]); //WHY CLONE?!!?
//                if (items.length == 0) break;
//            }
//        }
//        if (items.length > 0) System.out.println("HATCH OVERFLOW: " + Arrays.toString(items));
//    }
//
//    /**
//     * Export fluids to hatches regardless of space. Assumes canOutputsFit has been used
//     **/
//    public void outputFluids(FluidStack[] fluids) {
//        if (fluids == null) return;
//        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
//            if (hatch.getFluidHandler().isPresent()) {
//                FluidStack[] finalFluids = fluids;
//                fluids = hatch.getFluidHandler().map(fh -> fh.exportAndReturnOutputs(finalFluids.clone())).orElse(new FluidStack[0]);
//                if (fluids.length == 0) break;
//            }
//        }
//        if (fluids.length > 0) System.out.println("HATCH OVERFLOW: " + Arrays.toString(fluids));
//    }
//
//    /**
//     * Tests if items can fit across all output hatches
//     **/
//    public boolean canItemsFit(ItemStack[] items) {
//        if (items == null) return true;
//        int matchCount = 0;
//        for (IComponentHandler hatch : getComponents("hatch_item_output")) {
//            if (hatch.getItemHandler().isPresent()) {
//                matchCount += hatch.getItemHandler().map(ih -> ih.getSpaceForOutputs(items)).orElse(0);
//            }
//        }
//        return matchCount >= items.length;
//    }
//
//    /**
//     * Tests if fluids can fit across all output hatches
//     **/
//    public boolean canFluidsFit(FluidStack[] fluids) {
//        if (fluids == null) return true;
//        int matchCount = 0;
//        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
//            if (hatch.getFluidHandler().isPresent()) {
//                matchCount += hatch.getFluidHandler().map(fh -> fh.getSpaceForOutputs(fluids)).orElse(0);
//            }
//        }
//        return matchCount >= fluids.length;
//    }

    @Override
    public long getMaxInputVoltage() {
        List<IComponentHandler> hatches = getComponents("hatch_energy");
        return hatches.size() >= 1 ? hatches.stream().mapToLong(t -> t.getEnergyHandler().map(eh -> eh.getInputAmperage() * eh.getInputVoltage()).orElse(0L)).sum() : Ref.V[0];
    }

    public WidgetSupplier getInfoWidget() {
        return InfoRenderWidget.MultiRenderWidget.build().setPos(10, 10);
    }

    @Override
    public int drawInfo(InfoRenderWidget.MultiRenderWidget instance, PoseStack stack, Font renderer, int left, int top) {
        renderer.draw(stack, this.getDisplayName().getString(), left, top, 16448255);
        if (getMachineState() != MachineState.ACTIVE) {
            renderer.draw(stack, "Inactive.", left, top + 8, 16448255);
            return 16;
        } else if (instance.drawActiveInfo()) {
            renderer.draw(stack, "Progress: " + instance.currentProgress + "/" + instance.maxProgress, left, top + 8, 16448255);
            renderer.draw(stack, "Overclock: " + instance.overclock, left, top + 16, 16448255);
            renderer.draw(stack, "EU/t: " + instance.euT, left, top + 24, 16448255);
            return 32;
        }
        return 8;
    }

    public void explodeMultiblock() {
        this.components.forEach((s, l) -> {
            l.forEach(c -> {
                if (c.getTile() instanceof TileEntityMachine<?> machine){
                    Utils.createExplosion(this.level, machine.getBlockPos(), 6.0F, Explosion.BlockInteraction.DESTROY);
                }
            });
        });
        Utils.createExplosion(this.level, this.getBlockPos(), 6.0F, Explosion.BlockInteraction.DESTROY);
    }
}
