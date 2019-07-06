package muramasa.gtu.api.structure;

import com.google.common.collect.Lists;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.data.Structures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class StructureResult {

    private Structure structure;
    private boolean hasError;
    private String error = "";

    //TODO compile list of positions

    public HashMap<String, ArrayList<IComponentHandler>> components = new HashMap<>();
    public HashMap<String, ArrayList<IBlockState>> states = new HashMap<>();
    public List<BlockPos> positions = new ArrayList<>();

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
        ArrayList<IComponentHandler> existing = components.get(component.getId());
        if (existing == null) components.put(component.getId(), Lists.newArrayList(component));
        else existing.add(component);
        if (!elementId.isEmpty() && !elementId.equals(component.getId())) {
            existing = components.get(elementId);
            if (existing == null) components.put(elementId, Lists.newArrayList(component));
            else existing.add(component);
        }
        positions.add(component.getTile().getPos());
    }

    public void addState(String elementId, BlockPos pos, IBlockState state) {
        if (!elementId.equals(Structures.X.elementId)) {
            ArrayList<IBlockState> existing = states.get(elementId);
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
