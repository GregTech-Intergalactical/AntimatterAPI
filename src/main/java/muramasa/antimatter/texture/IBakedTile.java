package muramasa.antimatter.texture;

public interface IBakedTile {

    //TODO possible pass defaultData?
    TextureData getTextureData();

    void setTextureOverride(int textureOverride);
}
