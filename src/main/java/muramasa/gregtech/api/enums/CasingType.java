package muramasa.gregtech.api.enums;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CasingType implements IStringSerializable {

    ULV(),
    LV(),
    MV(),
    HV(),
    EV(),
    IV(),
    LUV(),
    ZPM(),
    UV(),
    MAX(),
    BRONZE(),
    BRICKED_BRONZE(),
    BRONZE_PLATED_BRICK(),
    FIRE_BRICK(),
    STEEL(),
    BRICKED_STEEL(),
    SOLID_STEEL(),
    STAINLESS_STEEL(),
    TITANIUM(),
    TUNGSTENSTEEL(),
    HEAT_PROOF(),
    FROST_PROOF(),
    RADIATION_PROOF(),
    FIREBOX_BRONZE(),
    FIREBOX_STEEL(),
    FIREBOX_TITANIUM(),
    FIREBOX_TUNGSTENSTEEL(),
    GEARBOX_BRONZE(),
    GEARBOX_STEEL(),
    GEARBOX_TITANIUM(),
    GEARBOX_TUNGSTENSTEEL(),
    PIPE_BRONZE(),
    PIPE_STEEL(),
    PIPE_TITANIUM(),
    PIPE_TUNGSTENSTEEL(),
    ENGINE_INTAKE(),
    TURBINE_1(),
    TURBINE_2(),
    TURBINE_3(),
    TURBINE_4(),
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
