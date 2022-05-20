package muramasa.antimatter.datagen.base;

import net.minecraft.data.DataGenerator;

import javax.annotation.Nonnull;

public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {

    public BlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, BLOCK_FOLDER, BlockModelBuilder::new, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Block Models: " + modid;
    }
}
