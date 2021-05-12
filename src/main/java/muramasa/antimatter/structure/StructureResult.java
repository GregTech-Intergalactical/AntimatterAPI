package muramasa.antimatter.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.Dir.*;

public class StructureResult {

    private final Structure structure;
    private boolean hasError;
    private String error = "";

    //TODO compile list of positions

    public Object2ObjectMap<String, List<IComponentHandler>> components = new Object2ObjectOpenHashMap<>();
    public Object2ObjectMap<String, List<BlockState>> states = new Object2ObjectOpenHashMap<>();
    public LongList positions = new LongArrayList();

    public StructureResult(Structure structure) {
        this.structure = structure;
    }

    public void withError(String error) {
        this.error = error;
        hasError = true;
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
        positions.add(component.getTile().getPos().toLong());
    }

    public void addState(String elementId, BlockPos pos, BlockState state) {
        if (!elementId.equals(StructureElement.IGNORE.elementId)) {
            List<BlockState> existing = states.get(elementId);
            if (existing == null) states.put(elementId, Lists.newArrayList(state));
            else existing.add(state);
            positions.add(pos.toLong());
        }
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (Map.Entry<String, IRequirement> entry : structure.getRequirements().entrySet()) {
            if (!entry.getValue().test(components, states)) {
                withError("Failed Element Requirement: " + entry.getKey());
                return false;
            }
        }
        return true;
    }

    public void build(TileEntityBasicMultiMachine machine, StructureResult result) {
        int3 size = result.structure.size();
        int2 offset = result.structure.getOffset();
        int3 corner = new int3(machine.getPos(), machine.getFacing()).left(size.getX() / 2).back(offset.x).up(offset.y);
        int3 working = new int3(machine.getFacing());
        int elementSize = result.structure.getElements().size();
        Tuple<int3, StructureElement> element;
        for (int i = 0; i < elementSize; i++) {
            element = structure.getElements().get(i);
            working.set(corner).offset(element.getA(), RIGHT, UP, FORWARD);
            StructureElement el = element.getB();
            int count = StructureCache.refCount(machine.getWorld(), working);
            el.onBuild(machine, working, result, count);
        }
    }

    public void remove(TileEntityBasicMultiMachine machine, StructureResult result) {
        int3 size = result.structure.size();
        int2 offset = result.structure.getOffset();
        int3 corner = new int3(machine.getPos(), machine.getFacing()).left(size.getX() / 2).back(offset.x).up(offset.y);
        int3 working = new int3(machine.getFacing());
        int elementSize = result.structure.getElements().size();
        Tuple<int3, StructureElement> element;
        for (int i = 0; i < elementSize; i++) {
            element = structure.getElements().get(i);
            working.set(corner).offset(element.getA(), RIGHT, UP, FORWARD);
            StructureElement el = element.getB();
            int count = StructureCache.refCount(machine.getWorld(), working);
            el.onRemove(machine, working, result, count);
        }
    }
}
