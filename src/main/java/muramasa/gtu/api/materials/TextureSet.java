package muramasa.gtu.api.materials;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.texture.Texture;

public class TextureSet implements IGregTechObject {

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
        GregTechAPI.register(TextureSet.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public Texture[] getTextures(MaterialType type) {
        return new Texture[] {
            new Texture("material/" + id + "/" + type.getId()),
            new Texture("material/" + id + "/" + type.getId() + "_overlay"),
        };
    }
}
