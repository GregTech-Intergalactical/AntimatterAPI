package muramasa.gregtech.loaders;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.common.blocks.*;
import muramasa.gregtech.common.items.ItemBlockMachines;
import muramasa.gregtech.common.items.ItemBlockOres;
import muramasa.gregtech.common.items.ItemBlockStorage;
import muramasa.gregtech.common.tileentities.base.TileEntityCable;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCasing;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityCoil;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber
public class ContentLoader {

    public static MetaTool metaTool = new MetaTool();

    public static BlockCable blockCable = new BlockCable();

    static {
//        Ref.TAB_MATERIALS.setTabStack(Materials.Titanium.getIngot(1));
//        Ref.TAB_ITEMS.setTabStack(ItemList.Debug_Scanner.get(1));
//        Ref.TAB_BLOCKS.setTabStack(new ItemStack(blockCasing, 1, CasingType.FUSION3.ordinal()));
//        Ref.TAB_MACHINES.setTabStack(new MachineStack(Machines.ALLOY_SMELTER, Tier.EV).asItemStack());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileEntityMachine.class, new ResourceLocation(Ref.MODID, "tile_machine"));
        GameRegistry.registerTileEntity(TileEntityBasicMachine.class, new ResourceLocation(Ref.MODID, "tile_basic_machine"));
        GameRegistry.registerTileEntity(TileEntitySteamMachine.class, new ResourceLocation(Ref.MODID, "tile_steam_machine"));
        GameRegistry.registerTileEntity(TileEntityMultiMachine.class, new ResourceLocation(Ref.MODID, "tile_multi_machine"));
        GameRegistry.registerTileEntity(TileEntityHatch.class, new ResourceLocation(Ref.MODID, "tile_hatch"));
        List<String> registeredTiles = new LinkedList<>();
        for (Machine type : Machines.getAll()) {
            event.getRegistry().register(type.getBlock());
            if (type.hasFlag(MachineFlag.MULTI) && !registeredTiles.contains(type.getTileClass().getName())) {
                GameRegistry.registerTileEntity(type.getTileClass(), new ResourceLocation(Ref.MODID, "tile_" + type.getName()));
                registeredTiles.add(type.getTileClass().getName());
            }
        }
        for (Material material : Materials.getAll()) {
            if (material.hasFlag(GenerationFlag.ORE)) {
                for (StoneType type : StoneType.getAll()) {
                    event.getRegistry().register(new BlockOre(type, material));
                }
            }
            if (material.hasFlag(GenerationFlag.BLOCK)) {
                event.getRegistry().register(new BlockStorage(material));
            }
        }
        for (Casing type : Casing.getAll()) {
            event.getRegistry().register(new BlockCasing(type));
        }
        for (Coil type : Coil.getAll()) {
            event.getRegistry().register(new BlockCoil(type));
        }
        for (StoneType type : StoneType.getGenerating()) {
            event.getRegistry().register(new BlockStone(type));
        }

        event.getRegistry().register(blockCable);
        GameRegistry.registerTileEntity(TileEntityCable.class, new ResourceLocation(Ref.MODID, "block_cable"));

        GameRegistry.registerTileEntity(TileEntityCasing.class, new ResourceLocation(Ref.MODID, "block_casing"));

        GameRegistry.registerTileEntity(TileEntityCoil.class, new ResourceLocation(Ref.MODID, "block_coil"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (MaterialItem item : MaterialItem.getAll()) {
            event.getRegistry().register(item);
        }
        for (StandardItem item : StandardItem.getAll()) {
            event.getRegistry().register(item);
        }
        for (Machine type : Machines.getAll()) {
            event.getRegistry().register(new ItemBlockMachines(type.getBlock()).setRegistryName(type.getBlock().getRegistryName()));
        }
        for (BlockOre block : BlockOre.getAll()) {
            event.getRegistry().register(new ItemBlockOres(block).setRegistryName(block.getRegistryName()));
        }
        for (BlockStorage block : BlockStorage.getAll()) {
            event.getRegistry().register(new ItemBlockStorage(block).setRegistryName(block.getRegistryName()));
        }
        for (BlockCasing block : BlockCasing.getAll()) {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
        for (BlockCoil block : BlockCoil.getAll()) {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
        for (BlockStone block : BlockStone.getAll()) {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }

        event.getRegistry().register(new ItemBlock(blockCable).setRegistryName(blockCable.getRegistryName()));

        event.getRegistry().register(metaTool);
    }
}
