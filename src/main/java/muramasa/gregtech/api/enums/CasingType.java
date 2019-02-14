package muramasa.gregtech.api.enums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CasingType implements IStringSerializable {

    HEAT_PROOF(),
    STAINLESS_STEEL(),
    FUSION_1(),
    FUSION_2(),
    FUSION_3();

    CasingType() {

    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
