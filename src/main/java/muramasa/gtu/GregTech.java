package muramasa.gtu;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Structures;
import muramasa.gtu.api.network.FluidStackMessage;
import muramasa.gtu.api.network.MachineNetworkEvent;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.events.OreGenHandler;
import muramasa.gtu.common.network.GuiHandler;
import muramasa.gtu.integration.ctx.GregTechTweaker;
import muramasa.gtu.integration.fr.ForestryRegistrar;
import muramasa.gtu.integration.gc.GalacticraftRegistrar;
import muramasa.gtu.integration.top.TheOneProbePlugin;
import muramasa.gtu.loaders.ContentLoader;
import muramasa.gtu.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = Ref.MODID, name = Ref.NAME, version = Ref.VERSION, dependencies = Ref.DEPENDS, useMetadata = true)
public class GregTech {

    @SidedProxy(clientSide = Ref.CLIENT, serverSide = Ref.SERVER)
    public static IProxy PROXY;

    @Mod.Instance
    public static GregTech INSTANCE;

    public static SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Ref.MODID);

    public static Logger LOGGER;

    static {
        NETWORK.registerMessage(FluidStackMessage.FluidStackMessageHandler.class, FluidStackMessage.class, MachineNetworkEvent.FLUID.ordinal(), Side.CLIENT);

        GregTechRegistry.addRegistrar(new ForestryRegistrar());
        GregTechRegistry.addRegistrar(new GalacticraftRegistrar());
        if (Utils.isModLoaded(Ref.MOD_CT)) GregTechTweaker.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LOGGER = e.getModLog();
        PROXY.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());
        GTCapabilities.register();

        if (Ref.DISABLE_VANILLA_ORE_GENERATION) MinecraftForge.EVENT_BUS.register(new OreGenHandler());

        GregTechAPI.registerJEICategory(RecipeMap.ORE_BY_PRODUCTS, Guis.MULTI_DISPLAY_COMPACT);
//        GregTechAPI.registerJEICategory(RecipeMap.SMELTING, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.STEAM_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.GAS_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.COMBUSTION_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.NAQUADAH_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.PLASMA_FUELS, Guis.MULTI_DISPLAY_COMPACT);

        GregTechRegistry.callRegistrationEvent(RegistrationEvent.MATERIAL);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.MATERIAL_INIT);

        Machines.init();
        Guis.init();
        Structures.init();
        ContentLoader.init();
        System.out.println("GREGTECH PREINIT FINISHED");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
        if (Utils.isModLoaded(Ref.MOD_TOP)) TheOneProbePlugin.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.CRAFTING_RECIPE);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.MATERIAL_RECIPE);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.MACHINE_RECIPE);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        PROXY.serverStarting(e);
    }
}
