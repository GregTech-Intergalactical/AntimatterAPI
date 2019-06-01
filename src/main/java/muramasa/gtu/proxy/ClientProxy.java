package muramasa.gtu.proxy;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.types.Cable;
import muramasa.gtu.api.pipe.types.FluidPipe;
import muramasa.gtu.api.pipe.types.ItemPipe;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.registration.IHasModelOverride;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.pipe.TileEntityCable;
import muramasa.gtu.api.tools.MaterialTool;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.GTModelLoader;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.models.ModelFluidCell;
import muramasa.gtu.client.render.models.ModelMachine;
import muramasa.gtu.client.render.models.ModelPipe;
import muramasa.gtu.common.blocks.BlockOre;
import muramasa.gtu.common.blocks.BlockStorage;
import muramasa.gtu.common.blocks.pipe.BlockPipe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
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
            Ref.MC.getItemColors().registerItemColorHandler(materialItemHandler, item);
        }

        IBlockColor machineBlockHandler = (state, world, pos, i) -> {
            TileEntity tile = Utils.getTile(world, pos);
            return tile instanceof TileEntityMachine && i == 0 ? ((TileEntityMachine) tile).getTextureData().getTint() : -1;
        };
        for (Machine type : Machines.getAll()) {
            Ref.MC.getBlockColors().registerBlockColorHandler(machineBlockHandler, type.getBlock());
        }

        IBlockColor cableBlockHandler = (state, world, pos, i) -> {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityCable) {
                if (((TileEntityCable) tile).isInsulated()) {
                    return i == 2 ? ((BlockPipe) state.getBlock()).getRGB() : -1;
                } else {
                    return i == 0 || i == 2 ? ((BlockPipe) state.getBlock()).getRGB() : -1;
                }
            }
            return -1;
        };
        IItemColor cableItemHandler = (stack, i) -> {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.KEY_CABLE_STACK_INSULATED) && stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED)) {
                return i == 2 ? ((BlockPipe) Block.getBlockFromItem(stack.getItem())).getRGB() : -1;
            } else {
                return i == 0 || i == 1 || i == 2 ? ((BlockPipe) Block.getBlockFromItem(stack.getItem())).getRGB() : -1;
            }
        };
        IBlockColor pipeBlockHandler = (state, world, pos, i) -> i == 0 || i == 1 || i == 2 ? ((BlockPipe) state.getBlock()).getRGB() : -1;
        IItemColor pipeItemHandler = (stack, i) -> i == 0 || i == 1 || i == 2 ? ((BlockPipe) Block.getBlockFromItem(stack.getItem())).getRGB() : -1;
        for (Cable type : Cable.getAll()) {
            Ref.MC.getBlockColors().registerBlockColorHandler(cableBlockHandler, GregTechRegistry.getCable(type));
            Ref.MC.getItemColors().registerItemColorHandler(cableItemHandler, Item.getItemFromBlock(GregTechRegistry.getCable(type)));
        }
        for (FluidPipe type : FluidPipe.getAll()) {
            Ref.MC.getBlockColors().registerBlockColorHandler(pipeBlockHandler, GregTechRegistry.getFluidPipe(type));
            Ref.MC.getItemColors().registerItemColorHandler(pipeItemHandler, Item.getItemFromBlock(GregTechRegistry.getFluidPipe(type)));
        }
        for (ItemPipe type : ItemPipe.getAll()) {
            Ref.MC.getBlockColors().registerBlockColorHandler(pipeBlockHandler, GregTechRegistry.getItemPipe(type));
            Ref.MC.getItemColors().registerItemColorHandler(pipeItemHandler, Item.getItemFromBlock(GregTechRegistry.getItemPipe(type)));
        }

        IBlockColor oreBlockHandler = (state, world, pos, i) -> i == 1 ? ((BlockOre) state.getBlock()).getMaterial().getRGB() : -1;
        IItemColor oreItemHandler = (stack, i) -> i == 1 ? ((BlockOre) Block.getBlockFromItem(stack.getItem())).getMaterial().getRGB() : -1;
        for (Material material : GenerationFlag.ORE.getMats()) {
            Block block = GregTechRegistry.getOre(material);
            Ref.MC.getBlockColors().registerBlockColorHandler(oreBlockHandler, block);
            Ref.MC.getItemColors().registerItemColorHandler(oreItemHandler, Item.getItemFromBlock(block));
        }

        IBlockColor storageBlockHandler = (state, world, pos, i) -> i == 0 ? ((BlockStorage) state.getBlock()).getMaterial().getRGB() : -1;
        IItemColor storageItemHandler = (stack, i) -> i == 0 ? ((BlockStorage) Block.getBlockFromItem(stack.getItem())).getMaterial().getRGB() : -1;
        for (Material material : GenerationFlag.BLOCK.getMats()) {
            Block block = GregTechRegistry.getStorage(material);
            Ref.MC.getBlockColors().registerBlockColorHandler(storageBlockHandler, block);
            Ref.MC.getItemColors().registerItemColorHandler(storageItemHandler, Item.getItemFromBlock(block));
        }

        IItemColor toolItemHandler = (stack, i) -> ((MaterialTool) stack.getItem()).getRGB(stack, i);
        for (ToolType type : ToolType.values()) {
            Ref.MC.getItemColors().registerItemColorHandler(toolItemHandler, GregTechRegistry.getMaterialTool(type));
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

    @Override
    public String trans(String unlocalized) {
        return I18n.format(unlocalized);
    }

    @SubscribeEvent
    public static void onRegisterTexture(TextureStitchEvent.Pre e) {
        //Apparently forge does not load fluid textures automatically
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

        ModelFluidCell modelFluidCell = new ModelFluidCell();
        GTModelLoader.register("fluid_cell", modelFluidCell);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        ModelUtils.onModelBake(e);
    }
}
