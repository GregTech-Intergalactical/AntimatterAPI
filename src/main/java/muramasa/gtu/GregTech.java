package muramasa.gtu;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.GTItemBlock;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.data.Guis;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.data.Structures;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.network.GregTechNetwork;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.api.tileentities.*;
import muramasa.gtu.api.tileentities.multi.TileEntityCasing;
import muramasa.gtu.api.tileentities.multi.TileEntityCoil;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.tileentities.pipe.TileEntityCable;
import muramasa.gtu.api.tileentities.pipe.TileEntityFluidPipe;
import muramasa.gtu.api.tileentities.pipe.TileEntityItemPipe;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
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

import java.util.LinkedList;
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
        if (Utils.isModLoaded(Ref.MOD_CT)) GregTechTweaker.init();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        LOGGER = e.getModLog();
        PROXY.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(GregTech.INSTANCE, new GuiHandler());
        GTCapabilities.register();

        if (Ref.DISABLE_VANILLA_ORE_GENERATION) MinecraftForge.EVENT_BUS.register(new OreGenHandler());
        MinecraftForge.EVENT_BUS.register(this);

        GregTechAPI.registerJEICategory(RecipeMap.ORE_BY_PRODUCTS, Guis.MULTI_DISPLAY_COMPACT);
//        GregTechAPI.registerJEICategory(RecipeMap.SMELTING, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.STEAM_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.GAS_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.COMBUSTION_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.NAQUADAH_FUELS, Guis.MULTI_DISPLAY_COMPACT);
        GregTechAPI.registerJEICategory(RecipeMap.PLASMA_FUELS, Guis.MULTI_DISPLAY_COMPACT);

        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL);
        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL_INIT);

        Machines.init();
        Guis.init();
        Structures.init();
        Data.init();
        System.out.println("GREGTECH PREINIT FINISHED");
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
        GregTechAPI.onRegistration(RegistrationEvent.CRAFTING_RECIPE);
        GregTechAPI.onRegistration(RegistrationEvent.MATERIAL_RECIPE);
        GregTechAPI.onRegistration(RegistrationEvent.MACHINE_RECIPE);
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

//        GregTechRegistry.TILES.forEach(t -> {
//            GameRegistry.registerTileEntity(t, new ResourceLocation(Ref.MODID));
//        });

        //TODO auto register all type tiles??? probably
        GameRegistry.registerTileEntity(TileEntityMachine.class, new ResourceLocation(Ref.MODID, "tile_machine"));
        GameRegistry.registerTileEntity(TileEntityBasicMachine.class, new ResourceLocation(Ref.MODID, "tile_basic_machine"));
        GameRegistry.registerTileEntity(TileEntityItemMachine.class, new ResourceLocation(Ref.MODID, "tile_item_machine"));
        GameRegistry.registerTileEntity(TileEntityFluidMachine.class, new ResourceLocation(Ref.MODID, "tile_fluid_machine"));
        GameRegistry.registerTileEntity(TileEntityItemFluidMachine.class, new ResourceLocation(Ref.MODID, "tile_item_fluid_machine"));
        GameRegistry.registerTileEntity(TileEntitySteamMachine.class, new ResourceLocation(Ref.MODID, "tile_steam_machine"));
        GameRegistry.registerTileEntity(TileEntityMultiMachine.class, new ResourceLocation(Ref.MODID, "tile_multi_machine"));
        GameRegistry.registerTileEntity(TileEntityHatch.class, new ResourceLocation(Ref.MODID, "tile_hatch"));
        List<String> registeredTiles = new LinkedList<>();

        GregTechAPI.all(Machine.class).forEach(m -> {
            if (m.hasFlag(MachineFlag.MULTI) && !registeredTiles.contains(m.getTileClass().getName())) {
                GameRegistry.registerTileEntity(m.getTileClass(), new ResourceLocation(Ref.MODID, "tile_" + m.getId()));
                registeredTiles.add(m.getTileClass().getName());
            }
        });

        GameRegistry.registerTileEntity(TileEntityPipe.class, new ResourceLocation(Ref.MODID, "block_pipe"));
        GameRegistry.registerTileEntity(TileEntityItemPipe.class, new ResourceLocation(Ref.MODID, "block_item_pipe"));
        GameRegistry.registerTileEntity(TileEntityFluidPipe.class, new ResourceLocation(Ref.MODID, "block_fluid_pipe"));
        GameRegistry.registerTileEntity(TileEntityCable.class, new ResourceLocation(Ref.MODID, "block_cable"));
        GameRegistry.registerTileEntity(TileEntityCasing.class, new ResourceLocation(Ref.MODID, "block_casing"));
        GameRegistry.registerTileEntity(TileEntityCoil.class, new ResourceLocation(Ref.MODID, "block_coil"));
    }
}
