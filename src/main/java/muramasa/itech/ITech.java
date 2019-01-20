package muramasa.itech;

import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.ItemFlag;
import muramasa.itech.api.enums.RecipeFlag;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.materials.Materials;
import muramasa.itech.api.recipe.RecipeAdder;
import muramasa.itech.client.creativetab.ITechTab;
import muramasa.itech.common.events.EventHandler;
import muramasa.itech.common.fluid.FluidBiomass;
import muramasa.itech.proxy.GuiHandler;
import muramasa.itech.proxy.IProxy;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = ITech.MODID, name = ITech.NAME, version = ITech.VERSION, useMetadata = true)
public class ITech {

    public static final String MODID = "itech"; //TODO move to Ref
    public static final String NAME = "Intergalactical Tech";
    public static final String VERSION = "0.1";

    public static final ITechTab TAB_MATERIALS = new ITechTab("materials", new ItemStack(Items.IRON_INGOT)); //TODO move to Ref
    public static final ITechTab TAB_MACHINES = new ITechTab("machines", new ItemStack(Blocks.FURNACE));
    public static final ITechTab TAB_ORES = new ITechTab("ores", new ItemStack(Blocks.DIAMOND_ORE));

    @SidedProxy(clientSide = "muramasa.itech.proxy.ClientProxy", serverSide = "muramasa.itech.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static ITech INSTANCE;

    public static Logger logger;

    static {
        Materials.init();
        ItemFlag.finish();
        RecipeFlag.finish();
        MachineList.init();
    }

    public Fluid biomass;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);

        new EventHandler().init();
        ITechCapabilities.register();

        NetworkRegistry.INSTANCE.registerGuiHandler(ITech.INSTANCE, new GuiHandler());

        biomass = new FluidBiomass();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
//        new MaterialRecipeLoader().run();
        for (Materials material : ItemFlag.CRUSHED.getMats()) {
            RecipeAdder.addPulverizerRecipe(material.getChunk(1), material.getCrushed(2), 40, 1);
            RecipeAdder.addThermalCentrifugeRecipe(material.getCrushed(1), material.getCrushedC(1), material.getDust(1), material.getDustT(4), 40, 1);
        }
        RecipeAdder.addAlloySmelterRecipe(Materials.Copper.getIngot(1), Materials.Redstone.getDust(4), Materials.RedAlloy.getIngot(1), 10, 1);
    }
}
