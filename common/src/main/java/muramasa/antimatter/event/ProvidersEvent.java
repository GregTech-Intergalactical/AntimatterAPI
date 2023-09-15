package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.registration.Side;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ProvidersEvent {
    private final List<IAntimatterProvider> providers = new ObjectArrayList<>(10);

    public final Side side;

    public ProvidersEvent(Side side) {
        this.side = side;
    }

    public void addProvider(String domain, Supplier<IAntimatterProvider> provider) {
        providers.add(provider.get());
    }

    public Collection<IAntimatterProvider> getProviders() {
        return providers;
    }

    public Side getSide() {
        return side;
    }
}
