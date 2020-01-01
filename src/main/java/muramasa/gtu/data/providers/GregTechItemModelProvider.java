package muramasa.gtu.data.providers;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IModelProvider;
import net.minecraft.block.Block;
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
    public String getName() {
        return Ref.NAME + " Item Models";
    }

    @Override
    protected void registerModels() {
        GregTechAPI.all(Item.class).forEach(i -> {
            if (i instanceof IModelProvider) ((IModelProvider) i).onItemModelBuild(this);
        });
        GregTechAPI.all(Block.class).forEach(b -> {
            if (b instanceof IModelProvider) {
                blockItem(b);
                ((IModelProvider) b).onItemModelBuild(this);
            }
        });
    }

    public ItemModelBuilder getBuilder(Item item) {
        return getBuilder(item.getRegistryName().getPath());
    }

    public ItemModelBuilder single(Item item, ResourceLocation texture) {
        return getBuilder(item).parent(new UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    public ItemModelBuilder layered(Item item, ResourceLocation[] textures) {
        ItemModelBuilder builder = getBuilder(item);
        builder.parent(new UncheckedModelFile("item/generated"));
        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }

    public ItemModelBuilder blockItem(Block block) {
        return withExistingParent(block.asItem().getRegistryName().getPath(), modLoc("block/" + block.asItem().getRegistryName().getPath()));
    }
}
