package muramasa.antimatter.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.IComponentHandler;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class StructureResult {

    private Structure structure;
    private boolean hasError;
    private String error = "";

    //TODO compile list of positions

    public Object2ObjectMap<String, List<IComponentHandler>> components = new Object2ObjectOpenHashMap<>();
    public Object2ObjectMap<String, List<BlockState>> states = new Object2ObjectOpenHashMap<>();
    public List<BlockPos> positions = new ObjectArrayList<>();

    public StructureResult(Structure structure) {
        this.structure = structure;
    }

    public StructureResult withError(String error) {
        this.error = error;
        hasError = true;
        return this;
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
        positions.add(component.getTile().getPos());
    }

    public void addState(String elementId, BlockPos pos, BlockState state) {
        if (!elementId.equals(StructureElement.IGNORE.elementId)) {
            List<BlockState> existing = states.get(elementId);
            if (existing == null) states.put(elementId, Lists.newArrayList(state));
            else existing.add(state);
            positions.add(pos);
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
}
