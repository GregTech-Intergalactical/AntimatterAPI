package muramasa.gtu.api.items;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelProvider;
import muramasa.gtu.api.registration.ITextureProvider;
import muramasa.gtu.api.texture.Texture;
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
