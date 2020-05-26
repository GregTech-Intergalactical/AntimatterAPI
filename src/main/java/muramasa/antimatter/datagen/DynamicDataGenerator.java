package muramasa.antimatter.datagen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

/**
 * This is an extension of DataGenerator that does not scream time taken and such,
 */
public class DynamicDataGenerator extends DataGenerator {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ObjectList<IDataProvider> PROVIDERS = new ObjectArrayList<>();

    public DynamicDataGenerator(Path output, Collection<Path> input) {
        super(output, input);
    }

    @Override
    public void run() throws IOException {
        DirectoryCache cache = new DirectoryCache(this.getOutputFolder(), "cache");
        cache.addProtectedPath(this.getOutputFolder().resolve("version.json"));
        for(IDataProvider provider : PROVIDERS) {
            provider.act(cache);
        }
        cache.writeCache();
    }

    public void addProvider() {

    }

    public void addProviders(IDataProvider... providers) {
        PROVIDERS.addAll(Arrays.asList(providers));
    }

}
