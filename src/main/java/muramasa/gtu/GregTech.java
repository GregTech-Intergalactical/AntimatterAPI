package muramasa.gtu;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.blocks.GTItemBlock;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.data.Structures;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.network.GregTechNetwork;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.api.ore.BlockRock;
import muramasa.gtu.api.ore.OreType;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.worldgen.GregTechWorldGenerator;
import muramasa.gtu.common.Data;
import muramasa.gtu.common.events.OreGenHandler;
import muramasa.gtu.common.network.GuiHandler;
import muramasa.gtu.integration.ForestryRegistrar;
import muramasa.gtu.integration.GalacticraftRegistrar;
import muramasa.gtu.integration.UndergroundBiomesRegistrar;
import muramasa.gtu.integration.ctx.GregTechTweaker;
import muramasa.gtu.integration.top.TheOneProbePlugin;
import muramasa.gtu.proxy.IProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Mod(modid = Ref.MODID, name = Ref.NAME, version = Ref.VERSION, dependencies = Ref.DEPENDS, useMetadata = true)
public class GregTech {

    @SidedProxy(clientSide = Ref.CLIENT, serverSide = Ref.SERVER)
    public static IProxy PROXY;

    @Mod.Instance
    public static GregTech INSTANCE;

    public static Logger LOGGER;

    static {
        GregTechNetwork.init();
        GregTechAPI.addRegistrar(new ForestryRegistrar());
        GregTechAPI.addRegistrar(new GalacticraftRegistrar());
        if (Utils.isModLoaded(Ref.MOD_UB)) GregTechAPI.addRegistrar(new UndergroundBiomesRegistrar());
        if (Utils.isModLoaded(Ref.MOD_CT)) GregTechAPI.addRegistrar(new GregTechTweaker());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LOGGER = e.getModLog();
        PROXY.preInit(e);

        GregTechAPI.onRegistration(RegistrationEvent.INIT);

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());
        GTCapabilities.register();

        OreGenHandler.init();
        MinecraftForge.EVENT_BUS.register(this);

        Ref.CONFIG = new File(e.getModConfigurationDirectory(), "GregTech/");

        new GregTechWorldGenerator();

        Data.init();
        Machines.init();
        Guis.init();
        Structures.init();

        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL);
        GregTechAPI.onRegistration(RegistrationEvent.DATA);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);

        GregTechAPI.onRegistration(RegistrationEvent.DATA_FINAL);

        if (Utils.isModLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();

        Ref.TAB_ITEMS.setStack(Data.DebugScanner.get(1));
        Ref.TAB_MATERIALS.setStack(Materials.Aluminium.getIngot(1));
        Ref.TAB_MACHINES.setStack(Data.DebugScanner.get(1));
        Ref.TAB_BLOCKS.setStack(Data.DebugScanner.get(1));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
        GregTechAPI.onRegistration(RegistrationEvent.WORLDGEN);
        GregTechWorldGenerator.init();
        if (!Configs.WORLD.ORE_JSON_RELOADING) GregTechWorldGenerator.reload();
        GregTechAPI.onRegistration(RegistrationEvent.RECIPE);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent e) {
        if (Configs.WORLD.ORE_JSON_RELOADING) GregTechWorldGenerator.reload();
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        List<MaterialType> types = GregTechAPI.all(MaterialType.class);
        List<Material> materials = GregTechAPI.all(Material.class);
        types.forEach(t -> materials.forEach(m -> {
            if (t.allowGeneration(m)) new MaterialItem(t, m);
        }));
        Arrays.stream(ToolType.VALUES).forEach(ToolType::instantiate);
        GregTechAPI.ITEMS.forEach(i -> e.getRegistry().register(i));
        GregTechAPI.BLOCKS.forEach(b -> e.getRegistry().register(b instanceof IItemBlock ? ((IItemBlock) b).getItemBlock(b) : new GTItemBlock(b)));
        GregTechAPI.onRegistration(RegistrationEvent.ITEM);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        //TODO, possibly loop over stone sets?
        MaterialType.ORE.getMats().forEach(m -> new BlockOre(OreType.NORMAL, m, "default"));
        MaterialType.ORE_SMALL.getMats().forEach(m -> new BlockOre(OreType.SMALL, m, "default"));
        new BlockRock(StoneType.STONE);

        GregTechAPI.all(Machine.class).forEach(m -> GregTechAPI.register(m.getTileClass()));
        StoneType.getGenerating().forEach(type -> GregTechAPI.register(new BlockStone(type)));
        GregTechAPI.BLOCKS.forEach(b -> e.getRegistry().register(b));
        GregTechAPI.TILES.forEach(c -> GameRegistry.registerTileEntity(c, new ResourceLocation(Ref.MODID, c.getName())));
    }
}
