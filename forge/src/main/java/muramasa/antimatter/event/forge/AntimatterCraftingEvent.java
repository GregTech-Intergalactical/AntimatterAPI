package muramasa.antimatter.event.forge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.ICraftingLoader;
import muramasa.antimatter.registration.IAntimatterRegistrar;

import java.util.Collection;
import java.util.List;

public class AntimatterCraftingEvent extends AntimatterEvent {

    private final CraftingEvent event;

    public AntimatterCraftingEvent(IAntimatterRegistrar registrar, CraftingEvent event) {
        super(registrar);
        this.event = event;
    }

    public CraftingEvent getEvent() {
        return event;
    }

    public void addLoader(ICraftingLoader loader) {
        this.event.addLoader(loader);
    }

    public Collection<ICraftingLoader> getLoaders() {
        return event.getLoaders();
    }
}
