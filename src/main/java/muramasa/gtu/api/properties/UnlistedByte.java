package muramasa.gtu.api.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedByte implements IUnlistedProperty<Byte> {

    @Override
    public String getName() {
        return "byte";
    }

    @Override
    public boolean isValid(Byte value) {
        return true;
    }

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }

    @Override
    public String valueToString(Byte value) {
        return value.toString();
    }
}
