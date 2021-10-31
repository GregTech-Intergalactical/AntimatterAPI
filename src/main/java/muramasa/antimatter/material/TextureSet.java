package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public class TextureSet implements IAntimatterObject {

    public static TextureSet NONE = new TextureSet(Ref.ID, "none");
    public static TextureSet DULL = new TextureSet(Ref.ID, "dull");
    public static TextureSet METALLIC = new TextureSet(Ref.ID, "metallic");
    public static TextureSet SHINY = new TextureSet(Ref.ID, "shiny");
    public static TextureSet ROUGH = new TextureSet(Ref.ID, "rough");
    public static TextureSet MAGNETIC = new TextureSet(Ref.ID, "magnetic");
    public static TextureSet DIAMOND = new TextureSet(Ref.ID, "diamond");
    public static TextureSet RUBY = new TextureSet(Ref.ID, "ruby");
    public static TextureSet LAPIS = new TextureSet(Ref.ID, "lapis");
    public static TextureSet GEM_H = new TextureSet(Ref.ID, "gem_h");
    public static TextureSet GEM_V = new TextureSet(Ref.ID, "gem_v");
    public static TextureSet GARNET = new TextureSet(Ref.ID, "garnet");
    public static TextureSet QUARTZ = new TextureSet(Ref.ID, "quartz");
    public static TextureSet FINE = new TextureSet(Ref.ID, "fine");
    public static TextureSet FLINT = new TextureSet(Ref.ID, "flint");
    public static TextureSet LIGNITE = new TextureSet(Ref.ID, "lignite");
    public static TextureSet WOOD = new TextureSet(Ref.ID, "wood");
    public static TextureSet REDSTONE = new TextureSet(Ref.ID, "redstone");

    private String domain, id;

    public TextureSet(String domain, String id) {
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(TextureSet.class, this);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public Texture getTexture(MaterialType<?> type, int layer) {
        //TODO return different numbered overlay based on current layer
        return new Texture(domain, "material/" + id + "/" + type.getId() + (layer == 0 ? "" : "_overlay"/*"_overlay_" + layer*/));
    }

    public String getPath() {
        return "material/" + id;
    }

    public Texture[] getTextures(MaterialType<?> type) {
        Texture[] textures = new Texture[type.getLayers()];
        for (int i = 0; i < type.getLayers(); i++) {
            textures[i] = getTexture(type, i);
        }
        return textures;
    }
}
