package muramasa.gtu.api.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Arrays;

public class UnlistedIntArray implements IUnlistedProperty<int[]> {

    @Override
    public String getName() {
        return "integer_array";
    }

    @Override
    public boolean isValid(int[] value) {
        return true;
    }

    @Override
    public Class<int[]> getType() {
        return int[].class;
    }

    @Override
    public String valueToString(int[] value) {
        return Arrays.toString(value);
    }
}
