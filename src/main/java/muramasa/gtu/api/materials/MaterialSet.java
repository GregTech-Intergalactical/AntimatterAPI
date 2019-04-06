package muramasa.gtu.api.materials;

import muramasa.gtu.api.texture.Texture;
import net.minecraft.util.IStringSerializable;

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
    FINE,
    FLINT;

    public Texture getBlockTexture(Prefix prefix) {
        return new Texture("blocks/material_set/" + getName() + "/" + prefix.getName());
    }

    public Texture getItemTexture(Prefix prefix) {
        return new Texture("items/material_set/" + getName() + "/" + prefix.getName());
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return getName();
    }
}
