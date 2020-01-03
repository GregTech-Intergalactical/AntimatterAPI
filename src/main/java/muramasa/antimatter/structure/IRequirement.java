package muramasa.antimatter.structure;

import muramasa.antimatter.capability.IComponentHandler;
import net.minecraft.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public interface IRequirement {

    boolean test(HashMap<String, ArrayList<IComponentHandler>> components, HashMap<String, ArrayList<BlockState>> states);
}
