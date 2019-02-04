package muramasa.gregtech.api.properties;

import muramasa.gregtech.api.enums.CoverType;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Arrays;

public class UnlistedCoverType implements IUnlistedProperty<CoverType[]> {

    @Override
    public String getName() {
        return "coverType";
    }

    @Override
    public boolean isValid(CoverType[] coverType) {
        return true;
    }

    @Override
    public Class<CoverType[]> getType() {
        return CoverType[].class;
    }

    @Override
    public String valueToString(CoverType[] coverType) {
        return Arrays.toString(coverType);
    }
}
