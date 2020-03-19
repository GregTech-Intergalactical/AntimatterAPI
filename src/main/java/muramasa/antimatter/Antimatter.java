package muramasa.antimatter;

import muramasa.antimatter.advancement.trigger.AntimatterTriggers;
import muramasa.antimatter.blocks.AntimatterItemBlock;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.resources.DynamicPackFinder;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.feature.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ref.ID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Antimatter implements IAntimatterRegistrar {

    public static Antimatter INSTANCE;
    public static AntimatterNetwork NETWORK = new AntimatterNetwork();
    public static Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY;

    public Antimatter() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (AntimatterModelManager.RESOURCE_METHOD == ResourceMethod.DYNAMIC_PACK && Minecraft.getInstance() != null) {
                Minecraft.getInstance().getResourcePackList().addPackFinder(new DynamicPackFinder("antimatter_pack", "Antimatter Resources", "desc", false));
            }
        });
        AntimatterModelManager.addProvider(Ref.ID, g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME.concat(" Item Models"), g));
    }

    private void setup(final FMLCommonSetupEvent e) {
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);

        AntimatterWorldGenerator.init();

        AntimatterTriggers.init();
        AntimatterAPI.onRegistration(RegistrationEvent.RECIPE);
        //AntimatterCapabilities.register(); //TODO broken
        //if (ModList.get().isLoaded(Ref.MOD_CT)) GregTechAPI.addRegistrar(new GregTechTweaker());
        //if (ModList.get().isLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();

        if (AntimatterModelManager.RESOURCE_METHOD == ResourceMethod.DYNAMIC_PACK) AntimatterModelManager.runProvidersDynamically();
    }

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
        AntimatterAPI.all(Item.class).forEach(i -> e.getRegistry().register(i));
        AntimatterAPI.all(Block.class).forEach(b -> e.getRegistry().register(b instanceof IItemBlockProvider ? ((IItemBlockProvider) b).getItemBlock(b) : new AntimatterItemBlock(b)));
    }

    @SubscribeEvent
    public static void onBlockRegistry(final RegistryEvent.Register<Block> e) {
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_BUILD);
        AntimatterAPI.all(Block.class).forEach(b -> e.getRegistry().register(b));
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> e) {
        AntimatterAPI.all(TileEntityType.class).forEach(t -> e.getRegistry().register(t));
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        AntimatterAPI.all(MenuHandler.class).forEach(h -> e.getRegistry().register(h.getContainerType()));
    }

    @SubscribeEvent
    public static void onSoundEventRegistry(final RegistryEvent.Register<SoundEvent> e) {
        e.getRegistry().registerAll(Ref.DRILL, Ref.WRENCH);
    }

    @SubscribeEvent
    public static void onRecipeSerializerRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> e) {
        CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
    }

    @SubscribeEvent
    public static void onDataGather(GatherDataEvent e) {
        if (e.includeClient()) {
            AntimatterModelManager.onProviderInit(Ref.ID, e.getGenerator());
        }
        if (e.includeServer()) {

        }
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
            case DATA_READY:
                AntimatterAPI.registerCover(Data.COVER_NONE);
                AntimatterAPI.registerCover(Data.COVER_OUTPUT);
                break;
            case WORLDGEN_INIT:
                new FeatureStoneLayer();
                new FeatureVeinLayer();
                new FeatureOreSmall();
                new FeatureOre();
                new FeatureSurfaceRock();
                break;
        }
    }
}
