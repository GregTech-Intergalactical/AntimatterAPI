package muramasa.antimatter;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.DynamicDataGenerator;
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
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Mod(Ref.ID)
public class Antimatter implements IAntimatterRegistrar {

    public static Antimatter INSTANCE;
    public static final AntimatterNetwork NETWORK = new AntimatterNetwork();
    public static final Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY;

    /*
    static {
        LogManager.getLogger().fatal("Something very naughty is happening...");
        // Path output = Paths.get("E:\\Programming\\Minecraft Mods\\DATA_TEST");
        Path output = Paths.get("gti/generated");
        GatherDataEvent.DataGeneratorConfig config =
                new GatherDataEvent.DataGeneratorConfig(ImmutableSet.of("gti"), output, Collections.emptySet(), true, true, true, true, true);
        // ExistingFileHelper helper = new ExistingFileHelper(ImmutableSet.of(Paths.get("E:\\Programming\\Minecraft Mods\\Repos\\GREGTECH_1.15\\GregTech\\src\\main\\resources")), true);
        DataGenerator gen = config.makeGenerator(p -> p, true);
        // DynamicDataGenerator gen = new DynamicDataGenerator(output, Collections.emptySet());
        // gen.addProvider(new ItemTagsProvider(gen));
        // gen.addProvider(new ForgeBlockTagsProvider(gen));
        gen.addProvider(new ForgeItemTagsProvider(gen));
        LogManager.getLogger().info(gen.getOutputFolder() + " is the output folder.");
        // try {
            //  gen.run();
        // } catch (IOException e) {
            // e.printStackTrace();
        // }
        config.runAll();
    }
     */

    //todo: datapack, resource pack, registration double check
    public Antimatter() {
        INSTANCE = this;
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AntimatterConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AntimatterConfig.COMMON_SPEC);

        eventBus.addListener(ClientHandler::onModelRegisterEvent);
        eventBus.addListener(ClientHandler::onItemColorHandler);
        eventBus.addListener(ClientHandler::onBlockColorHandler);

        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::serverSetup);
        eventBus.addListener(EventPriority.LOWEST, this::dataSetup);

        AntimatterAPI.addRegistrar(INSTANCE);
        AntimatterModelManager.addProvider(Ref.ID, g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        ClientHandler.setup(e);
        if (AntimatterModelManager.RESOURCE_METHOD == ResourceMethod.DYNAMIC_PACK) AntimatterModelManager.runProvidersDynamically();
        AntimatterAPI.getClientDeferredQueue().ifPresent(q -> q.iterator().forEachRemaining(DeferredWorkQueue::runLater));
    }

    private void commonSetup(final FMLCommonSetupEvent e) {
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
        if (e.includeClient()) AntimatterModelManager.onProviderInit(Ref.ID, gen);
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
