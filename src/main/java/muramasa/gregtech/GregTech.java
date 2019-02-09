package muramasa.gregtech;

import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.recipe.RecipeAdder;
import muramasa.gregtech.common.events.EventHandler;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.proxy.GuiHandler;
import muramasa.gregtech.proxy.IProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Ref.MODID, name = Ref.NAME, version = Ref.VERSION, useMetadata = true)
public class GregTech {

    @SidedProxy(clientSide = "muramasa.gregtech.proxy.ClientProxy", serverSide = "muramasa.gregtech.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static GregTech INSTANCE;

    public static Logger logger;

    static {
        Materials.init();
        Machines.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);

        new EventHandler().init();
        ITechCapabilities.register();

        Materials.initFluids();

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        //TODO new MaterialRecipeLoader().run();
        for (Material material : GenerationFlag.CRUSHED.getMats()) {
            RecipeAdder.addPulverizerRecipe(material.getChunk(1), material.getCrushed(2), 40, 1);
            RecipeAdder.addThermalCentrifugeRecipe(material.getCrushed(1), material.getCrushedC(1), material.getDust(1), material.getDustT(4), 40, 1);
        }
        RecipeAdder.addAlloySmelterRecipe(Materials.Copper.getIngot(1), Materials.Redstone.getDust(4), Materials.RedAlloy.getIngot(1), 10, 1);
        RecipeAdder.addAlloySmelterRecipe(Materials.Copper.getIngot(1), Materials.Cobalt.getDust(1), Materials.RedAlloy.getIngot(16), 10, 1);
        RecipeAdder.addBlastFurnaceRecipe(Materials.Silicon.getDust(1), Materials.Silicon.getIngot(1), 10, 1);

        RecipeAdder.addOreWasherRecipe(Materials.Aluminium.getCrushed(1), new FluidStack(FluidRegistry.WATER, 100), Materials.Aluminium.getCrushedP(1), Materials.Aluminium.getDustT(1), Materials.Stone.getDust(1), 40, 1);
    }
}
