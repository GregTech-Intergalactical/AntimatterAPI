package muramasa.antimatter.tier;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public interface IAntimatterTier extends IAntimatterObject {

    int getVoltage();

    Texture getBaseTexture();

    default Texture[] getTextures(IAntimatterTier... tiers) {
        Texture[] textures = new Texture[tiers.length];
        for (int i = 0; i < tiers.length; i++) {
            textures[i] = tiers[i].getBaseTexture();
        }
        return textures;
    }
}
