package muramasa.gtu.api.ore;

import com.google.common.collect.Lists;
import muramasa.gtu.api.materials.MaterialType;
import net.minecraft.util.IStringSerializable;

import java.util.List;
import java.util.Locale;

public enum OreType implements IStringSerializable {

    //TODO expand ore types :)
    NORMAL(MaterialType.ORE),
    SMALL(MaterialType.ORE_SMALL);

    private MaterialType type;

    OreType(MaterialType type) {
        this.type = type;
    }

    public static List<OreType> VALUES;

    static {
        VALUES = Lists.newArrayList(values());
    }

    public MaterialType getMaterialType() {
        return type;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
