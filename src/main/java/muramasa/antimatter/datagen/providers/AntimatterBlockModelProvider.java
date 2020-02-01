package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.function.Function;

public abstract class AntimatterBlockModelProvider extends BlockModelProvider {

    public AntimatterBlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
        Function<ResourceLocation, BlockModelBuilder> factoryOverride = loc -> new AntimatterBlockModelBuilder(loc, existingFileHelper);
        ObfuscationReflectionHelper.setPrivateValue(ModelProvider.class, this, factoryOverride, "factory");
    }
}
