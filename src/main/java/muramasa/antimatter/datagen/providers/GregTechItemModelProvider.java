package muramasa.antimatter.datagen.providers;

import muramasa.gtu.Ref;
import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
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
            if (i instanceof IModelProvider) ((IModelProvider) i).onItemModelBuild(i, this);
        });
        GregTechAPI.all(Block.class).forEach(b -> {
            if (b instanceof IModelProvider) ((IModelProvider) b).onItemModelBuild(b, this);
        });
    }

    public ItemModelBuilder getBuilder(IItemProvider item) {
        return getBuilder(item.asItem().getRegistryName().getPath());
    }

    public ItemModelBuilder textured(IItemProvider item, ResourceLocation[] textures) {
        ItemModelBuilder builder = getBuilder(item);
        builder.parent(new UncheckedModelFile("item/generated"));
        for (int i = 0; i < textures.length; i++) {
            builder.texture("layer" + i, textures[i]);
        }
        return builder;
    }

    public ItemModelBuilder blockItem(Block block) {
        return blockItem(block.asItem());
    }

    public ItemModelBuilder blockItem(IItemProvider item) {
        return withExistingParent(item.asItem().getRegistryName().getPath(), modLoc("block/" + item.asItem().getRegistryName().getPath()));
    }
}
