package muramasa.gregtech.proxy;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.client.render.ModelLoader;
import muramasa.gregtech.client.render.models.ModelCable;
import muramasa.gregtech.client.render.models.ModelMachine;
import muramasa.gregtech.client.render.models.ModelOre;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.blocks.BlockOre;
import muramasa.gregtech.common.blocks.BlockStorage;
import muramasa.gregtech.common.items.ItemBlockOres;
import muramasa.gregtech.common.items.ItemBlockStorage;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.loaders.ContentLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
        ModelLoaderRegistry.registerLoader(new ModelLoader());
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
        IItemColor oreColorHandlerItem = new ItemBlockOres.ColorHandler();
        for (BlockOre block : BlockOre.getAll()) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(oreColorHandlerBlock, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(oreColorHandlerItem, Item.getItemFromBlock(block));
        }

        IBlockColor storageColorHandlerBlock = new BlockStorage.ColorHandler();
        IItemColor storageColorHandlerItem = new ItemBlockStorage.ColorHandler();
        for (BlockStorage block : BlockStorage.getAll()) {
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
        ContentLoader.blockCasing.initModel();
        ContentLoader.blockCoil.initModel();

        for (MaterialItem item : MaterialItem.getAll()) {
            item.initModel();
        }
        for (StandardItem item : StandardItem.getAll()) {
            item.initModel();
        }
        for (Machine type : Machines.getAll()) {
            type.getBlock().initModel();
        }
        for (BlockOre block : BlockOre.getAll()) {
            block.initModel();
        }
        for (BlockStorage block : BlockStorage.getAll()) {
            block.initModel();
        }

        ModelMachine modelMachine = new ModelMachine();
        ModelLoader.register(new ResourceLocation(Ref.MODID, "block_machine"), modelMachine);

        ModelOre modelOre = new ModelOre();
        ModelLoader.register(new ResourceLocation(Ref.MODID, "block_ore"), modelOre);

        ModelCable modelCable = new ModelCable();
        ModelLoader.register(ContentLoader.blockCable, modelCable);
    }
}
