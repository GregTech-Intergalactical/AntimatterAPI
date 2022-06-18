package net.minecraftforge.client.model.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;

public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
    }

    public ItemModelProvider(DataGenerator generator, String modid, Function<ResourceLocation, ItemModelBuilder> factory, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, factory, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Item Models: " + modid;
    }
}
