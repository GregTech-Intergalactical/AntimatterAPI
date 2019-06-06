package muramasa.gtu.api.materials;

import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.texture.Texture;

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

    private String name;

    public TextureSet(String name) {
        this.name = name;
        LOOKUP.put(name, this);
    }

    @Override
    public String getName() {
        return name;
    }

    public Texture getBlockTexture(Prefix prefix) {
        return new Texture("blocks/material_set/" + name + "/" + prefix.getName());
    }

    public Texture getItemTexture(Prefix prefix) {
        return new Texture("items/material_set/" + name + "/" + prefix.getName());
    }

    public static TextureSet get(String name) {
        return LOOKUP.get(name);
    }

    public static Collection<TextureSet> getAll() {
        return LOOKUP.values();
    }
}
