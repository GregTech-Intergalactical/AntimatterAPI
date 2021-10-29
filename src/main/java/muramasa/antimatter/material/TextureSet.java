package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public class TextureSet implements IAntimatterObject {

    public static TextureSet NONE = new TextureSet(Ref.ID, "none");

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
