package muramasa.antimatter.datagen.providers;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;

import java.lang.reflect.Field;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class AntimatterBlockModelProvider extends BlockModelProvider {

    private final String name;

    public AntimatterBlockModelProvider(DataGenerator generator, String modid, String name, ExistingFileHelper exFileHelper) {
        super(generator, modid, exFileHelper);
        this.name = name;
        //Because forge is dumb and doesn't provide a constructor that allows you to pass your own factory
        try{
            Field field = ModelProvider.class.getDeclaredField("factory");
            field.setAccessible(true);
            Function<ResourceLocation, BlockModelBuilder> function = loc -> new AntimatterBlockModelBuilder(loc, exFileHelper);
            field.set(this, function);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void registerModels() {
    }

    @Override
    public String getName() {
        return name;
    }

}
