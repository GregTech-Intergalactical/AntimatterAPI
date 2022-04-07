package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.ICraftingLoader;
import muramasa.antimatter.registration.IAntimatterRegistrar;

import java.util.Collection;
import java.util.List;

public class AntimatterCraftingEvent extends AntimatterEvent {

    private final List<ICraftingLoader> loaders = new ObjectArrayList<>();

    public AntimatterCraftingEvent(IAntimatterRegistrar registrar) {
        super(registrar);
    }

    public void addLoader(ICraftingLoader loader) {
        this.loaders.add(loader);
    }

    public Collection<ICraftingLoader> getLoaders() {
        return loaders;
    }
}
