package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import net.minecraft.data.DataGenerator;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ProvidersEvent {
    private final List<IAntimatterProvider> providers = new ObjectArrayList<>(10);

    public final DataGenerator generator;

    public ProvidersEvent(DataGenerator generator) {
        this.generator = generator;
    }

    public void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> provider) {
        providers.add(provider.apply(generator));
    }

    public Collection<IAntimatterProvider> getProviders() {
        return providers;
    }
}
