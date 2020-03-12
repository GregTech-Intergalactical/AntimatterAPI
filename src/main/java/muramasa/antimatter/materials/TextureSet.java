package muramasa.antimatter.materials;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public class TextureSet implements IAntimatterObject {

    private static int LAST_INTERNAL_ID;

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
    private int internalId;

    public TextureSet(String id) {
        this.id = id;
        this.internalId = LAST_INTERNAL_ID++;
        AntimatterAPI.register(TextureSet.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getInternalId() {
        return internalId;
    }

    public Texture getTexture(MaterialType<?> type, int layer) {
        return getTexture(Ref.ID, type, layer);
    }

    public Texture getTexture(String domain, MaterialType<?> type, int layer) {
        //TODO return different numbered overlay based on current layer
        return new Texture(domain, "material/" + id + "/" + type.getId() + (layer == 0 ? "" : "_overlay"/*"_overlay_" + layer*/));
    }

    public Texture[] getTextures(MaterialType<?> type) {
        return getTextures(Ref.ID, type);
    }

    public Texture[] getTextures(String domain, MaterialType<?> type) {
        Texture[] textures = new Texture[type.getLayers()];
        for (int i = 0; i < type.getLayers(); i++) {
            textures[i] = getTexture(domain, type, i);
        }
        return textures;
    }

    public static int getLastInternalId() {
        return LAST_INTERNAL_ID;
    }
}
