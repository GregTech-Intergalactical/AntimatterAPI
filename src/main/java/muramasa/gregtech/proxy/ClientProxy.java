package muramasa.gregtech.proxy;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.client.render.GTModelLoader;
import muramasa.gregtech.client.render.models.ModelCable;
import muramasa.gregtech.client.render.models.ModelMachine;
import muramasa.gregtech.common.blocks.*;
import muramasa.gregtech.common.items.ItemBlockOre;
import muramasa.gregtech.common.items.ItemBlockStorage;
import muramasa.gregtech.loaders.ContentLoader;
import muramasa.gregtech.loaders.GregTechRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        ModelLoaderRegistry.registerLoader(new GTModelLoader());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        IItemColor materialColorHandler = new MaterialItem.ColorHandler();
        for (Item item : MaterialItem.getAll()) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(materialColorHandler, item);
        }
        IBlockColor machineColorHandler = new BlockMachine.ColorHandler();
        for (Machine type : Machines.getAll()) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(machineColorHandler, type.getBlock());
        }
        IBlockColor oreColorHandlerBlock = new BlockOre.ColorHandler();
        IItemColor oreColorHandlerItem = new ItemBlockOre.ColorHandler();
        for (StoneType type : StoneType.getAll()) {
            for (Material material : ItemFlag.ORE.getMats()) {
                Block block = GregTechRegistry.getOre(type, material);
                Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(oreColorHandlerBlock, block);
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(oreColorHandlerItem, Item.getItemFromBlock(block));
            }
        }
        IBlockColor storageColorHandlerBlock = new BlockStorage.ColorHandler();
        IItemColor storageColorHandlerItem = new ItemBlockStorage.ColorHandler();
        for (Material material : ItemFlag.BLOCK.getMats()) {
            Block block = GregTechRegistry.getStorage(material);
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(storageColorHandlerBlock, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(storageColorHandlerItem, Item.getItemFromBlock(block));
        }
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new MetaTool.ColorHandler(), ContentLoader.metaTool);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
//        ContentLoader.blockMachines.initItemModel();
//        ContentLoader.blockMultiMachine.initItemModel();
//        ContentLoader.blockHatch.initItemModel();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) {
        //NOOP
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ContentLoader.metaTool.initModel();

        ContentLoader.blockCable.initModel();

        for (MaterialItem item : MaterialItem.getAll()) {
            item.initModel();
        }
        for (StandardItem item : StandardItem.getAll()) {
            item.initModel();
        }
        for (Machine type : Machines.getAll()) {
            type.getBlock().initModel();
        }
        for (StoneType type : StoneType.getAll()) {
            for (Material material : ItemFlag.ORE.getMats()) {
                BlockOre block = GregTechRegistry.getOre(type, material);
                block.initModel();
            }
        }
        for (Material material : ItemFlag.BLOCK.getMats()) {
            BlockStorage block = GregTechRegistry.getStorage(material);
            block.initModel();
        }
        for (Casing type : Casing.getAll()) {
            BlockCasing block = GregTechRegistry.getCasing(type);
            block.initModel();
        }
        for (Coil type : Coil.getAll()) {
            BlockCoil block = GregTechRegistry.getCoil(type);
            block.initModel();
        }
        for (StoneType type : StoneType.getGenerating()) {
            BlockStone block = GregTechRegistry.getStone(type);
            block.initModel();
        }

        ModelMachine modelMachine = new ModelMachine();
        GTModelLoader.register("block_machine", modelMachine);

        ModelCable modelCable = new ModelCable();
        GTModelLoader.register(ContentLoader.blockCable.getRegistryName().getResourcePath(), modelCable);
    }
}
