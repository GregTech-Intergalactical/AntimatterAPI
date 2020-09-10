package muramasa.antimatter.structure;

import muramasa.antimatter.capability.machine.MachineCapabilityHandler;
import muramasa.antimatter.capability.IComponentHandler;

public interface IComponent {

    MachineCapabilityHandler<? extends IComponentHandler> getComponentHandler();
}
