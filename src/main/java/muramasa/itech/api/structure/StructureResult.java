package muramasa.itech.api.structure;

import muramasa.itech.api.capability.IComponent;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureResult {

    private StructurePattern pattern;
    private boolean hasError;
    private String error = "";

    private ArrayList<IComponent> components = new ArrayList<>();
    private HashMap<String, Integer> subElements = new HashMap<>();

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
        if (!components.contains(component)) {
            components.add(component);
        }
    }

    public void addSubElement(String elementId) {
        subElements.merge(elementId, 1, Integer::sum);
    }

    public ArrayList<IComponent> getComponents() {
        return components;
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (String key : pattern.getRequirements()) {
            if (!subElements.containsKey(key) || !pattern.testRequirement(key, subElements.get(key))) {
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
        return input1 > input2;
    }
}
