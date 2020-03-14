package muramasa.antimatter.materials;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public class TextureSet implements IAntimatterObject {

    private static int LAST_INTERNAL_ID;

    public static TextureSet NONE = new TextureSet(Ref.ID, "none");

    private String domain, id;
    private int internalId;

    public TextureSet(String domain, String id) {
        this.domain = domain;
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
        //TODO return different numbered overlay based on current layer
        return new Texture(domain, "material/" + id + "/" + type.getId() + (layer == 0 ? "" : "_overlay"/*"_overlay_" + layer*/));
    }

    public Texture[] getTextures(MaterialType<?> type) {
        Texture[] textures = new Texture[type.getLayers()];
        for (int i = 0; i < type.getLayers(); i++) {
            textures[i] = getTexture(type, i);
        }
        return textures;
    }

    public static int getLastInternalId() {
        return LAST_INTERNAL_ID;
    }
}
