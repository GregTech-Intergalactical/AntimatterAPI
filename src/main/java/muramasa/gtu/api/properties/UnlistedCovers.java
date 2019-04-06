package muramasa.gtu.api.properties;

import muramasa.gtu.api.cover.Cover;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedCovers implements IUnlistedProperty<Cover[]> {

    @Override
    public String getName() {
        return "covers";
    }

    @Override
    public boolean isValid(Cover[] handler) {
        return true;
    }

    @Override
    public Class<Cover[]> getType() {
        return Cover[].class;
    }

    @Override
    public String valueToString(Cover[] handler) {
        return handler.toString();
    }
}
