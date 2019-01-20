package muramasa.itech.api.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedInteger implements IUnlistedProperty<Integer> {

    @Override
    public String getName() {
        return "integer";
    }

    @Override
    public boolean isValid(Integer value) {
        return true;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String valueToString(Integer value) {
        return value.toString();
    }
}
