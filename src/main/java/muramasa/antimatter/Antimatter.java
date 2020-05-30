package muramasa.antimatter;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ref.ID)
public class Antimatter implements IAntimatterRegistrar {

    public static Antimatter INSTANCE;
    public static final AntimatterNetwork NETWORK = new AntimatterNetwork();
    public static final Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY;

    static {
        AntimatterAPI.runBackgroundProviders();
    }

    public Antimatter() {
        INSTANCE = this;
        AntimatterAPI.addRegistrar(INSTANCE);
        PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new); // todo: scheduled to change in new Forge

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);

        eventBus.addListener(ClientHandler::onItemColorHandler);
        eventBus.addListener(ClientHandler::onBlockColorHandler);

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::serverSetup);
        eventBus.addListener(EventPriority.LOWEST, this::dataSetup);

    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup(e);
        AntimatterAPI.runAssetProvidersDynamically();
        AntimatterAPI.getClientDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        CommonHandler.setup(e);
        LOGGER.info("AntimatterAPI Data Processing has Finished. All Data Objects can now be Modified!");
        AntimatterAPI.onRegistration(RegistrationEvent.READY);
        // AntimatterAPI.onRegistration(RegistrationEvent.RECIPE); Recipes should be part of the 'forge' registry

        AntimatterWorldGenerator.init();
        AntimatterCaps.register();

        AntimatterAPI.getCommonDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));

        //if (ModList.get().isLoaded(Ref.MOD_CT)) GregTechAPI.addRegistrar(new GregTechTweaker());
        //if (ModList.get().isLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent e) {
        ServerHandler.setup(e);
        AntimatterAPI.getServerDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
    }

    public void dataSetup(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) AntimatterAPI.onProviderInit(Ref.ID, gen, Dist.CLIENT);
        if (e.includeServer()) AntimatterAPI.onProviderInit(Ref.ID, gen, Dist.DEDICATED_SERVER);
    }

    @Override
    public String getId() {
        return Ref.ID;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case DATA_INIT:
                AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
                AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
                AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterLanguageProvider(Ref.ID, Ref.NAME.concat(" Localization"), "en_us", g));
                AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockTagProvider(Ref.ID, Ref.NAME.concat(" Block Tags"), false, g));
                AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterItemTagProvider(Ref.ID, Ref.NAME.concat(" Item Tags"), false, g));
                Data.init();
                break;
//            case DATA_READY:
//                AntimatterAPI.registerCover(Data.COVER_NONE);
//                AntimatterAPI.registerCover(Data.COVER_OUTPUT);
//                break;
        }
    }
}
