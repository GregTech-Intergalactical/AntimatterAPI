package muramasa.antimatter.example;

import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;

public class ExampleItem extends ItemBasic<ExampleItem> implements IAntimatterObject, ITextureProvider, IModelProvider {

    public ExampleItem(String domain, String id, Properties properties) {
        super(domain, id, "", properties);
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(getDomain(), "item/" + getId())};
    }
}
