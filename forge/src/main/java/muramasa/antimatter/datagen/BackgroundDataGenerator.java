package muramasa.antimatter.datagen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * This is an extension of DataGenerator that does not scream time taken and such,
 */
public class BackgroundDataGenerator extends DataGenerator {

    private final ObjectList<DataProvider> PROVIDERS = new ObjectArrayList<>();

    public BackgroundDataGenerator() {
        super(new File("").toPath(), Collections.emptySet());
    }

    @Override
    @SuppressWarnings("all")
    public void run() throws IOException {
        for (DataProvider provider : PROVIDERS) {
            provider.run(null);
        }
    }

    public void addProviders(DataProvider... providers) {
        PROVIDERS.addAll(Arrays.asList(providers));
    }
}
