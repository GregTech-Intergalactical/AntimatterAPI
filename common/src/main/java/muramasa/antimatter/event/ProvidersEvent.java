package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.registration.Side;
import net.minecraft.data.DataGenerator;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ProvidersEvent {
    private final List<IAntimatterProvider> providers = new ObjectArrayList<>(10);

    public final DataGenerator generator;
    public final Side side;

    public ProvidersEvent(DataGenerator generator, Side side) {
        this.generator = generator;
        this.side = side;
    }

    public void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> provider) {
        providers.add(provider.apply(generator));
    }

    public Collection<IAntimatterProvider> getProviders() {
        return providers;
    }

    public Side getSide() {
        return side;
    }
}
