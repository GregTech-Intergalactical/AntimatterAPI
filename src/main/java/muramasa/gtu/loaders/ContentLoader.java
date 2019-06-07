package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockOre;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.blocks.BlockStorage;
import muramasa.gtu.api.blocks.GTItemBlock;
import muramasa.gtu.api.blocks.pipe.BlockCable;
import muramasa.gtu.api.blocks.pipe.BlockFluidPipe;
import muramasa.gtu.api.blocks.pipe.BlockItemPipe;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.items.MaterialTool;
import muramasa.gtu.api.items.StandardItem;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.pipe.types.Cable;
import muramasa.gtu.api.pipe.types.FluidPipe;
import muramasa.gtu.api.pipe.types.ItemPipe;
import muramasa.gtu.api.registration.GregTechRegistry;
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
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber
public class ContentLoader {

    public static void init() {

        //Items
        List<Prefix> prefixes = GregTechRegistry.getAll(Prefix.class);
        List<Material> materials = GregTechRegistry.getAll(Material.class);
        prefixes.forEach(p -> materials.forEach(m -> {
            if (p.allowGeneration(m)) new MaterialItem(p, m);
        }));
        for (ItemType type : ItemType.getAll()) {
            if (!type.isEnabled()) continue;
            GregTechRegistry.register(StandardItem.class, type.getNewInstance());
        }
        for (ToolType type : ToolType.values()) {
            GregTechRegistry.register(MaterialTool.class, type.getInstance());
        }

        //Blocks
        GenerationFlag.ORE.getMats().forEach(BlockOre::new);





        //Machines.getAll().forEach(type -> GregTechRegistry.register(type.getBlock()));
        Cable.getAll().forEach(type -> GregTechRegistry.register(new BlockCable(type)));
        ItemPipe.getAll().forEach(type -> GregTechRegistry.register(new BlockItemPipe(type)));
        FluidPipe.getAll().forEach(type -> GregTechRegistry.register(new BlockFluidPipe(type)));
        //Casing.getAll().forEach(type -> GregTechRegistry.register(new BlockCasing(type)));
//        Coil.getAll().forEach(type -> GregTechRegistry.register(new BlockCoil(type)));

        GenerationFlag.BLOCK.getMats().forEach(m -> GregTechRegistry.register(new BlockStorage(m)));
        StoneType.getGenerating().forEach(type -> GregTechRegistry.register(new BlockStone(type)));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e) {
        GregTechRegistry.BLOCKS.forEach(b -> e.getRegistry().register(b));

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

        GregTechRegistry.getAll(Machine.class).forEach(m -> {
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

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        GregTechRegistry.ITEMS.forEach(i -> e.getRegistry().register(i));
        GregTechRegistry.BLOCKS.forEach(b -> e.getRegistry().register(new GTItemBlock(b)));
        GregTechRegistry.callRegistrationEvent(RegistrationEvent.ITEM);
    }
}
