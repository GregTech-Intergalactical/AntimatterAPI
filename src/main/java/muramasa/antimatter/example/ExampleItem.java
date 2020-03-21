package muramasa.antimatter.example;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.item.Item;

public class ExampleItem extends ItemBasic implements IAntimatterObject, ITextureProvider, IModelProvider {

    public ExampleItem(String domain, String id, Item.Properties properties) {
        super(domain, id, properties);
        AntimatterAPI.register(ExampleItem.class, this);
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(domain, "item/" + getId())};
    }
}
