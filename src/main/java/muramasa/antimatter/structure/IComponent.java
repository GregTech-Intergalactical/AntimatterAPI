package muramasa.antimatter.structure;

import muramasa.antimatter.capability.IComponentHandler;
import net.minecraftforge.common.util.LazyOptional;

public interface IComponent {

    LazyOptional<? extends IComponentHandler> getComponentHandler();
}
