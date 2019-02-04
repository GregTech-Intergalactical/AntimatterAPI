package muramasa.gregtech.proxy;

import muramasa.gregtech.api.items.MetaItem;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.client.render.ModelLoader;
import muramasa.gregtech.client.render.models.ModelCable;
import muramasa.gregtech.client.render.models.ModelMachine;
import muramasa.gregtech.client.render.models.ModelOre;
import muramasa.gregtech.common.blocks.BlockMachine;
import muramasa.gregtech.common.blocks.BlockOre;
import muramasa.gregtech.common.items.ItemBlockOres;
import muramasa.gregtech.loaders.ContentLoader;
import net.minecraft.client.Minecraft;
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
        ModelLoaderRegistry.registerLoader(new ModelLoader());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new MetaItem.ColorHandler(), ContentLoader.metaItem);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new MetaTool.ColorHandler(), ContentLoader.metaTool);

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockOre.ColorHandler(), ContentLoader.blockOre);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemBlockOres.ColorHandler(), Item.getItemFromBlock(ContentLoader.blockOre));

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockMachine.ColorHandler(), ContentLoader.blockMachines);
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
        ContentLoader.metaItem.initModel();
        ContentLoader.metaTool.initModel();
        ContentLoader.blockMachines.initModel();
        ContentLoader.blockMultiMachine.initModel();
        ContentLoader.blockHatch.initModel();
        ContentLoader.blockOre.initModel();
        ContentLoader.blockCable.initModel();
        ContentLoader.blockCasing.initModel();
        ContentLoader.blockCoil.initModel();

        //TODO avoid multiple instances of ModelMachine, static lists?
        ModelMachine modelMachine = new ModelMachine();
        ModelLoader.register(ContentLoader.blockMachines, modelMachine);
        ModelLoader.register(ContentLoader.blockMultiMachine, modelMachine);
        ModelLoader.register(ContentLoader.blockHatch, modelMachine);
        ModelLoader.register(ContentLoader.blockCable, new ModelCable());
        ModelLoader.register(ContentLoader.blockOre, new ModelOre());
    }
}
