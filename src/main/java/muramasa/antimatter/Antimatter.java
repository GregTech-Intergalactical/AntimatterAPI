package muramasa.antimatter;

import muramasa.antimatter.blocks.AntimatterItemBlock;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
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

//        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
//            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::setup);
//        });

        Data.init();
    }

    private void setup(final FMLCommonSetupEvent e) {
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_READY);
        //Ref.TAB_ITEMS.setIcon(Data.DebugScanner.get(1));
        //Ref.TAB_MATERIALS.setIcon(Materials.Aluminium.getIngot(1));
        //Ref.TAB_MACHINES.setIcon(Data.DebugScanner.get(1));
        //Ref.TAB_BLOCKS.setIcon(Data.DebugScanner.get(1));
    }

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
        AntimatterAPI.all(Item.class).forEach(i -> e.getRegistry().register(i));
        AntimatterAPI.all(Block.class).forEach(b -> e.getRegistry().register(b instanceof IItemBlockProvider ? ((IItemBlockProvider) b).getItemBlock(b) : new AntimatterItemBlock(b)));
        //AntimatterAPI.onRegistration(RegistrationEvent.ITEM);
    }

    @SubscribeEvent
    public static void onBlockRegistry(final RegistryEvent.Register<Block> e) {
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_BUILD);
        //GregTechAPI.all(Machine.class).forEach(m -> GregTechAPI.register(m.getTileClass()));
        AntimatterAPI.all(Block.class).forEach(b -> e.getRegistry().register(b));
        //AntimatterAPI.onRegistration(RegistrationEvent.BLOCK);
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> e) {
        AntimatterAPI.all(TileEntityType.class).forEach(t -> e.getRegistry().register(t));
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        //AntimatterAPI.onRegistration(RegistrationEvent.GUI);
        AntimatterAPI.all(MenuHandler.class).forEach(h -> e.getRegistry().register(h.getContainerType()));
    }

    @SubscribeEvent
    public static void onDataGather(GatherDataEvent e) {
        if (e.includeClient()) {
            e.getGenerator().addProvider(new AntimatterItemModelProvider(Ref.ID, Ref.NAME + " Item Models", e.getGenerator()));
        }
        if (e.includeServer()) {

        }
    }

    @SubscribeEvent
    public void serverAboutToStart(FMLServerAboutToStartEvent e) {
        //if (Configs.WORLD.ORE_JSON_RELOADING) GregTechWorldGenerator.reload();
    }

    @Override
    public String getId() {
        return Ref.ID;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case DATA_READY:
                AntimatterAPI.registerCover(Data.COVER_NONE);
                AntimatterAPI.registerCover(Data.COVER_OUTPUT);
                break;
        }
    }
}
