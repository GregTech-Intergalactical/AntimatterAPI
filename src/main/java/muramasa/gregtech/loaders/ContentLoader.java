package muramasa.gregtech.loaders;

import muramasa.gregtech.api.items.MetaItem;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.common.blocks.*;
import muramasa.gregtech.common.items.ItemBlockMachines;
import muramasa.gregtech.common.items.ItemBlockOres;
import muramasa.gregtech.common.tileentities.base.TileEntityCable;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.TileEntityOre;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCasing;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCoil;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.gregtech.common.tileentities.overrides.multi.TileEntityElectricBlastFurnace;
import muramasa.gregtech.common.tileentities.overrides.multi.TileEntityFusionReactor;
import muramasa.gregtech.common.utils.Ref;
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

    public static BlockOre blockOre = new BlockOre();
    public static BlockMachine blockMachines = new BlockMachine("block_machine");

    public static BlockMultiMachine blockMultiMachine = new BlockMultiMachine("block_multi_machine");
    public static BlockHatch blockHatch = new BlockHatch("block_hatch");

    public static BlockCable blockCable = new BlockCable();

    public static BlockCasing blockCasing = new BlockCasing();
    public static BlockCoil blockCoil = new BlockCoil();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileEntityBasicMachine.class, new ResourceLocation(Ref.MODID, "tilebasic"));
        GameRegistry.registerTileEntity(TileEntitySteamMachine.class, new ResourceLocation(Ref.MODID, "tilesteam"));
        GameRegistry.registerTileEntity(TileEntityElectricBlastFurnace.class, new ResourceLocation(Ref.MODID, "tileebf"));
        GameRegistry.registerTileEntity(TileEntityFusionReactor.class, new ResourceLocation(Ref.MODID, "tilefr"));

        event.getRegistry().register(blockOre);
        GameRegistry.registerTileEntity(TileEntityOre.class, new ResourceLocation(Ref.MODID, "block_ore"));

        event.getRegistry().register(blockMachines);
        GameRegistry.registerTileEntity(TileEntityMachine.class, new ResourceLocation(Ref.MODID, "block_machine"));

        event.getRegistry().register(blockMultiMachine);
        GameRegistry.registerTileEntity(TileEntityMultiMachine.class, new ResourceLocation(Ref.MODID, "block_multi_machine"));

        event.getRegistry().register(blockHatch);
        GameRegistry.registerTileEntity(TileEntityHatch.class, new ResourceLocation(Ref.MODID, "block_hatch"));

        event.getRegistry().register(blockCable);
        GameRegistry.registerTileEntity(TileEntityCable.class, new ResourceLocation(Ref.MODID, "block_cable"));

        event.getRegistry().register(blockCasing);
        GameRegistry.registerTileEntity(TileEntityCasing.class, new ResourceLocation(Ref.MODID, "block_casing"));

        event.getRegistry().register(blockCoil);
        GameRegistry.registerTileEntity(TileEntityCoil.class, new ResourceLocation(Ref.MODID, "block_coil"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockOres(blockOre).setRegistryName(blockOre.getRegistryName()));
        event.getRegistry().register(new ItemBlockMachines(blockMachines).setRegistryName(blockMachines.getRegistryName()));
        event.getRegistry().register(new ItemBlockMachines(blockMultiMachine).setRegistryName(blockMultiMachine.getRegistryName()));
        event.getRegistry().register(new ItemBlockMachines(blockHatch).setRegistryName(blockHatch.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCable).setRegistryName(blockCable.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCasing).setRegistryName(blockCasing.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCoil).setRegistryName(blockCoil.getRegistryName()));

        event.getRegistry().register(metaItem);
        event.getRegistry().register(metaTool);
    }
}
