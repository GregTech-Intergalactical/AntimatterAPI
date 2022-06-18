package net.minecraftforge.client.model.generators;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelBuilder extends ModelBuilder<BlockModelBuilder> {

    public BlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }
}
