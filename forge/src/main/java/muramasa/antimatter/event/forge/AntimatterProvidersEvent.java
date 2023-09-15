package muramasa.antimatter.event.forge;

import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.Collection;
import java.util.function.Supplier;

public class AntimatterProvidersEvent extends AntimatterEvent implements IModBusEvent {
    public final ProvidersEvent event;

    public AntimatterProvidersEvent(ProvidersEvent event, IAntimatterRegistrar registrar) {
        super(registrar);
        this.event = event;
    }

    public ProvidersEvent getEvent() {
        return event;
    }

    public void addProvider(String domain, Supplier<IAntimatterProvider> provider) {
        event.addProvider(domain, provider);
    }

    public Collection<IAntimatterProvider> getProviders() {
        return event.getProviders();
    }
}
