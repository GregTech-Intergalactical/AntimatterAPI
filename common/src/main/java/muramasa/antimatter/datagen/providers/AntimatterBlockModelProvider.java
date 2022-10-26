package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public class AntimatterBlockModelProvider extends AntimatterModelProvider<AntimatterBlockModelBuilder> {

    private final String name;

    public AntimatterBlockModelProvider(String modid, String name) {
        super(modid, BLOCK_FOLDER, AntimatterBlockModelBuilder::new);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
