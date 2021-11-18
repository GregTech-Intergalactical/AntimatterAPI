package muramasa.antimatter.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class StructureResult {

    private final Structure structure;
    private boolean hasError;
    private String error = "";

    public Object2ObjectMap<String, List<IComponentHandler>> components = new Object2ObjectOpenHashMap<>();
    public Object2ObjectMap<String, List<BlockState>> states = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, StructureElement> elementLookup = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, StructureElement> tickingElements = new Object2ObjectOpenHashMap<>();
    public final LongList positions = new LongArrayList();

    public StructureResult(Structure structure) {
        this.structure = structure;
    }

    public void withError(String error) {
        this.error = error;
        hasError = true;
    }

    public StructureResult register(BlockPos pos, StructureElement el) {
        elementLookup.put(pos, el);
        if (el.ticks()) {
            tickingElements.put(pos, el);
        }
        return this;
    }

    public StructureElement get(BlockPos pos) {
        return elementLookup.get(pos);
    }

    public String getError() {
        return "[Structure Debug] " + error;
    }

    public void addComponent(String elementId, IComponentHandler component) {
        List<IComponentHandler> existing = components.get(component.getId());
        if (existing == null) components.put(component.getId(), Lists.newArrayList(component));
        else existing.add(component);
        if (!elementId.isEmpty() && !elementId.equals(component.getId())) {
            existing = components.get(elementId);
            if (existing == null) components.put(elementId, Lists.newArrayList(component));
            else existing.add(component);
        }
        positions.add(component.getTile().getBlockPos().asLong());
    }

    public void addState(String elementId, BlockPos pos, BlockState state) {
        if (!elementId.equals(StructureElement.IGNORE.elementId)) {
            List<BlockState> existing = states.get(elementId);
            if (existing == null) states.put(elementId, Lists.newArrayList(state));
            else existing.add(state);
            positions.add(pos.asLong());
        }
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (Map.Entry<String, IRequirement> entry : structure.getRequirements().entrySet()) {
            if (!entry.getValue().test(this)) {
                withError("Failed Element Requirement: " + entry.getKey());
                return false;
            }
        }
        return true;
    }

    public void build(TileEntityBasicMultiMachine<?> machine, StructureResult result) {
        for (Map.Entry<BlockPos, StructureElement> entry : this.elementLookup.entrySet()) {
            int count = StructureCache.refCount(machine.getLevel(), entry.getKey());
            entry.getValue().onBuild(machine, entry.getKey(), result, count);
        }
    }

    public void remove(TileEntityBasicMultiMachine<?> machine, StructureResult result) {
        for (Map.Entry<BlockPos, StructureElement> entry : this.elementLookup.entrySet()) {
            int count = StructureCache.refCount(machine.getLevel(), entry.getKey());
            entry.getValue().onRemove(machine, entry.getKey(), result, count);
        }
    }

    public void updateState(TileEntityBasicMultiMachine<?> machine, StructureResult result) {
        MachineState proper = machine.getMachineState().getTextureState();
        for (Map.Entry<BlockPos, StructureElement> entry : this.elementLookup.entrySet()) {
            int count = StructureCache.refCount(machine.getLevel(), entry.getKey());
            entry.getValue().onStateChange(machine, proper, entry.getKey(), result, count);
        }
    }

    public void tick(TileEntityBasicMultiMachine<?> machine) {
        if (tickingElements.isEmpty()) return;
        for (Map.Entry<BlockPos, StructureElement> entry : tickingElements.entrySet()) {
            entry.getValue().tick(machine, this, entry.getKey());
        }
    }
}
