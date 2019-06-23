package muramasa.gtu;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.GTItemBlock;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.data.Structures;
import muramasa.gtu.api.network.GregTechNetwork;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import muramasa.gtu.common.events.OreGenHandler;
import muramasa.gtu.common.network.GuiHandler;
import muramasa.gtu.integration.ctx.GregTechTweaker;
import muramasa.gtu.integration.fr.ForestryRegistrar;
import muramasa.gtu.integration.gc.GalacticraftRegistrar;
import muramasa.gtu.integration.top.TheOneProbePlugin;
import muramasa.gtu.loaders.OreDictLoader;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

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
        if (Utils.isModLoaded(Ref.MOD_CT)) GregTechTweaker.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LOGGER = e.getModLog();
        PROXY.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());
        GTCapabilities.register();

        OreGenHandler.init();
        MinecraftForge.EVENT_BUS.register(this);

        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL);
        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL_INIT);

        Machines.init();
        Guis.init();
        Structures.init();
        Data.init();

        Ref.TAB_ITEMS.setStack(Data.DebugScanner.get(1));
        Ref.TAB_MATERIALS.setStack(Materials.Aluminium.getIngot(1));
        Ref.TAB_MACHINES.setStack(Data.DebugScanner.get(1));
        Ref.TAB_BLOCKS.setStack(Data.DebugScanner.get(1));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
        if (Utils.isModLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();
        OreDictLoader.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
        GregTechAPI.onRegistration(RegistrationEvent.RECIPE);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        GregTechAPI.ITEMS.forEach(i -> e.getRegistry().register(i));
        GregTechAPI.BLOCKS.forEach(b -> e.getRegistry().register(new GTItemBlock(b)));
        GregTechAPI.onRegistration(RegistrationEvent.ITEM);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        GregTechAPI.BLOCKS.forEach(b -> e.getRegistry().register(b));
        GregTechAPI.TILES.forEach(c -> GameRegistry.registerTileEntity(c, new ResourceLocation(Ref.MODID, c.getName())));
    }
}
