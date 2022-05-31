package muramasa.antimatter.datagen.providers;

import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;

@MethodsReturnNonnullByDefault
public class AntimatterBlockModelProvider extends BlockModelProvider {

    private final String name;

    public AntimatterBlockModelProvider(DataGenerator generator, String modid, String name, ExistingFileHelper exFileHelper) {
        super(generator, modid, loc -> new AntimatterBlockModelBuilder(loc, exFileHelper), exFileHelper);
        this.name = name;
    }

    @Override
    protected void registerModels() {
    }

    @Override
    public String getName() {
        return name;
    }

}
