package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.MultiMachineEnergyHandler;
import muramasa.antimatter.capability.machine.MultiMachineFluidHandler;
import muramasa.antimatter.capability.machine.MultiMachineItemHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Allows a MultiMachine to handle GUI recipes, instead of using Hatches **/
public class TileEntityBasicMultiMachine extends TileEntityMachine {

    protected Optional<StructureResult> result = Optional.empty();

    public TileEntityBasicMultiMachine(Machine<?> type) {
        super(type);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        invalidateStructure();
    }

    @Override
    public void onFirstTick() {
        if (!isStructureValid()) {
            checkStructure();
        }
        super.onFirstTick();
    }

    public boolean checkStructure() {
        Structure structure = getMachineType().getStructure(getMachineTier());
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            this.result = Optional.of(result);
            StructureCache.add(world, pos, result.positions);
            if (isServerSide()) {
                if (onStructureFormed()) {
                    afterStructureFormed();
                    setMachineState(MachineState.IDLE);
                    System.out.println("[Structure Debug] Valid Structure");
                    if (hadFirstTick()) this.recipeHandler.ifPresent(t -> {
                        if (t.hasRecipe())
                            setMachineState(MachineState.NO_POWER);
                        else {
                            t.checkRecipe();
                        }
                    });
                    sidedSync(true);
                    return true;
                }
            } else {
                this.result.ifPresent(r -> r.components.forEach((k, v) -> v.forEach(c -> {
                    Utils.markTileForRenderUpdate(c.getTile());
                })));
                sidedSync(true);
                return true;
            }
        } else {
            invalidateStructure();
        }
        return false;
    }

    @Override
    public boolean setFacing(Direction side) {
        boolean ok = super.setFacing(side);
        if (ok) {
            checkStructure();
        }
        return ok;
    }

    public void invalidateStructure() {
        if (removed) return;
        if (!result.isPresent()) return;
        StructureCache.remove(this.getWorld(), getPos());
        if (isServerSide()) {
            onStructureInvalidated();
            result = Optional.empty();
            resetMachine();
        } else {
            this.result.ifPresent(r -> r.components.forEach((k, v) -> v.forEach(c -> {
                Utils.markTileForRenderUpdate(c.getTile());
            })));
            result = Optional.empty();
        }
    }

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
        if (!result.isPresent() && world != null && world.getGameTime() % 200 == 0) {
            //Uncomment to periodically check structure.
            // checkStructure();
        }
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

    public void afterStructureFormed(){
        //NOOP
    }

    public void onStructureInvalidated() {
        //NOOP
    }

    @Override
    public MachineState getDefaultMachineState() {
        return MachineState.INVALID_STRUCTURE;
    }
}
