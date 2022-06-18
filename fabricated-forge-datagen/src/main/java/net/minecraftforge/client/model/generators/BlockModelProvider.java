package net.minecraftforge.client.model.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;

public abstract class BlockModelProvider extends ModelProvider<BlockModelBuilder> {

    public BlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, BLOCK_FOLDER, BlockModelBuilder::new, existingFileHelper);
    }

    public BlockModelProvider(DataGenerator generator, String modid, Function<ResourceLocation, BlockModelBuilder> factory, ExistingFileHelper existingFileHelper) {
        super(generator, modid, BLOCK_FOLDER, factory, existingFileHelper);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Block Models: " + modid;
    }
}
