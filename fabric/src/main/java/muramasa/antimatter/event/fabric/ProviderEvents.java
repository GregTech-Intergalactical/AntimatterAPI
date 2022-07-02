package muramasa.antimatter.event.fabric;

import muramasa.antimatter.event.ProvidersEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ProviderEvents {
    public static final Event<Providing> PROVIDERS = EventFactory.createArrayBacked(Providing.class, listeners -> event -> {
        for (Providing listener : listeners) {
            listener.onProvidersInit(event);
        }
    });

    public interface Providing {
        void onProvidersInit(ProvidersEvent event);
    }
}
