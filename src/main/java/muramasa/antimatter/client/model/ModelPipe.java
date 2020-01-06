package muramasa.antimatter.client.model;

import muramasa.antimatter.client.ModelBuilder;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;

public class ModelPipe extends ModelDynamic {

    public ModelPipe(Texture... defaultTextures) {
        super(defaultTextures);
        staticBaking();
    }

    public ModelPipe(ITextureProvider provider) {
        super(provider);
        staticBaking();
    }

    @Override
    protected ModelBuilder getDefaultModel() {
        return super.getDefaultModel();
    }
}
