package muramasa.antimatter;

import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.loaders.MaterialRecipes;
import muramasa.antimatter.datagen.loaders.Pipes;
import muramasa.antimatter.datagen.loaders.Tools;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.datagen.resources.DynamicDataPackFinder;
import muramasa.antimatter.event.AntimatterCraftingEvent;
import muramasa.antimatter.event.AntimatterProvidersEvent;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.material.SubTag;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.recipe.RecipeBuilders;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ref.ID)
public class Antimatter extends AntimatterMod {

    public static Antimatter INSTANCE;
    public static final AntimatterNetwork NETWORK = new AntimatterNetwork();
    public static final Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY;

    static {
        // AntimatterAPI.runBackgroundProviders();
    }

    public Antimatter() {
        super();
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new); // todo: scheduled to
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

        AntimatterDynamics.addProvider(Ref.ID,
                g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
        AntimatterDynamics.addProvider(Ref.ID,
                g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
        AntimatterDynamics.addProvider(Ref.SHARED_ID,
                g -> new AntimatterBlockStateProvider(Ref.SHARED_ID, "Antimatter Shared BlockStates", g));
        AntimatterDynamics.addProvider(Ref.SHARED_ID,
                g -> new AntimatterItemModelProvider(Ref.SHARED_ID, "Antimatter Shared Item Models", g));
        AntimatterDynamics.addProvider(Ref.ID,
                g -> new AntimatterLanguageProvider(Ref.ID, Ref.NAME.concat(" en_us Localization"), "en_us", g));
        AntimatterDynamics.addProvider(Ref.SHARED_ID,
                g -> new AntimatterLanguageProvider(Ref.SHARED_ID, Ref.NAME.concat(" en_us Localization"), "en_us", g));
        AntimatterAPI.init();

        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS))
            new KubeJSRegistrar();
    }

    private void addCraftingLoaders(AntimatterCraftingEvent ev) {
        ev.addLoader(MaterialRecipes::init);
        ev.addLoader(Pipes::loadRecipes);
        ev.addLoader(Tools::init);
    }

    private void providers(AntimatterProvidersEvent ev) {
        if (ev.getSide() == Dist.CLIENT) {

        } else {
            final AntimatterBlockTagProvider[] p = new AntimatterBlockTagProvider[1];
            ev.addProvider(Ref.ID, g -> {
                p[0] = new AntimatterBlockTagProvider(Ref.ID, Ref.NAME.concat(" Block Tags"), false, g,
                        new ExistingFileHelperOverride());
                return p[0];
            });
            ev.addProvider(Ref.SHARED_ID, g -> new AntimatterFluidTagProvider(Ref.SHARED_ID,
                    "Antimatter Shared Fluid Tags", false, g, new ExistingFileHelperOverride()));
            ev.addProvider(Ref.ID, g -> new AntimatterItemTagProvider(Ref.ID, Ref.NAME.concat(" Item Tags"),
                    false, g, p[0], new ExistingFileHelperOverride()));
            ev.addProvider(Ref.ID,
                    g -> new AntimatterBlockLootProvider(Ref.ID, Ref.NAME.concat(" Loot generator"), g));
        }
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup(e);
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
        CommonHandler.setup(e);
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
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
            AntimatterKubeJS.init();
        }
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent e) {
        ServerHandler.setup(e);
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

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Dist side) {
        if (event == RegistrationEvent.DATA_INIT) {
            SlotType.init();
            RecipeBuilders.init();
            Data.init(side);
            SubTag.init();
            GuiEvent.init();
        } else if (event == RegistrationEvent.WORLDGEN_INIT) {
            AntimatterWorldGenerator.init();
        }
    }

    @Override
    public String getId() {
        return Ref.ID;
    }
}
