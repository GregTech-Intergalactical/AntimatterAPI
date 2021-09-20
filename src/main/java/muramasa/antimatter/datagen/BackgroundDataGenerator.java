package muramasa.antimatter.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IDataProvider;
import speiger.src.collections.objects.lists.ObjectArrayList;
import speiger.src.collections.objects.lists.ObjectList;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * This is an extension of DataGenerator that does not scream time taken and such,
 */
public class BackgroundDataGenerator extends DataGenerator {

    private final ObjectList<IDataProvider> PROVIDERS = new ObjectArrayList<>();

    public BackgroundDataGenerator() {
        super(new File("").toPath(), Collections.emptySet());
    }

    @Override
    @SuppressWarnings("all")
    public void run() throws IOException {
        for (IDataProvider provider : PROVIDERS) {
            provider.act(null);
        }
    }

    public void addProviders(IDataProvider... providers) {
        PROVIDERS.addAll(Arrays.asList(providers));
    }
}
