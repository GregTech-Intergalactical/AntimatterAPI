package muramasa.antimatter.datagen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IDataProvider;

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
            provider.run(null);
        }
    }

    public void addProviders(IDataProvider... providers) {
        PROVIDERS.addAll(Arrays.asList(providers));
    }
}
