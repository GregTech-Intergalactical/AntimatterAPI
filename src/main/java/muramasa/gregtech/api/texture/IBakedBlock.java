package muramasa.gregtech.api.texture;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IBakedBlock {

    TextureData getTextureData();

    ModelResourceLocation getModel();
}
