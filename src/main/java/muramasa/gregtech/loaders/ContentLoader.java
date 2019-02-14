package muramasa.gregtech.loaders;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.api.items.StandardItem;
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

@Mod.EventBusSubscriber
public class ContentLoader {

    public static MetaTool metaTool = new MetaTool();

    public static BlockCable blockCable = new BlockCable();

    public static BlockCasing blockCasing = new BlockCasing();
    public static BlockCoil blockCoil = new BlockCoil();

    static {
//        Ref.TAB_MATERIALS.setTabStack(Materials.Titanium.getIngot(1));
//        Ref.TAB_ITEMS.setTabStack(ItemList.Debug_Scanner.get(1));
//        Ref.TAB_BLOCKS.setTabStack(new ItemStack(blockCasing, 1, CasingType.FUSION3.ordinal()));
//        Ref.TAB_MACHINES.setTabStack(new MachineStack(Machines.ALLOY_SMELTER, Tier.EV).asItemStack());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileEntityBasicMachine.class, new ResourceLocation(Ref.MODID, "tilebasic"));
        GameRegistry.registerTileEntity(TileEntitySteamMachine.class, new ResourceLocation(Ref.MODID, "tilesteam"));
        GameRegistry.registerTileEntity(TileEntityElectricBlastFurnace.class, new ResourceLocation(Ref.MODID, "tileebf"));
        GameRegistry.registerTileEntity(TileEntityFusionReactor.class, new ResourceLocation(Ref.MODID, "tilefr"));


        GameRegistry.registerTileEntity(TileEntityMachine.class, new ResourceLocation(Ref.MODID, "block_machine"));
        GameRegistry.registerTileEntity(TileEntityMultiMachine.class, new ResourceLocation(Ref.MODID, "block_multi_machine"));
        GameRegistry.registerTileEntity(TileEntityHatch.class, new ResourceLocation(Ref.MODID, "block_hatch"));
        for (Machine type : Machines.getAll()) {
            event.getRegistry().register(type.getBlock());
        }
        for (Material material : Materials.getAll()) {
            if (material.hasFlag(GenerationFlag.ORE)) {
                event.getRegistry().register(new BlockOre(material));
            }
            if (material.hasFlag(GenerationFlag.BLOCK)) {
                event.getRegistry().register(new BlockStorage(material));
            }
        }

        event.getRegistry().register(blockCable);
        GameRegistry.registerTileEntity(TileEntityCable.class, new ResourceLocation(Ref.MODID, "block_cable"));

        event.getRegistry().register(blockCasing);
        GameRegistry.registerTileEntity(TileEntityCasing.class, new ResourceLocation(Ref.MODID, "block_casing"));

        event.getRegistry().register(blockCoil);
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

        event.getRegistry().register(new ItemBlock(blockCable).setRegistryName(blockCable.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCasing).setRegistryName(blockCasing.getRegistryName()));
        event.getRegistry().register(new ItemBlock(blockCoil).setRegistryName(blockCoil.getRegistryName()));

        event.getRegistry().register(metaTool);
    }
}
