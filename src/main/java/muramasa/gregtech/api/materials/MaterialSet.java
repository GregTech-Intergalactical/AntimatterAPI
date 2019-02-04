package muramasa.gregtech.api.materials;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public enum MaterialSet implements IStringSerializable {

    NONE,
    DULL,
    METALLIC,
    SHINY,
    ROUGH,
    MAGNETIC,
    DIAMOND,
    RUBY,
    LAPIS,
    GEM_H,
    GEM_V,
    QUARTZ,
    FLINT;

    public ResourceLocation getOreLoc() {
        return new ResourceLocation(Ref.MODID, "items/material_set/" + getName() + "/ore");
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
