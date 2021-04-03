package muramasa.antimatter;

import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.datagen.resources.DynamicDataPackFinder;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.CommonHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);

        /* Lifecycle events */
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::serverSetup);

        providers();
        AntimatterAPI.init();
        AntimatterWorldGenerator.init();
    }

    private void providers() {
        final AntimatterBlockTagProvider[] p = new AntimatterBlockTagProvider[1];
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME.concat(" BlockStates"), g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
        AntimatterAPI.addProvider(Ref.ID, g -> {
            p[0] = new AntimatterBlockTagProvider(Ref.ID, Ref.NAME.concat(" Block Tags"), false, g, new ExistingFileHelperOverride());
            return p[0];
        });
        AntimatterAPI.addProvider(Ref.ID, g ->
                new AntimatterItemTagProvider(Ref.ID,Ref.NAME.concat(" Item Tags"), false, g, p[0], new ExistingFileHelperOverride()));
        AntimatterAPI.addProvider(Ref.ID, g ->
                new AntimatterRecipeProvider(Ref.ID,Ref.NAME.concat(" Recipes"), g));
        AntimatterAPI.addProvider(Ref.ID, g -> new AntimatterBlockLootProvider(Ref.ID,Ref.NAME.concat( " Loot generator"),g));
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup(e);
        AntimatterAPI.runAssetProvidersDynamically();
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        AntimatterAPI.getClientDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(e::enqueueWork));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
        CommonHandler.setup(e);
        LOGGER.info("AntimatterAPI Data Processing has Finished. All Data Objects can now be Modified!");
        AntimatterAPI.getCommonDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(e::enqueueWork));
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent e) {
        ServerHandler.setup(e);
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        MinecraftForge.EVENT_BUS.register(DynamicDataPackFinder.class);
        AntimatterAPI.getServerDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(e::enqueueWork));
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Dist side) {
        if (event == RegistrationEvent.DATA_INIT) {
            Data.init(side);
        }
    }
}
