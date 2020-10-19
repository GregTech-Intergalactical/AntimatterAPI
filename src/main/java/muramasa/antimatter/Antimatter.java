package muramasa.antimatter;

import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
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
        AntimatterAPI.runBackgroundProviders();
    }

    public Antimatter() {
        super();
        INSTANCE = this;

        PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new); // todo: scheduled to change in new Forge

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);

        eventBus.addListener(ClientHandler::onItemColorHandler);
        eventBus.addListener(ClientHandler::onBlockColorHandler);
        eventBus.addListener(ClientHandler::onModelRegistry);

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::serverSetup);

        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterLanguageProvider(Ref.ID, Ref.NAME.concat(" Localization"), "en_us", g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockTagProvider(Ref.ID, Ref.NAME.concat(" Block Tags"), false, g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterItemTagProvider(Ref.ID, Ref.NAME.concat(" Item Tags"), false, g));
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup(e);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AntimatterTextureStitcher::onTextureStitch);
        AntimatterAPI.runAssetProvidersDynamically();
        AntimatterAPI.getClientDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        CommonHandler.setup(e);
        AntimatterAPI.init();
        LOGGER.info("AntimatterAPI Data Processing has Finished. All Data Objects can now be Modified!");
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        AntimatterAPI.getCommonDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
        //if (ModList.get().isLoaded(Ref.MOD_CT)) GregTechAPI.addRegistrar(new GregTechTweaker());
        //if (ModList.get().isLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent e) {
        ServerHandler.setup(e);
        AntimatterAPI.getServerDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case DATA_INIT:
                Data.init();
                break;
//            case DATA_READY:
//                AntimatterAPI.registerCover(Data.COVER_EMPTY);
//                AntimatterAPI.registerCover(Data.COVER_OUTPUT);
//                break;
        }
    }
}
