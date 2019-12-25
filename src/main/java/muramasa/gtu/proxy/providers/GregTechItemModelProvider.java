package muramasa.gtu.proxy.providers;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;

public class GregTechItemModelProvider extends ItemModelProvider {

    public GregTechItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Ref.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        GregTechAPI.all(Item.class).forEach(i -> {
            if (i instanceof IModelProvider) ((IModelProvider) i).onItemModelBuild(this, getBuilder(i.getRegistryName().getPath()));
        });
    }

    @Override
    public String getName() {
        return Ref.NAME + " Item Models";
    }

    public ItemModelBuilder single(ItemModelBuilder builder, ResourceLocation texture) {
        return builder.parent(new UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    public ItemModelBuilder layered(ItemModelBuilder builder, ResourceLocation[] textures) {
        builder.parent(new UncheckedModelFile("item/generated"));
        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }
}
