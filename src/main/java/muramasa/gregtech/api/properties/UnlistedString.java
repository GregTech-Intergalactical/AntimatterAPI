package muramasa.gregtech.api.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedString implements IUnlistedProperty<String> {

    @Override
    public String getName() {
        return "string";
    }

    @Override
    public boolean isValid(String value) {
        return true;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }
}
