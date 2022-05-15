package muramasa.antimatter.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.ICraftingLoader;

import java.util.Collection;
import java.util.List;

public class CraftingEvent {
    private final List<ICraftingLoader> loaders = new ObjectArrayList<>();

    public void addLoader(ICraftingLoader loader) {
        this.loaders.add(loader);
    }

    public Collection<ICraftingLoader> getLoaders() {
        return loaders;
    }
}
