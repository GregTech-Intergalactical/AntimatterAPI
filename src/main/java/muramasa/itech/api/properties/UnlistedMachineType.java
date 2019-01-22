package muramasa.itech.api.properties;

import muramasa.itech.api.machines.Machine;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedMachineType implements IUnlistedProperty<Machine> {

    @Override
    public String getName() {
        return "machineType";
    }

    @Override
    public boolean isValid(Machine machineType) {
        return true;
    }

    @Override
    public Class<Machine> getType() {
        return Machine.class;
    }

    @Override
    public String valueToString(Machine machineType) {
        return machineType.toString();
    }
}
