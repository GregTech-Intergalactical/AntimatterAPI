package muramasa.antimatter.datagen.providers;

import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class AntimatterBlockModelProvider extends BlockModelProvider {

    private final String name;

    public AntimatterBlockModelProvider(DataGenerator generator, String modid, String name, ExistingFileHelper exFileHelper) {
        super(generator, modid, exFileHelper);
        this.name = name;
        Function<ResourceLocation, BlockModelBuilder> factoryOverride = loc -> new AntimatterBlockModelBuilder(loc, exFileHelper);
        ObfuscationReflectionHelper.setPrivateValue(ModelProvider.class, this, factoryOverride, "factory");
    }

    @Override
    protected void registerModels() { }

    @Override
    public String getName() {
        return name;
    }

}
