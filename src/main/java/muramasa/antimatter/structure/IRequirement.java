package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import muramasa.antimatter.capability.IComponentHandler;
import net.minecraft.block.BlockState;

import java.util.List;

@FunctionalInterface
public interface IRequirement {

    boolean test(Object2ObjectMap<String, List<IComponentHandler>> components, Object2ObjectMap<String, List<BlockState>> states);
}
