package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.capability.IComponent;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureResult {

    private StructurePattern pattern;
    private boolean hasError;
    private String error = "";

    private HashMap<String, ArrayList<IComponent>> components = new HashMap<>();

    public StructureResult(StructurePattern pattern) {
        this.pattern = pattern;
    }

    public StructureResult withError(String error) {
        this.error = error;
        hasError = true;
        return this;
    }

    public String getError() {
        return "[Structure Debug] " + error;
    }

    public void addComponent(IComponent component) {
        if (!components.containsKey(component.getId())) {
            components.put(component.getId(), new ArrayList<>());
        }
        components.get(component.getId()).add(component);
    }

    public HashMap<String, ArrayList<IComponent>> getComponents() {
        return components;
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (String key : pattern.getRequirements()) {
            if (!components.containsKey(key) || !pattern.testRequirement(key, components.get(key).size())) {
                withError("Failed Requirement: " + key);
                return false;
            }
        }
        return true;
    }

    public static boolean equal(int input1, int input2) {
        return input1 == input2;
    }

    public static boolean moreOrEqual(int input1, int input2) {
        return input1 >= input2;
    }
}
