package muramasa.gtu.api.texture;

public interface IBakedTile {

    //TODO possible pass defaultData?
    TextureData getTextureData();

    void setTextureOverride(int textureOverride);
}
