package muramasa.gregtech;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.data.Guis;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.data.Structures;
import muramasa.gregtech.api.interfaces.GregTechRegistrar;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.events.OreGenHandler;
import muramasa.gregtech.integration.fr.ForestryRegistrar;
import muramasa.gregtech.integration.gc.GalacticraftRegistrar;
import muramasa.gregtech.integration.jei.GregTechJEIPlugin;
import muramasa.gregtech.loaders.GregTechRegistry;
import muramasa.gregtech.loaders.InternalRegistrar;
import muramasa.gregtech.proxy.GuiHandler;
import muramasa.gregtech.proxy.IProxy;
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

    @SidedProxy(clientSide = "muramasa.gregtech.proxy.ClientProxy", serverSide = "muramasa.gregtech.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static GregTech INSTANCE;

    public static Logger logger;

    public static GregTechRegistrar INTERNAL_REGISTRAR = new InternalRegistrar();

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

        if (Ref.DISABLE_VANILLA_ORE_GENERATION) {
            MinecraftForge.EVENT_BUS.register(new OreGenHandler());
        }

        GregTechJEIPlugin.registerCategory(RecipeMap.ORE_BY_PRODUCTS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechJEIPlugin.registerCategory(RecipeMap.PLASMA_FUELS, Guis.MULTI_DISPLAY_COMPACT);

        for (GregTechRegistrar registrar : GregTechRegistry.getRegistrars()) {
            registrar.onMaterialRegistration();
            registrar.onMaterialInit();
        }
        INTERNAL_REGISTRAR.onMaterialRegistration();
        INTERNAL_REGISTRAR.onMaterialInit();
        Machines.init();
        Guis.init();
        Structures.init();
        MaterialItem.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        INTERNAL_REGISTRAR.onCraftingRecipeRegistration();
        INTERNAL_REGISTRAR.onMachineRecipeRegistration();
        INTERNAL_REGISTRAR.onMaterialRecipeRegistration();
        for (GregTechRegistrar registrar : GregTechRegistry.getRegistrars()) {
            registrar.onCraftingRecipeRegistration();
            registrar.onMachineRecipeRegistration();
            registrar.onMaterialRecipeRegistration();
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent e) {
        proxy.serverStarting(e);
    }
}
