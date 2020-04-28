package muramasa.antimatter.tile.multi;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.impl.ControllerComponentHandler;
import muramasa.antimatter.capability.impl.ControllerConfigHandler;
import muramasa.antimatter.capability.impl.MachineRecipeHandler;
import muramasa.antimatter.capability.impl.MultiMachineRecipeHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.GuiEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TileEntityMultiMachine extends TileEntityMachine implements IComponent {

    protected int efficiency, efficiencyIncrease; //TODO move to BasicMachine
    protected long EUt;
    protected Optional<ControllerComponentHandler> componentHandler = Optional.empty();

    protected Optional<StructureResult> result = Optional.empty();

    public TileEntityMultiMachine(TileEntityType<?> tileType) {
        super(tileType);
    }

    public TileEntityMultiMachine(Machine type) {
        super(type);
    }

    @Override
    public void initCaps() {
        super.initCaps();
        componentHandler = Optional.of(new ControllerComponentHandler(getMachineType(), this));
        configHandler = Optional.of(new ControllerConfigHandler(this));
        recipeHandler = Optional.of(new MultiMachineRecipeHandler<>(this));

        //TODO fix this oversight. This should not be needed? since super checks for proper flags? needs tested
        itemHandler = Optional.empty();
        fluidHandler = Optional.empty();
        energyHandler = Optional.empty();
    }

    public boolean checkStructure() {
        if (!isServerSide()) return false;
        Structure structure = getMachineType().getStructure(getMachineTier());
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            this.result = Optional.of(result);
            if (onStructureFormed()) {
                StructureCache.add(world, pos, result.positions);
                this.result.ifPresent(r -> r.components.forEach((k, v) -> v.forEach(c -> c.onStructureFormed(this))));
                setMachineState(MachineState.IDLE);
                System.out.println("[Structure Debug] Valid Structure");
                return true;
            }
        }
        this.result = Optional.empty();
        System.out.println("[Structure Debug] Invalid Structure" + result.getError());
        return false;
    }

    public void invalidateStructure() {
        result.ifPresent(r -> r.components.forEach((k, v) -> v.forEach(c -> c.onStructureInvalidated(this))));
        result = Optional.empty();
        resetMachine();
        System.out.println("INVALIDATED STRUCTURE");
        onStructureInvalidated();
    }

    /** Returns a list of Components **/
    public List<IComponentHandler> getComponents(IAntimatterObject object) {
        return getComponents(object.getId());
    }

    public List<IComponentHandler> getComponents(String id) {
        if (result.isPresent()) {
            List<IComponentHandler> list = result.get().components.get(id);
            return list != null ? list : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public List<BlockState> getStates(String id) {
        if (result.isPresent()) {
            List<BlockState> list = result.get().states.get(id);
            return list != null ? list : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public boolean isStructureValid() {
        return StructureCache.has(world, pos);
    }

    /** Events **/
    public boolean onStructureFormed() {
        return true;
    }

    public void onStructureInvalidated() {
        //NOOP
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == GuiEvent.MULTI_ACTIVATE) {
            checkStructure();
            recipeHandler.ifPresent(MachineRecipeHandler::checkRecipe);
        }
    }

    /** Returns list of items across all input hatches. Merges equal filters empty **/
    public ItemStack[] getStoredItems() {
        if (!has(MachineFlag.ITEM)) return new ItemStack[0];
        List<ItemStack> all = new ObjectArrayList<>();
        for (IComponentHandler hatch : getComponents("hatch_item_input")) {
            hatch.getItemHandler().ifPresent(h -> Utils.mergeItems(all, h.getInputList()));
        }
        System.out.println(all.toString());
        return all.toArray(new ItemStack[0]);
    }

    /** Returns list of fluids across all input hatches. Merges equal filters empty **/
    public FluidStack[] getStoredFluids() {
        if (!has(MachineFlag.FLUID)) return new FluidStack[0];
        List<FluidStack> all = new ObjectArrayList<>();
        for (IComponentHandler hatch : getComponents("hatch_fluid_input")) {
            hatch.getFluidHandler().ifPresent(h -> Utils.mergeFluids(all, h.getInputList()));
        }
        System.out.println(all.toString());
        return all.toArray(new FluidStack[0]);
    }

    /** Returns the total energy stored across all energy hatches **/
    public long getStoredEnergy() {
        long total = 0;
        for (IComponentHandler hatch : getComponents("hatch_energy")) {
            if (hatch.getEnergyHandler().isPresent()) total += hatch.getEnergyHandler().get().getEnergyStored();
        }
        return total;
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doItemsMatchAndSizeValid has been used **/
    public void consumeItems(ItemStack[] items) {
        if (items == null) return;
        for (IComponentHandler hatch : getComponents("hatch_item_input")) {
            if (hatch.getItemHandler().isPresent()) {
                items = hatch.getItemHandler().get().consumeAndReturnInputs(items.clone());
                if (items.length == 0) break;
            }
        }
        if (items.length > 0) System.out.println("DID NOT CONSUME ALL: " + items.toString());
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doFluidsMatchAndSizeValid has been used **/
    public void consumeFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        for (IComponentHandler hatch : getComponents("hatch_fluid_input")) {
            if (hatch.getFluidHandler().isPresent()) {
                fluids = hatch.getFluidHandler().get().consumeAndReturnInputs(fluids);
                if (fluids.length == 0) break;
            }
        }
        if (fluids.length > 0) System.out.println("DID NOT CONSUME ALL: " + fluids.toString());
    }

    /** Consumes energy from all energy hatches. Assumes enough energy is present in hatches **/
    public void consumeEnergy(long energy) {
        if (energy <= 0) return;
        for (IComponentHandler hatch : getComponents("hatch_energy")) {
            if (hatch.getEnergyHandler().isPresent()) {
                energy -= hatch.getEnergyHandler().get().extract(energy, false);
                if (energy == 0) break;
            }
        }
    }

    /** Export items to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputItems(ItemStack[] items) {
        if (items == null) return;
        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
            if (hatch.getItemHandler().isPresent()) {
                items = hatch.getItemHandler().get().exportAndReturnOutputs(items.clone()); //WHY CLONE?!!?
                if (items.length == 0) break;
            }
        }
        if (items.length > 0) System.out.println("HATCH OVERFLOW: " + items.toString());
    }

    /** Export fluids to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
            if (hatch.getFluidHandler().isPresent()) {
                fluids = hatch.getFluidHandler().get().exportAndReturnOutputs(fluids.clone());
                if (fluids.length == 0) break;
            }
        }
        if (fluids.length > 0) System.out.println("HATCH OVERFLOW: " + fluids.toString());
    }

    /** Tests if items can fit across all output hatches **/
    public boolean canItemsFit(ItemStack[] items) {
        if (items == null) return true;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
            if (hatch.getItemHandler().isPresent()) {
                matchCount += hatch.getItemHandler().get().getSpaceForOutputs(items);
            }
        }
        return matchCount >= items.length;
    }

    /** Tests if fluids can fit across all output hatches **/
    public boolean canFluidsFit(FluidStack[] fluids) {
        if (fluids == null) return true;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents("hatch_fluid_output")) {
            if (hatch.getFluidHandler().isPresent()) {
                matchCount += hatch.getFluidHandler().get().getSpaceForOutputs(fluids);
            }
        }
        return matchCount >= fluids.length;
    }

    @Override
    public int getMaxInputVoltage() {
        List<IComponentHandler> hatches = getComponents("hatch_energy");
        return hatches.size() >= 1 ? hatches.get(0).getEnergyHandler().isPresent() ? hatches.get(0).getEnergyHandler().get().getInputVoltage() : Ref.V[0] : Ref.V[0];
    }

    @Override
    public Optional<ControllerComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public MachineState getDefaultMachineState() {
        return MachineState.INVALID_STRUCTURE;
    }

    //TODO
//    @Override
//    public boolean hasCapability(Capability<?> capability, @Nullable Direction side) {
//        return capability == GTCapabilities.COMPONENT || super.hasCapability(capability, side);
//    }
//
//    @Nullable
//    @Override
//    public <T> T getCapability(Capability<T> capability, @Nullable Direction side) {
//        return capability == GTCapabilities.COMPONENT && componentHandler.isPresent() ? GTCapabilities.COMPONENT.cast(componentHandler.get()) : super.getCapability(capability, side);
//    }
}
