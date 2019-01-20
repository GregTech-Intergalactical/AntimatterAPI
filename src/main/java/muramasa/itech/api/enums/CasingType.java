package muramasa.itech.api.enums;

import net.minecraft.util.IStringSerializable;

public enum CasingType implements IStringSerializable {

    HEAT_PROOF("heatproof"),
    STAINLESS_STEEL("stainlesssteel"),
//    FUSION1("fusion1"),
//    FUSION2("fusion2"),
    FUSION3("fusion3");

    private String name;

    CasingType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
