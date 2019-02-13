package muramasa.gregtech.api.properties;

import muramasa.gregtech.api.cover.Cover;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Arrays;

public class UnlistedCovers implements IUnlistedProperty<Cover[]> {

    @Override
    public String getName() {
        return "covers";
    }

    @Override
    public boolean isValid(Cover[] cover) {
        return true;
    }

    @Override
    public Class<Cover[]> getType() {
        return Cover[].class;
    }

    @Override
    public String valueToString(Cover[] cover) {
        return Arrays.toString(cover);
    }
}
