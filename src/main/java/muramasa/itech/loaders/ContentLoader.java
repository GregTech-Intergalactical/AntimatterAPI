package muramasa.itech.loaders;

import muramasa.itech.ITech;
import muramasa.itech.api.items.MetaItem;
import muramasa.itech.api.items.MetaTool;
import muramasa.itech.common.blocks.*;
import muramasa.itech.common.items.ItemBlockMachines;
import muramasa.itech.common.items.ItemBlockOres;
import muramasa.itech.common.tileentities.base.TileEntityCable;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.tileentities.base.TileEntityOre;
import muramasa.itech.common.tileentities.base.multi.TileEntityCasing;
import muramasa.itech.common.tileentities.base.multi.TileEntityCoil;
import muramasa.itech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.itech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.itech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.itech.common.tileentities.overrides.multi.TileEntityElectricBlastFurnace;
import muramasa.itech.common.tileentities.overrides.multi.TileEntityFusionReactor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ContentLoader {

    public static MetaItem metaItem = new MetaItem();
    public static MetaTool metaTool = new MetaTool();

    public static BlockOres blockOres = new BlockOres();
    public static BlockMachines blockMachines = new BlockMachines("blockmachines");

    public static BlockMultiMachines blockMultiMachines = new BlockMultiMachines("blockmultimachines");
    public static BlockHatches blockHatches = new BlockHatches("blockhatches");

    public static BlockCables blockCables = new BlockCables();

    public static BlockCasings blockCasings = new BlockCasings();
    public static BlockCoils blockCoils = new BlockCoils();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileEntityBasicMachine.class, new ResourceLocation(ITech.MODID, "tilebasic"));
        GameRegistry.registerTileEntity(TileEntitySteamMachine.class, new ResourceLocation(ITech.MODID, "tilesteam"));
        GameRegistry.registerTileEntity(TileEntityElectricBlastFurnace.class, new ResourceLocation(ITech.MODID, "tileebf"));
        GameRegistry.registerTileEntity(TileEntityFusionReactor.class, new ResourceLocation(ITech.MODID, "tilefr"));

        event.getRegistry().register(blockOres);
        GameRegistry.registerTileEntity(TileEntityOre.class, new ResourceLocation(ITech.MODID, "blockores"));

        event.getRegistry().register(blockMachines);
        GameRegistry.registerTileEntity(TileEntityMachine.class, new ResourceLocation(ITech.MODID, "blockmachines"));

        event.getRegistry().register(blockMultiMachines);
        GameRegistry.registerTileEntity(TileEntityMultiMachine.class, new ResourceLocation(ITech.MODID, "blockmultimachines"));

        event.getRegistry().register(blockHatches);
        GameRegistry.registerTileEntity(TileEntityHatch.class, new ResourceLocation(ITech.MODID, "blockhatches"));

        event.getRegistry().register(blockCables);
        GameRegistry.registerTileEntity(TileEntityCable.class, new ResourceLocation(ITech.MODID, "blockcables"));

        event.getRegistry().register(blockCasings);
        GameRegistry.registerTileEntity(TileEntityCasing.class, new ResourceLocation(ITech.MODID, "blockcasings"));

        event.getRegistry().register(blockCoils);
        GameRegistry.registerTileEntity(TileEntityCoil.class, new ResourceLocation(ITech.MODID, "blockcoils"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockOres(blockOres).setRegistryName(blockOres.getRegistryName()));
        event.getRegistry().register(new ItemBlockMachines(blockMachines).setRegistryName(blockMachines.getRegistryName()));
        event.getRegistry().register(new ItemBlockMachines(blockMultiMachines).setRegistryName(blockMultiMachines.getRegistryName()));
        event.getRegistry().register(new ItemBlockMachines(blockHatches).setRegistryName(blockHatches.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCables).setRegistryName(blockCables.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCasings).setRegistryName(blockCasings.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCoils).setRegistryName(blockCoils.getRegistryName()));

        event.getRegistry().register(metaItem);
        event.getRegistry().register(metaTool);
    }
}
