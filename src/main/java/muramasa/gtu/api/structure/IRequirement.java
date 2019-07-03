package muramasa.gtu.api.structure;

import muramasa.gtu.api.capability.IComponentHandler;

import java.util.ArrayList;
import java.util.HashMap;

public interface IRequirement {

    boolean test(HashMap<String, ArrayList<IComponentHandler>> components);
}
