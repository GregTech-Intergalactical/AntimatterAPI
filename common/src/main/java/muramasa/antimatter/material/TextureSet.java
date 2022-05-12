package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public class TextureSet implements IAntimatterObject {

    public static final TextureSet NONE = new TextureSet(Ref.ID, "none");
    public static final TextureSet DULL = new TextureSet(Ref.ID, "dull");
    public static final TextureSet METALLIC = new TextureSet(Ref.ID, "metallic");
    public static final TextureSet SHINY = new TextureSet(Ref.ID, "shiny");
    public static final TextureSet ROUGH = new TextureSet(Ref.ID, "rough");
    public static final TextureSet MAGNETIC = new TextureSet(Ref.ID, "magnetic");
    public static final TextureSet DIAMOND = new TextureSet(Ref.ID, "diamond");
    public static final TextureSet RUBY = new TextureSet(Ref.ID, "ruby");
    public static final TextureSet LAPIS = new TextureSet(Ref.ID, "lapis");
    public static final TextureSet GEM_H = new TextureSet(Ref.ID, "gem_h");
    public static final TextureSet GEM_V = new TextureSet(Ref.ID, "gem_v");
    public static final TextureSet GARNET = new TextureSet(Ref.ID, "garnet");
    public static final TextureSet QUARTZ = new TextureSet(Ref.ID, "quartz");
    public static final TextureSet FINE = new TextureSet(Ref.ID, "fine");
    public static final TextureSet FLINT = new TextureSet(Ref.ID, "flint");
    public static final TextureSet LIGNITE = new TextureSet(Ref.ID, "lignite");
    public static final TextureSet WOOD = new TextureSet(Ref.ID, "wood");
    public static final TextureSet REDSTONE = new TextureSet(Ref.ID, "redstone");

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

    public static void init() {

    }
}
