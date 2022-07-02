package muramasa.antimatter.fabric;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;

public class AntimatterImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        EntrypointUtils.invoke("antimatter", IAntimatterRegistrarInitializer.class, IAntimatterRegistrarInitializer::onRegistrarInit);
        AntimatterRegistration.onRegister();
        CraftingEvents.CRAFTING.register(Antimatter.INSTANCE::addCraftingLoaders);
        ProviderEvents.PROVIDERS.register(this::providers);
    }

    private void providers(ProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev);
        KubeJSRegistrar.providerEvent(ev);
    }
}
