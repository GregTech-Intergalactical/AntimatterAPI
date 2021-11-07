package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class AntimatterProvidersEvent extends AntimatterEvent {

    private final List<IAntimatterProvider> providers = new ObjectArrayList<>(10);
    private final Dist dist;
    public final DataGenerator generator;

    public AntimatterProvidersEvent(DataGenerator generator, Dist type, IAntimatterRegistrar registrar) {
        super(registrar);
        this.dist = type;
        this.generator = generator;
    }

    public Dist getSide() {
        return dist;
    }

    public void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> provider) {
        providers.add(provider.apply(generator));
    }

    public Collection<IAntimatterProvider> getProviders() {
        return providers;
    }
}
