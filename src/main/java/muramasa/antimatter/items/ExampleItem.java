package muramasa.antimatter.items;

import muramasa.gtu.Ref;
import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.registration.IGregTechObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.item.Item;

public class ExampleItem extends Item implements IGregTechObject, ITextureProvider, IModelProvider {

    private String id;

    public ExampleItem(String id, Item.Properties properties) {
        super(properties);
        this.id = id;
        setRegistryName(Ref.MODID, getId());
        GregTechAPI.register(ExampleItem.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{new Texture("item/" + getId())};
    }
}
