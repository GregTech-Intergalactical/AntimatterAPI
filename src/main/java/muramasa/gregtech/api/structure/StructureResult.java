package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.capability.IComponent;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureResult {

    private Structure structure;
    private boolean hasError;
    private String error = "";

    private HashMap<String, ArrayList<IComponent>> components = new HashMap<>();

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

    public void addComponent(String elementName, IComponent component) {
        if (!components.containsKey(component.getId())) {
            components.put(component.getId(), new ArrayList<>());
        }
        components.get(component.getId()).add(component);

//        if (!components.containsKey(elementName)) {
//            components.put(elementName, new ArrayList<>());
//        }
//        components.get(elementName).add(component);
    }

    public HashMap<String, ArrayList<IComponent>> getComponents() {
        return components;
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (String key : structure.getRequirements()) {
            if (!components.containsKey(key) || !structure.testRequirement(key, components.get(key).size())) {
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
