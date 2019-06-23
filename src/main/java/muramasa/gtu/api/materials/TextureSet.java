package muramasa.gtu.api.materials;

import com.google.common.collect.ImmutableList;
import muramasa.gtu.Ref;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.texture.Texture;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;

public class TextureSet implements IGregTechObject {

    private static HashMap<String, TextureSet> LOOKUP = new HashMap<>();

    public static TextureSet NONE = new TextureSet("none");
    public static TextureSet DULL = new TextureSet("dull");
    public static TextureSet METALLIC = new TextureSet("metallic");
    public static TextureSet SHINY = new TextureSet("shiny");
    public static TextureSet ROUGH = new TextureSet("rough");
    public static TextureSet MAGNETIC = new TextureSet("magnetic");
    public static TextureSet DIAMOND = new TextureSet("diamond");
    public static TextureSet RUBY = new TextureSet("ruby");
    public static TextureSet LAPIS = new TextureSet("lapis");
    public static TextureSet GEM_H = new TextureSet("gem_h");
    public static TextureSet GEM_V = new TextureSet("gem_v");
    public static TextureSet QUARTZ = new TextureSet("quartz");
    public static TextureSet FINE = new TextureSet("fine");
    public static TextureSet FLINT = new TextureSet("flint");
    public static TextureSet LIGNITE = new TextureSet("lignite");

    private String id;

    public TextureSet(String id) {
        this.id = id;
        LOOKUP.put(id, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public Texture getBlockTexture(Prefix prefix) {
        return new Texture("blocks/material_set/" + id + "/" + prefix.getId());
    }

    public Texture getItemTexture(Prefix prefix) {
        return new Texture("items/material_set/" + id + "/" + prefix.getId());
    }

    public ImmutableList<ResourceLocation> getItemTextures(Prefix prefix) {
        return ImmutableList.of(
            new ResourceLocation(Ref.MODID, "items/material_set/" + id + "/" + prefix.getId()),
            new ResourceLocation(Ref.MODID, "items/material_set/" + id + "/overlay/" + prefix.getId())
        );
    }

    public static TextureSet get(String name) {
        return LOOKUP.get(name);
    }

    public static Collection<TextureSet> getAll() {
        return LOOKUP.values();
    }
}
