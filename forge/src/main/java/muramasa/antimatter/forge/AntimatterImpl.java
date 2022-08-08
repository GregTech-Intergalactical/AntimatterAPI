package muramasa.antimatter.forge;


import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.AntimatterMod;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.forge.AntimatterCraftingEvent;
import muramasa.antimatter.event.forge.AntimatterProvidersEvent;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import static muramasa.antimatter.Antimatter.LOGGER;

@Mod(Ref.ID)
public class AntimatterImpl {
    public AntimatterImpl(){
        AntimatterAPI.setSIDE(FMLEnvironment.dist.isClient() ? Side.CLIENT : Side.SERVER);
        new Antimatter();
        Antimatter.PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new); // todo: scheduled to
        // change in new Forge
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);

        /* Lifecycle events */
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::serverSetup);
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(EventPriority.LOWEST, this::onGatherData);

        eventBus.addListener(this::addCraftingLoaders);
        eventBus.addListener(this::providers);
    }

    private void addCraftingLoaders(AntimatterCraftingEvent ev) {
        Antimatter.INSTANCE.addCraftingLoaders(ev.getEvent());
    }

    private void providers(AntimatterProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev.getEvent());
        KubeJSRegistrar.providerEvent(ev.getEvent());
    }

    private void onGatherData(GatherDataEvent event){
        AntimatterMod.onGatherData(event.getGenerator(), event.includeClient(), event.includeServer());
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        AntimatterDynamics.runDataProvidersDynamically();
        e.enqueueWork(() -> AntimatterAPI.getClientDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during client setup: " + ex.getMessage());
                }
            }
        }));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        CommonHandler.setup();
        LOGGER.info("AntimatterAPI Data Processing has Finished. All Data Objects can now be Modified!");
        e.enqueueWork(() -> AntimatterAPI.getCommonDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during common setup: " + ex.getMessage());
                }
            }
        }));
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent e) {
        ServerHandler.setup();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        AntimatterDynamics.runDataProvidersDynamically();
        e.enqueueWork(() -> AntimatterAPI.getServerDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    LOGGER.warn("Caught error during server setup: " + ex.getMessage());
                }
            }
        }));
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
    }
}
