package muramasa.gtu.api.structure;

import muramasa.gtu.api.capability.IComponentHandler;

import java.util.Optional;

public interface IComponent {

    Optional<? extends IComponentHandler> getComponentHandler();
}
