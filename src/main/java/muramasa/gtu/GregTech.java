package muramasa.gtu;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.blocks.GTItemBlock;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.gui.MenuHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.data.Structures;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.network.GregTechNetwork;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.api.ore.OreType;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.tools.GregTechToolType;
import muramasa.gtu.common.Data;
import muramasa.gtu.proxy.ClientHandler;
import muramasa.gtu.proxy.IProxyHandler;
import muramasa.gtu.proxy.ServerHandler;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

@Mod(Ref.MODID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class GregTech {

    public static GregTech INSTANCE;
    public static IProxyHandler PROXY = DistExecutor.runForDist(() -> ClientHandler::new, () -> ServerHandler::new);
    public static GregTechNetwork NETWORK = new GregTechNetwork();
    public static Logger LOGGER = LogManager.getLogger(Ref.MODID);

    public GregTech() {
        INSTANCE = this;
        DistExecutor.runWhenOn(Dist.CLIENT, () -> ClientHandler::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        //GregTechAPI.addRegistrar(new ForestryRegistrar());
        //GregTechAPI.addRegistrar(new GalacticraftRegistrar());
        //if (ModList.get().isLoaded(Ref.MOD_UB)) GregTechAPI.addRegistrar(new UndergroundBiomesRegistrar());
        //if (ModList.get().isLoaded(Ref.MOD_CT)) GregTechAPI.addRegistrar(new GregTechTweaker());
    }

    private static void buildData() {
        Data.init();
        Machines.init();
        Structures.init();
    }

    private void setup(final FMLCommonSetupEvent e) {
        GregTechAPI.onRegistration(RegistrationEvent.INIT);

        GTCapabilities.register();

        //OreGenHandler.init();

        //TODO Ref.CONFIG = new File(e.getModConfigurationDirectory(), "GregTech/");

        //new GregTechWorldGenerator();



        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL);
        GregTechAPI.onRegistration(RegistrationEvent.DATA);

        GregTechAPI.onRegistration(RegistrationEvent.DATA_READY);

        //if (ModList.get().isLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();

        Ref.TAB_ITEMS.setIcon(Data.DebugScanner.get(1));
        Ref.TAB_MATERIALS.setIcon(Materials.Aluminium.getIngot(1));
        Ref.TAB_MACHINES.setIcon(Data.DebugScanner.get(1));
        Ref.TAB_BLOCKS.setIcon(Data.DebugScanner.get(1));

        GregTechAPI.onRegistration(RegistrationEvent.WORLDGEN);
        //GregTechWorldGenerator.init();
        //if (!Configs.WORLD.ORE_JSON_RELOADING) GregTechWorldGenerator.reload();
        GregTechAPI.onRegistration(RegistrationEvent.RECIPE);

    }

    @SubscribeEvent
    public void serverAboutToStart(FMLServerAboutToStartEvent e) {
        //if (Configs.WORLD.ORE_JSON_RELOADING) GregTechWorldGenerator.reload();
    }

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
        List<MaterialType> types = GregTechAPI.all(MaterialType.class);
        List<Material> materials = GregTechAPI.all(Material.class);
        types.forEach(t -> materials.forEach(m -> {
            if (t.allowGeneration(m)) new MaterialItem(t, m);
        }));
        Arrays.stream(GregTechToolType.VALUES).forEach(GregTechToolType::instantiate);
        GregTechAPI.all(Item.class).forEach(i -> e.getRegistry().register(i));
        GregTechAPI.all(Block.class).forEach(b -> e.getRegistry().register(b instanceof IItemBlock ? ((IItemBlock) b).getItemBlock(b) : new GTItemBlock(b)));
        GregTechAPI.onRegistration(RegistrationEvent.ITEM);
    }

    @SubscribeEvent
    public static void onBlockRegistry(final RegistryEvent.Register<Block> e) {
        buildData();
        MaterialType.ORE.all().forEach(m -> Arrays.stream(StoneType.getAll()).forEach(s -> new BlockOre(m, OreType.NORMAL, s)));
        MaterialType.ORE_SMALL.all().forEach(m -> Arrays.stream(StoneType.getAll()).forEach(s -> new BlockOre(m, OreType.SMALL, s)));
        MaterialType.BLOCK.all().forEach(m -> new BlockStorage(m, MaterialType.BLOCK));
        MaterialType.FRAME.all().forEach(m -> new BlockStorage(m, MaterialType.FRAME));
        //new BlockRock(StoneType.STONE);
        //GregTechAPI.all(Machine.class).forEach(m -> GregTechAPI.register(m.getTileClass()));
        StoneType.getStoneGenerating().forEach(BlockStone::new);
        GregTechAPI.all(Block.class).forEach(b -> e.getRegistry().register(b));
        GregTechAPI.onRegistration(RegistrationEvent.BLOCK);
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> e) {
        GregTechAPI.all(TileEntityType.class).forEach(t -> e.getRegistry().register(t));
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e) {
        GregTechAPI.onRegistration(RegistrationEvent.GUI);
        GregTechAPI.all(MenuHandler.class).forEach(h -> e.getRegistry().register(h.getContainerType()));
    }
}
