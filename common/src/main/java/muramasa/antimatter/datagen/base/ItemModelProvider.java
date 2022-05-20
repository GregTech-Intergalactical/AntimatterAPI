package muramasa.antimatter.datagen.base;

import net.minecraft.data.DataGenerator;

import javax.annotation.Nonnull;

public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

    public ItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Item Models: " + modid;
    }
}
