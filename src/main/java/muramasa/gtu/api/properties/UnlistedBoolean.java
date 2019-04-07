package muramasa.gtu.api.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedBoolean implements IUnlistedProperty<Boolean> {

    @Override
    public String getName() {
        return "boolean";
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public String valueToString(Boolean value) {
        return value.toString();
    }
}
