package muramasa.antimatter.structure;

import muramasa.antimatter.capability.IComponentHandler;

import java.util.Optional;

public interface IComponent {

    Optional<? extends IComponentHandler> getComponentHandler();
}
