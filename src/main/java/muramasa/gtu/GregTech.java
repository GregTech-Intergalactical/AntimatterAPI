package muramasa.gtu;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Structures;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.common.events.OreGenHandler;
import muramasa.gtu.common.network.GuiHandler;
import muramasa.gtu.integration.fr.ForestryRegistrar;
import muramasa.gtu.integration.gc.GalacticraftRegistrar;
import muramasa.gtu.loaders.ContentLoader;
import muramasa.gtu.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Ref.MODID, name = Ref.NAME, version = Ref.VERSION, useMetadata = true)
public class GregTech {

    @SidedProxy(clientSide = Ref.CLIENT, serverSide = Ref.SERVER)
    public static IProxy proxy;

    @Mod.Instance
    public static GregTech INSTANCE;

    public static Logger logger;

    static {
        GregTechRegistry.addRegistrar(new ForestryRegistrar());
        GregTechRegistry.addRegistrar(new GalacticraftRegistrar());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        proxy.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());
        GTCapabilities.register();

        if (Ref.DISABLE_VANILLA_ORE_GENERATION) MinecraftForge.EVENT_BUS.register(new OreGenHandler());

        GregTechAPI.registerJEICategory(RecipeMap.ORE_BY_PRODUCTS, Guis.MULTI_DISPLAY_COMPACT);
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
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.CRAFTING_RECIPE);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.MATERIAL_RECIPE);
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.MACHINE_RECIPE);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        proxy.serverStarting(e);
    }
}
