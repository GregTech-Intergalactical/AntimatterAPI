package muramasa.antimatter;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemTagProvider;
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
import net.minecraftforge.client.model.ModelLoaderRegistry;
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

    //todo: datapack
    public Antimatter() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new); // todo: scheduled to change in new Forge

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);

        // ModLoadingContext.get().getActiveContainer().dispatchConfigEvent();

        Minecraft.getInstance().getResourcePackList().addPackFinder(Ref.PACK_FINDER);

        // ModelLoaderRegistry.registerLoader(AntimatterModelManager.LOADER_MAIN.getLoc(), AntimatterModelManager.LOADER_MAIN);
        // ModelLoaderRegistry.registerLoader(AntimatterModelManager.LOADER_DYNAMIC.getLoc(), AntimatterModelManager.LOADER_DYNAMIC);
        // ModelLoaderRegistry.registerLoader(AntimatterModelManager.LOADER_PIPE.getLoc(), AntimatterModelManager.LOADER_PIPE);

        eventBus.addListener(ClientHandler::onItemColorHandler);
        eventBus.addListener(ClientHandler::onBlockColorHandler);

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::serverSetup);
        eventBus.addListener(EventPriority.LOWEST, this::dataSetup);

        AntimatterAPI.addRegistrar(INSTANCE);
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup(e);
        AntimatterAPI.getClientDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        AntimatterAPI.runProvidersDynamically(ResourceMethod.DYNAMIC_PACK); // in common as data will be setup later
        CommonHandler.setup(e);
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
        if (e.includeClient()) AntimatterAPI.onProviderInit(Ref.ID, gen);
        if (e.includeServer()) gen.addProvider(new AntimatterItemTagProvider(Ref.ID, Ref.NAME.concat(" Item Tags"), false, gen));
    }

    @Override
    public String getId() {
        return Ref.ID;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case DATA_INIT:
                Data.init();
                break;
//            case DATA_READY:
//                AntimatterAPI.registerCover(Data.COVER_NONE);
//                AntimatterAPI.registerCover(Data.COVER_OUTPUT);
//                break;
        }
    }
}
