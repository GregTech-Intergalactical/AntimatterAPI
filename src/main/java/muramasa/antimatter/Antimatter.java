package muramasa.antimatter;

import muramasa.antimatter.blocks.AntimatterItemBlock;
import muramasa.antimatter.blocks.BlockStone;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.items.MaterialItem;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockRock;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.proxy.IProxyHandler;
import muramasa.antimatter.proxy.ServerHandler;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.IItemBlock;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
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

import java.util.Arrays;
import java.util.List;

@Mod(Ref.ID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class Antimatter implements IAntimatterRegistrar {

    public static Antimatter INSTANCE;
    public static AntimatterNetwork NETWORK = new AntimatterNetwork();
    public static Logger LOGGER = LogManager.getLogger(Ref.ID);
    public static IProxyHandler PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new);

    public Antimatter() {
        INSTANCE = this;
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientHandler::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        Data.init();
    }

    private void setup(final FMLCommonSetupEvent e) {
        //Ref.TAB_ITEMS.setIcon(Data.DebugScanner.get(1));
        //Ref.TAB_MATERIALS.setIcon(Materials.Aluminium.getIngot(1));
        //Ref.TAB_MACHINES.setIcon(Data.DebugScanner.get(1));
        //Ref.TAB_BLOCKS.setIcon(Data.DebugScanner.get(1));
    }

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
        List<MaterialType> types = AntimatterAPI.all(MaterialType.class);
        List<Material> materials = AntimatterAPI.all(Material.class);
        types.forEach(t -> materials.forEach(m -> {
            if (t.allowGeneration(m)) new MaterialItem(Ref.ID, t, m);
        }));
        //Arrays.stream(AntimatterToolType.VALUES).forEach(t -> t.instantiate(Ref.ID));
        AntimatterAPI.all(Item.class).forEach(i -> e.getRegistry().register(i));
        AntimatterAPI.all(Block.class).forEach(b -> e.getRegistry().register(b instanceof IItemBlock ? ((IItemBlock) b).getItemBlock(b) : new AntimatterItemBlock(b)));
        AntimatterAPI.onRegistration(RegistrationEvent.ITEM);
    }

    @SubscribeEvent
    public static void onBlockRegistry(final RegistryEvent.Register<Block> e) {
        AntimatterAPI.onRegistration(RegistrationEvent.DATA_BUILD);
        MaterialType.ORE.all().forEach(m -> Arrays.stream(StoneType.getAll()).forEach(s -> {
            new BlockOre(m, s, MaterialType.ORE);
            new BlockRock(m, s);
        }));
        MaterialType.ORE_SMALL.all().forEach(m -> Arrays.stream(StoneType.getAll()).forEach(s -> new BlockOre(m, s, MaterialType.ORE_SMALL)));
        MaterialType.BLOCK.all().forEach(m -> new BlockStorage(Ref.ID, m, MaterialType.BLOCK));
        MaterialType.FRAME.all().forEach(m -> new BlockStorage(Ref.ID, m, MaterialType.FRAME));
        //new BlockRock(StoneType.STONE);
        //GregTechAPI.all(Machine.class).forEach(m -> GregTechAPI.register(m.getTileClass()));
        StoneType.getStoneGenerating().forEach(t -> new BlockStone(Ref.ID, t));
        AntimatterAPI.all(Block.class).forEach(b -> e.getRegistry().register(b));
        AntimatterAPI.onRegistration(RegistrationEvent.BLOCK);
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> e) {
        AntimatterAPI.all(TileEntityType.class).forEach(t -> e.getRegistry().register(t));
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        AntimatterAPI.onRegistration(RegistrationEvent.GUI);
        AntimatterAPI.all(MenuHandler.class).forEach(h -> e.getRegistry().register(h.getContainerType()));
    }

    @SubscribeEvent
    public static void onDataGather(GatherDataEvent e) {
        DataGenerator gen = e.getGenerator();
        if (e.includeClient()) {
            //gen.addProvider(new AntimatterBlockStateProvider(gen, e.getExistingFileHelper()));
            //gen.addProvider(new AntimatterItemModelProvider(gen, e.getExistingFileHelper()));
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
            case ITEM:
                AntimatterAPI.registerCover(Data.COVER_NONE);
                AntimatterAPI.registerCover(Data.COVER_OUTPUT);
                break;
        }
    }
}
