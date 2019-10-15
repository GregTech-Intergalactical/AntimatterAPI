package muramasa.gtu.api.structure;

import muramasa.gtu.api.capability.IComponentHandler;
import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public interface IRequirement {

    boolean test(HashMap<String, ArrayList<IComponentHandler>> components, HashMap<String, ArrayList<BlockState>> states);
}
