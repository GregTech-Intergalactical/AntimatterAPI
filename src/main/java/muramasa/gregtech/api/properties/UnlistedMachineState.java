package muramasa.gregtech.api.properties;

import muramasa.gregtech.api.enums.MachineState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedMachineState implements IUnlistedProperty<MachineState> {

    @Override
    public String getName() {
        return "machineState";
    }

    @Override
    public boolean isValid(MachineState machineState) {
        return true;
    }

    @Override
    public Class<MachineState> getType() {
        return MachineState.class;
    }

    @Override
    public String valueToString(MachineState machineState) {
        return machineState.getName();
    }
}
