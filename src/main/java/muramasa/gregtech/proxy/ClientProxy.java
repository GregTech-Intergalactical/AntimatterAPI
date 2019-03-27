package muramasa.gregtech.proxy;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.tools.ToolType;
import muramasa.gregtech.api.registration.IHasModelOverride;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.pipe.types.Cable;
import muramasa.gregtech.api.pipe.types.FluidPipe;
import muramasa.gregtech.api.pipe.types.ItemPipe;
import muramasa.gregtech.api.tools.MaterialTool;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.render.GTModelLoader;
import muramasa.gregtech.client.render.models.ModelMachine;
import muramasa.gregtech.client.render.models.ModelPipe;
import muramasa.gregtech.common.blocks.BlockOre;
import muramasa.gregtech.common.blocks.BlockStorage;
import muramasa.gregtech.common.blocks.pipe.BlockPipe;
import muramasa.gregtech.api.tileentities.TileEntityMachine;
import muramasa.gregtech.api.registration.GregTechRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
        IItemColor materialItemHandler = (stack, i) -> i == 0 ? ((MaterialItem) stack.getItem()).getMaterial().getRGB() : -1;
        for (Item item : MaterialItem.getAll()) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(materialItemHandler, item);
        }

        IBlockColor machineBlockHandler = (state, world, pos, i) -> {
            TileEntity tile = Utils.getTile(world, pos);
            return tile instanceof TileEntityMachine && i == 0 ? ((TileEntityMachine) tile).getTextureData().getTint() : -1;
        };
        for (Machine type : Machines.getAll()) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(machineBlockHandler, type.getBlock());
        }

        IBlockColor pipeBlockHandler = (state, world, pos, i) -> i == 0 ? ((BlockPipe) state.getBlock()).getRGB() : -1;
        IItemColor pipeItemHandler = (stack, i) -> i == 0 ? ((BlockPipe) Block.getBlockFromItem(stack.getItem())).getRGB() : -1;
        for (Cable type : Cable.getAll()) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(pipeBlockHandler, GregTechRegistry.getCable(type));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(pipeItemHandler, Item.getItemFromBlock(GregTechRegistry.getCable(type)));
        }
        for (FluidPipe type : FluidPipe.getAll()) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(pipeBlockHandler, GregTechRegistry.getFluidPipe(type));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(pipeItemHandler, Item.getItemFromBlock(GregTechRegistry.getFluidPipe(type)));
        }
        for (ItemPipe type : ItemPipe.getAll()) {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(pipeBlockHandler, GregTechRegistry.getItemPipe(type));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(pipeItemHandler, Item.getItemFromBlock(GregTechRegistry.getItemPipe(type)));
        }

        IBlockColor oreBlockHandler = (state, world, pos, i) -> i == 1 ? ((BlockOre) state.getBlock()).getMaterial().getRGB() : -1;
        IItemColor oreItemHandler = (stack, i) -> i == 1 ? ((BlockOre) Block.getBlockFromItem(stack.getItem())).getMaterial().getRGB() : -1;
        for (Material material : ItemFlag.ORE.getMats()) {
            Block block = GregTechRegistry.getOre(material);
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(oreBlockHandler, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(oreItemHandler, Item.getItemFromBlock(block));
        }

        IBlockColor storageBlockHandler = (state, world, pos, i) -> i == 0 ? ((BlockStorage) state.getBlock()).getMaterial().getRGB() : -1;
        IItemColor storageItemHandler = (stack, i) -> i == 0 ? ((BlockStorage) Block.getBlockFromItem(stack.getItem())).getMaterial().getRGB() : -1;
        for (Material material : ItemFlag.BLOCK.getMats()) {
            Block block = GregTechRegistry.getStorage(material);
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(storageBlockHandler, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(storageItemHandler, Item.getItemFromBlock(block));
        }

        IItemColor toolItemHandler = MaterialTool::getRGB;
        for (ToolType type : ToolType.values()) {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(toolItemHandler, GregTechRegistry.getMaterialTool(type));
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        //NOOP
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) {
        //NOOP
    }

    @SubscribeEvent
    public static void onRegisterTexture(TextureStitchEvent.Pre e) {
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/liquid_still"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/liquid_flowing"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/gas_still"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/gas_flowing"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/plasma_still"));
        e.getMap().registerSprite(new ResourceLocation(Ref.MODID, "blocks/fluid/plasma_flowing"));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        for (Item item : GregTechRegistry.getRegisteredItems()) {
            if (item instanceof IHasModelOverride) ((IHasModelOverride) item).initModel();
        }
        for (Block block : GregTechRegistry.getRegisteredBlocks()) {
            if (block instanceof IHasModelOverride) ((IHasModelOverride) block).initModel();
        }

        ModelMachine modelMachine = new ModelMachine();
        GTModelLoader.register("block_machine", modelMachine);

        ModelPipe modelPipe = new ModelPipe();
        GTModelLoader.register("block_pipe", modelPipe);
    }
}
