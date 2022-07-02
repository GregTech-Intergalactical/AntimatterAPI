package muramasa.antimatter.event.fabric;

import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class LoaderEvents {
    public static final Event<Loader> LOADER = EventFactory.createArrayBacked(Loader.class, listeners -> (registrar, reg) -> {
        for (Loader listener : listeners) {
            listener.load(registrar, reg);
        }
    });

    public interface Loader{
        void load(IAntimatterRegistrar registrar, IRecipeRegistrate reg);
    }
}
