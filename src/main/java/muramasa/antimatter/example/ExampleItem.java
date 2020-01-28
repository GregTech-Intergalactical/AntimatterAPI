package muramasa.antimatter.example;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.item.Item;

public class ExampleItem extends Item implements IAntimatterObject, ITextureProvider, IModelProvider {

    private String namespace, id;

    public ExampleItem(String namespace, String id, Item.Properties properties) {
        super(properties);
        this.namespace = namespace;
        this.id = id;
        setRegistryName(getNamespace(), getId());
        AntimatterAPI.register(ExampleItem.class, this);
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture(getNamespace(), "item/" + getId())};
    }
}
