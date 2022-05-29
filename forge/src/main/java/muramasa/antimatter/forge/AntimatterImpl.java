package muramasa.antimatter.forge;


import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.event.forge.AntimatterCraftingEvent;
import muramasa.antimatter.event.forge.AntimatterProvidersEvent;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
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

@Mod(Ref.ID)
public class AntimatterImpl {
    public AntimatterImpl(){
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

        MinecraftForge.EVENT_BUS.addListener(this::addCraftingLoaders);
        MinecraftForge.EVENT_BUS.addListener(this::providers);
    }

    private void addCraftingLoaders(AntimatterCraftingEvent ev) {
        Antimatter.INSTANCE.addCraftingLoaders(ev.getEvent());
    }

    private void providers(AntimatterProvidersEvent ev) {
        Antimatter.INSTANCE.providers(ev.getEvent());
        KubeJSRegistrar.providersEvent(ev.getEvent());
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup();
        // AntimatterAPI.runAssetProvidersDynamically();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        e.enqueueWork(() -> AntimatterAPI.getClientDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    Antimatter.LOGGER.warn("Caught error during client setup: " + ex.getMessage());
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
                    Antimatter.LOGGER.warn("Caught error during common setup: " + ex.getMessage());
                }
            }
        }));
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent e) {
        ServerHandler.setup();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        MinecraftForge.EVENT_BUS.register(DynamicDataPackFinder.class);
        e.enqueueWork(() -> AntimatterAPI.getServerDeferredQueue().ifPresent(t -> {
            for (Runnable r : t) {
                try {
                    r.run();
                } catch (Exception ex) {
                    Antimatter.LOGGER.warn("Caught error during server setup: " + ex.getMessage());
                }
            }
        }));
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
    }
}
