package muramasa.antimatter.structure;

import muramasa.antimatter.capability.IComponentHandler;
import net.minecraft.block.BlockState;
import speiger.src.collections.objects.maps.interfaces.Object2ObjectMap;

import java.util.List;

@FunctionalInterface
public interface IRequirement {

    boolean test(Object2ObjectMap<String, List<IComponentHandler>> components, Object2ObjectMap<String, List<BlockState>> states);
}
