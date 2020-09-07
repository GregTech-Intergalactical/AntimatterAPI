package muramasa.antimatter.structure;

import muramasa.antimatter.capability.machine.MachineCapabilityHolder;
import muramasa.antimatter.capability.IComponentHandler;

public interface IComponent {

    MachineCapabilityHolder<? extends IComponentHandler> getComponentHandler();
}
