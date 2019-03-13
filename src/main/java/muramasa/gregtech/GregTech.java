package muramasa.gregtech;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.data.Guis;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.data.Structures;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.events.EventHandler;
import muramasa.gregtech.integration.jei.GregTechJEIPlugin;
import muramasa.gregtech.loaders.MachineRecipeLoader;
import muramasa.gregtech.loaders.MaterialRecipeLoader;
import muramasa.gregtech.proxy.GuiHandler;
import muramasa.gregtech.proxy.IProxy;
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

    @SidedProxy(clientSide = "muramasa.gregtech.proxy.ClientProxy", serverSide = "muramasa.gregtech.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static GregTech INSTANCE;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        logger = e.getModLog();
        proxy.preInit(e);

        GregTechJEIPlugin.registerCategory(RecipeMap.ORE_BY_PRODUCTS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechJEIPlugin.registerCategory(RecipeMap.PLASMA_FUELS, Guis.MULTI_DISPLAY_COMPACT);

        new EventHandler().init();
        GTCapabilities.register();

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());

        //Init GT Data
        Materials.init();
        Machines.init();
        Guis.init();
        Structures.init();
        MaterialItem.init();
        StandardItem.init();
        ItemType.init();
        //TODO call methods to init addon data
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        //TODO new MaterialRecipeLoader().run();
        MachineRecipeLoader.init();
        MaterialRecipeLoader.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        proxy.serverStarting(e);
    }
}
