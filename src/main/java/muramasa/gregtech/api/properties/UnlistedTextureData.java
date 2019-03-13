package muramasa.gregtech.api.properties;

import muramasa.gregtech.api.texture.TextureData;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedTextureData implements IUnlistedProperty<TextureData> {

    @Override
    public String getName() {
        return "texture_data";
    }

    @Override
    public boolean isValid(TextureData value) {
        return true;
    }

    @Override
    public Class<TextureData> getType() {
        return TextureData.class;
    }

    @Override
    public String valueToString(TextureData value) {
        return value.toString();
    }
}
