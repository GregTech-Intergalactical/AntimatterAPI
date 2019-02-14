package muramasa.gregtech.api.properties;

import muramasa.gregtech.api.cover.CoverBehaviour;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Arrays;

public class UnlistedCovers implements IUnlistedProperty<CoverBehaviour[]> {

    @Override
    public String getName() {
        return "covers";
    }

    @Override
    public boolean isValid(CoverBehaviour[] cover) {
        return true;
    }

    @Override
    public Class<CoverBehaviour[]> getType() {
        return CoverBehaviour[].class;
    }

    @Override
    public String valueToString(CoverBehaviour[] cover) {
        return Arrays.toString(cover);
    }
}
