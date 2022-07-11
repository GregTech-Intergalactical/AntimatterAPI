package muramasa.antimatter.fabric;

import io.github.fabricators_of_create.porting_lib.event.common.PlayerEvents;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.fabric.AntimatterCapsImpl;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.fabric.CraftingEvents;
import muramasa.antimatter.event.fabric.ProviderEvents;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.registration.IAntimatterRegistrarInitializer;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.fabric.AntimatterRegistration;
import muramasa.antimatter.structure.StructureCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class AntimatterImpl implements ModInitializer {
    @Override
    public void onInitialize() {
        EntrypointUtils.invoke("antimatter", IAntimatterRegistrarInitializer.class, IAntimatterRegistrarInitializer::onRegistrarInit);
        AntimatterRegistration.onRegister();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        CraftingEvents.CRAFTING.register(Antimatter.INSTANCE::addCraftingLoaders);
        ProviderEvents.PROVIDERS.register(this::providers);
        ModConfigEvent.LOADING.register(AntimatterConfig::onModConfigEvent);
        ModConfigEvent.RELOADING.register(AntimatterConfig::onModConfigEvent);
        ServerWorldEvents.UNLOAD.register((server, world) -> StructureCache.onWorldUnload(world));
        RegisterCapabilitiesEvent.REGISTER_CAPS.register(AntimatterCapsImpl::register);
    }

    private void providers(ProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev);
        KubeJSRegistrar.providerEvent(ev);
    }
}
