package muramasa.gtu.api.structure;

import com.google.common.collect.Lists;
import muramasa.gtu.api.capability.IComponentHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureResult {

    private Structure structure;
    private boolean hasError;
    private String error = "";

    private HashMap<String, ArrayList<IComponentHandler>> components = new HashMap<>();

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

    public void addComponent(String elementName, IComponentHandler component) {
        ArrayList<IComponentHandler> existing = components.get(component.getId());
        if (existing == null) components.put(component.getId(), Lists.newArrayList(component));
        else existing.add(component);
        if (!elementName.isEmpty() && !elementName.equals(component.getId())) {
            existing = components.get(elementName);
            if (existing == null) components.put(elementName, Lists.newArrayList(component));
            else existing.add(component);
        }
    }

    public HashMap<String, ArrayList<IComponentHandler>> getComponents() {
        return components;
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (String req : structure.getRequirements()) {
            if (!components.containsKey(req) || !structure.testRequirement(req, components.get(req).size())) {
                withError("Failed Element Requirement: " + req);
                return false;
            }
        }
        return true;
    }
}
