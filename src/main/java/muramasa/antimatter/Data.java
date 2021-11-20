package muramasa.antimatter;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.block.BlockProxy;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.client.ClientData;
import muramasa.antimatter.cover.*;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterMaterialFluid;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.MenuHandlerPipe;
import muramasa.antimatter.gui.container.*;
import muramasa.antimatter.item.DebugScannerItem;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.material.*;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.structure.BlockStateElement;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.MaterialSword;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.tool.behaviour.*;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.material.Element.*;
import static muramasa.antimatter.material.TextureSet.DIAMOND;
import static muramasa.antimatter.material.TextureSet.*;
import static net.minecraft.block.material.Material.WOOD;
import static net.minecraft.block.material.Material.*;
import static net.minecraft.item.ItemTier.*;

public class Data {

    public static final net.minecraft.block.material.Material WRENCH_MATERIAL = new net.minecraft.block.material.Material(MaterialColor.METAL, false, true, true, true, false, false, PushReaction.NORMAL);

    //CELLS
    public final static Set<ItemFluidCell> EMPTY_CELLS = new HashSet<>();

    //Item Types
    public static MaterialTypeItem<?> DUST = new MaterialTypeItem<>("dust", 2, true, Ref.U);
    public static MaterialTypeItem<?> DUST_SMALL = new MaterialTypeItem<>("dust_small", 2, true, Ref.U4);
    public static MaterialTypeItem<?> DUST_TINY = new MaterialTypeItem<>("dust_tiny", 2, true, Ref.U9);
    public static MaterialTypeItem<?> DUST_IMPURE = new MaterialTypeItem<>("dust_impure", 2, true, Ref.U);
    public static MaterialTypeItem<?> DUST_PURE = new MaterialTypeItem<>("dust_pure", 2, true, Ref.U);
    public static MaterialTypeItem<MaterialTypeBlock.IOreGetter> ROCK = new MaterialTypeItem<>("rock", 2, false, Ref.U9);
    public static MaterialTypeItem<?> CRUSHED = new MaterialTypeItem<>("crushed", 2, true, Ref.U);
    public static MaterialTypeItem<?> CRUSHED_CENTRIFUGED = new MaterialTypeItem<>("crushed_centrifuged", 2, true, Ref.U);
    public static MaterialTypeItem<?> CRUSHED_PURIFIED = new MaterialTypeItem<>("crushed_purified", 2, true, Ref.U);
    public static MaterialTypeItem<?> RAW_ORE = new MaterialTypeItem<>("raw_ore", 2, true, -1);
    public static MaterialTypeItem<?> INGOT = new MaterialTypeItem<>("ingot", 2, true, Ref.U);
    public static MaterialTypeItem<?> INGOT_HOT = new MaterialTypeItem<>("ingot_hot", 2, true, Ref.U);
    public static MaterialTypeItem<?> NUGGET = new MaterialTypeItem<>("nugget", 2, true, Ref.U9);
    public static MaterialTypeItem<?> GEM = new MaterialTypeItem<>("gem", 2, true, Ref.U);
    public static MaterialTypeItem<?> GEM_BRITTLE = new MaterialTypeItem<>("gem_brittle", 2, true, Ref.U);
    public static MaterialTypeItem<?> GEM_POLISHED = new MaterialTypeItem<>("gem_polished", 2, true, Ref.U);
    public static MaterialTypeItem<?> LENS = new MaterialTypeItem<>("lens", 2, true, Ref.U * 3 / 4);
    public static MaterialTypeItem<?> PLATE = new MaterialTypeItem<>("plate", 2, true, Ref.U, (a, b, c) -> {
        CoverFactory factory = CoverFactory.builder((u, v, t, w) -> new CoverPlate(u, v, t, w, b, c)).item((u, v) -> new CoverMaterialItem(u.getDomain(), b, u, c)).build(Ref.ID, "plate_" + c.getId());
        return (MaterialItem) factory.getItem().getItem();
    });
    public static MaterialTypeItem<?> PLATE_DENSE = new MaterialTypeItem<>("plate_dense", 2, true, Ref.U * 9);
    public static MaterialTypeItem<?> PLATE_TINY = new MaterialTypeItem<>("plate_tiny", 2, true, Ref.U / 8);
    public static MaterialTypeItem<?> ROD = new MaterialTypeItem<>("rod", 2, true, Ref.U2);
    public static MaterialTypeItem<?> ROD_LONG = new MaterialTypeItem<>("rod_long", 2, true, Ref.U);
    public static MaterialTypeItem<?> RING = new MaterialTypeItem<>("ring", 2, true, Ref.U4);
    public static MaterialTypeItem<?> FOIL = new MaterialTypeItem<>("foil", 2, true, Ref.U);
    public static MaterialTypeItem<?> BOLT = new MaterialTypeItem<>("bolt", 2, true, Ref.U8);
    public static MaterialTypeItem<?> SCREW = new MaterialTypeItem<>("screw", 2, true, Ref.U9);
    public static MaterialTypeItem<?> GEAR = new MaterialTypeItem<>("gear", 2, true, Ref.U * 4);
    public static MaterialTypeItem<?> GEAR_SMALL = new MaterialTypeItem<>("gear_small", 2, true, Ref.U);
    public static MaterialTypeItem<?> WIRE_FINE = new MaterialTypeItem<>("wire_fine", 2, true, Ref.U8);
    public static MaterialTypeItem<?> SPRING = new MaterialTypeItem<>("spring", 2, true, Ref.U);
    public static MaterialTypeItem<?> ROTOR = new MaterialTypeItem<>("rotor", 2, true, Ref.U * 4 + Ref.U4);
    public static MaterialTypeItem<?> DRILLBIT = new MaterialTypeItem<>("drill_bit", 2, true, Ref.U * 4);
    public static MaterialTypeItem<?> CHAINSAWBIT = new MaterialTypeItem<>("chainsaw_bit", 2, true, Ref.U * 2);
    public static MaterialTypeItem<?> WRENCHBIT = new MaterialTypeItem<>("wrench_bit", 2, true, Ref.U * 4);
    public static MaterialTypeItem<?> BUZZSAW_BLADE = new MaterialTypeItem<>("buzzsaw_blade", 2, true, Ref.U * 4);

    //Block Types
    public static MaterialTypeBlock<MaterialTypeBlock.IOreGetter> ORE = new MaterialTypeBlock<>("ore", 1, true, -1);
    public static MaterialTypeBlock<MaterialTypeBlock.IOreGetter> ORE_SMALL = new MaterialTypeBlock<>("ore_small", 1, false, -1);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> ORE_STONE = new MaterialTypeBlock<>("ore_stone", 1, true, -1);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> BLOCK = new MaterialTypeBlock<>("block", 1, false, -1);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> FRAME = new MaterialTypeBlock<>("frame", 1, true, -1);

    //Fluid Types
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> LIQUID = new MaterialTypeFluid<>("liquid", 1, true, -1);
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> GAS = new MaterialTypeFluid<>("gas", 1, true, -1);
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> PLASMA = new MaterialTypeFluid<>("plasma", 1, true, -1);

    //Dummy Types
    public static MaterialType<?> TOOLS = new MaterialType<>("tools", 1, false, -1).nonGen();
    public static MaterialType<?> ARMOR = new MaterialType<>("armor", 1, false, -1).nonGen();

    public static final Material NULL = AntimatterAPI.register(Material.class, new Material(Ref.ID, "null", 0xffffff, NONE).addTools(5.0F, 5, Integer.MAX_VALUE, 3, ImmutableMap.of(Enchantments.BLOCK_FORTUNE, 3)).addHandleStat(0, 0.0F));

    //Vanilla Stone Materials
    public static Material Stone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "stone", 0xcdcdcd, NONE).asDust(DUST_IMPURE, GEAR).addHandleStat(-10, -0.5F));
    public static Material Granite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "granite", 0xa07882, NONE).asDust(ROCK));
    public static Material Diorite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "diorite", 0xf0f0f0, NONE).asDust(ROCK));
    public static Material Andesite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "andesite", 0xbfbfbf, NONE).asDust(ROCK));

    public static Material Gravel = AntimatterAPI.register(Material.class, new Material(Ref.ID, "gravel", 0xcdcdcd, NONE).asDust(ROCK));
    public static Material Sand = AntimatterAPI.register(Material.class, new Material(Ref.ID, "sand", 0xfafac8, NONE).asDust(ROCK));
    public static Material RedSand = AntimatterAPI.register(Material.class, new Material(Ref.ID, "red_sand", 0xff8438, NONE).asDust(ROCK));
    public static Material Sandstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "sandstone", 0xfafac8, NONE).asDust(ROCK));
    public static Material Blackstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "blackstone", 0x2c272d, NONE).asDust());
    public static Material Basalt = AntimatterAPI.register(Material.class, new Material(Ref.ID, "basalt", 0x1e1414, ROUGH)).asDust(ROCK);
    public static Material Endstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "endstone", 0xffffff, NONE).asDust());
    public static Material Netherrack = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherrack", 0xc80000, NONE).asDust());

    public static Material Prismarine = AntimatterAPI.register(Material.class, new Material(Ref.ID, "prismarine", 0x6eb2a5, NONE)).asDust();
    public static Material DarkPrismarine = AntimatterAPI.register(Material.class, new Material(Ref.ID, "dark_prismarine", 0x587d6c, NONE)).asDust();

    public static StoneType STONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "stone", Stone, new Texture("minecraft", "block/stone"), SoundType.STONE, false).setState(Blocks.STONE));

    public static StoneType GRANITE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "granite", Granite, new Texture("minecraft", "block/granite"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || muramasa.antimatter.Ref.debugStones).setState(Blocks.GRANITE));
    public static StoneType DIORITE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "diorite", Diorite, new Texture("minecraft", "block/diorite"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || muramasa.antimatter.Ref.debugStones).setState(Blocks.DIORITE));
    public static StoneType ANDESITE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "andesite", Andesite, new Texture("minecraft", "block/andesite"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || muramasa.antimatter.Ref.debugStones).setState(Blocks.ANDESITE));

    public static StoneType GRAVEL = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "gravel", Gravel, new Texture("minecraft", "block/gravel"), SoundType.GRAVEL, false).setState(Blocks.GRAVEL).setGravity(true).setBlockMaterial(net.minecraft.block.material.Material.SAND).setHardnessAndResistance(0.6F).setRequiresTool(false).setToolType(ToolType.SHOVEL));
    public static StoneType SAND = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sand", Sand, new Texture("minecraft", "block/sand"), SoundType.SAND, false).setState(Blocks.SAND).setGravity(true).setBlockMaterial(net.minecraft.block.material.Material.SAND).setHardnessAndResistance(0.5F).setRequiresTool(false).setToolType(ToolType.SHOVEL));
    public static StoneType SAND_RED = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sand_red", RedSand, new Texture("minecraft", "block/red_sand"), SoundType.SAND, false).setState(Blocks.RED_SAND).setGravity(true).setBlockMaterial(net.minecraft.block.material.Material.SAND).setHardnessAndResistance(0.5F).setRequiresTool(false).setToolType(ToolType.SHOVEL));
    public static StoneType SANDSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sandstone", Sandstone, new Texture("minecraft", "block/sandstone"), SoundType.STONE, false).setState(Blocks.SANDSTONE));
    public static StoneType BASALT_VANILLA = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "vanilla_basalt", Basalt, new Texture("minecraft", "block/basalt_side"), SoundType.BASALT, false).setState(Blocks.BASALT).setHardnessAndResistance(1.25F, 4.2F));
    public static StoneType BLACKSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "blackstone", Blackstone, new Texture("minecraft", "block/blackstone"), SoundType.STONE, false).setState(Blocks.BLACKSTONE));

    public static StoneType NETHERRACK = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "netherrack", Netherrack, new Texture("minecraft", "block/netherrack"), SoundType.NETHERRACK, false).setState(Blocks.NETHERRACK).setHardnessAndResistance(0.4F));
    public static StoneType ENDSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "endstone", Endstone, new Texture("minecraft", "block/end_stone"), SoundType.STONE, false).setState(Blocks.END_STONE).setHardnessAndResistance(3.0F, 9.0F));

    static {
        StructureBuilder.addGlobalElement("A", BlockStateElement.AIR);
        StructureBuilder.addGlobalElement(" ", StructureElement.IGNORE);
        NULL.remove(ROD);

        ROCK.set((m, s) -> {
            if (m == null || s == null || !ROCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ROCK, m, s);
            BlockSurfaceRock rock = AntimatterAPI.get(BlockSurfaceRock.class, "surface_rock_" + m.getId() + "_" + Utils.getConventionalStoneType(s), m.materialDomain());
            return new MaterialTypeBlock.Container(rock != null ? rock.defaultBlockState() : Blocks.AIR.defaultBlockState());
        });
        ORE.set((m, s) -> {
            if (m != null) {
                Item item = AntimatterAPI.getReplacement(ORE, m);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || s == null || !ORE.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ORE, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, ORE.getId() + "_" + m.getId() + "_" + Utils.getConventionalStoneType(s));
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        ORE_SMALL.set((m, s) -> {
            if (m == null || s == null || !ORE_SMALL.allowGen(m))
                return MaterialTypeBlock.getEmptyBlockAndLog(ORE_SMALL, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, ORE_SMALL.getId() + "_" + m.getId() + "_" + Utils.getConventionalStoneType(s));
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        ORE_STONE.set(m -> {
            if (m == null || !ORE_STONE.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ORE_STONE, m);
            BlockOreStone block = AntimatterAPI.get(BlockOreStone.class, ORE_STONE.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        BLOCK.set(m -> {
            if (m != null) {
                Item item = AntimatterAPI.getReplacement(BLOCK, m);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || !BLOCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(BLOCK, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, BLOCK.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        FRAME.set(m -> {
            if (m == null || !FRAME.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(FRAME, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, FRAME.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();

        LIQUID.set((m, i) -> {
            if (m == null || !LIQUID.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(LIQUID, m);
            if (m.getId().equals("water")) return new FluidStack(Fluids.WATER, i);
            else if (m.getId().equals("lava")) return new FluidStack(Fluids.LAVA, i);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, LIQUID.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return new FluidStack(fluid.getFluid(), i);
        });
        GAS.set((m, i) -> {
            if (m == null || !GAS.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(GAS, m);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, GAS.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return new FluidStack(fluid.getFluid(), i);
        });
        PLASMA.set((m, i) -> {
            if (m == null || !PLASMA.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(PLASMA, m);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, PLASMA.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return new FluidStack(fluid.getFluid(), i);
        });
    }

    public static DebugScannerItem DEBUG_SCANNER = new DebugScannerItem(Ref.ID, "debug_scanner").tip(TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");

    public static final AntimatterToolType SWORD = new AntimatterToolType(Ref.ID, "sword", 2, 1, 10, 3.0F, -2.4F).setToolClass(MaterialSword.class).addEffectiveBlocks(Blocks.COBWEB).setHasContainer(false);
    public static final AntimatterToolType PICKAXE = new AntimatterToolType(Ref.ID, "pickaxe", 1, 2, 10, 1.0F, -2.8F).addEffectiveMaterials(ICE_SOLID, net.minecraft.block.material.Material.METAL, net.minecraft.block.material.Material.STONE, HEAVY_METAL, PISTON).setHasContainer(false);
    public static final AntimatterToolType SHOVEL = new AntimatterToolType(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F).addEffectiveMaterials(CLAY, net.minecraft.block.material.Material.SAND, TOP_SNOW, SNOW, DIRT).setHasContainer(false);
    public static final AntimatterToolType AXE = new AntimatterToolType(Ref.ID, "axe", 1, 1, 10, 6.0F, -3.0F).addEffectiveMaterials(WOOD, PLANT, REPLACEABLE_PLANT, BAMBOO).setHasContainer(false);
    public static final AntimatterToolType HOE = new AntimatterToolType(Ref.ID, "hoe", 1, 2, 10, -2.0F, -1.0F).setHasContainer(false);
    public static final AntimatterToolType HAMMER = new AntimatterToolType(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F).addToolTypes("pickaxe").addEffectiveMaterials(net.minecraft.block.material.Material.METAL, net.minecraft.block.material.Material.STONE).setUseSound(SoundEvents.ANVIL_PLACE).setRepairability(false);
    public static final AntimatterToolType WRENCH = new AntimatterToolType(Ref.ID, "wrench", 2, 2, 2, 1.5F, -2.8F).setUseSound(Ref.WRENCH).setOverlayLayers(0).setRepairability(false);
    public static final AntimatterToolType SAW = new AntimatterToolType(Ref.ID, "saw", 2, 2, 2, 2.0F, -2.8F).addEffectiveBlocks(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE).setRepairability(false);
    public static final AntimatterToolType FILE = new AntimatterToolType(Ref.ID, "file", 2, 2, 2, -2.0F, -2.4F).setRepairability(false);
    public static final AntimatterToolType CROWBAR = new AntimatterToolType(Ref.ID, "crowbar", 2, 10, 5, 1.0F, -2.0F).setUseSound(SoundEvents.ITEM_BREAK).setSecondaryRequirement(MaterialTag.RUBBERTOOLS).setRepairability(false);
    public static final AntimatterToolType DRILL = new AntimatterToolType(Ref.ID, "drill", 2, 2, 10, 3.0F, -3.0F).setPowered(100000, 1, 2, 3).setUseSound(Ref.DRILL).addToolTypes("pickaxe", "shovel").addEffectiveMaterials(ICE_SOLID, net.minecraft.block.material.Material.METAL, net.minecraft.block.material.Material.STONE, HEAVY_METAL, PISTON, DIRT, CLAY, net.minecraft.block.material.Material.SAND).setRepairability(false);
    public static final AntimatterToolType SOFT_HAMMER = new AntimatterToolType(Ref.ID, "soft_hammer", 2, 2, 2, 1.0F, -3.0F).setRepairability(false);//.setUseSound();
    public static final AntimatterToolType SCREWDRIVER = new AntimatterToolType(Ref.ID, "screwdriver", 2, 2, 2, 0.0F, -1.0F).setUseSound(Ref.WRENCH).setRepairability(false);
    public static final AntimatterToolType MORTAR = new AntimatterToolType(Ref.ID, "mortar", 5, 5, 2, -2.0F, 0.0F).setUseSound(SoundEvents.GRINDSTONE_USE).setBlockBreakability(false).setRepairability(false);
    public static final AntimatterToolType WIRE_CUTTER = new AntimatterToolType(Ref.ID, "wire_cutter", 5, 3, 2, 0.0F, -1.5F).setUseSound(SoundEvents.SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CLOTH_DECORATION).setRepairability(false);
    public static final AntimatterToolType BRANCH_CUTTER = new AntimatterToolType(Ref.ID, "branch_cutter", 1, 3, 2, 0.0F, -1.5F).addToolTypes("grafter").addEffectiveMaterials(LEAVES).setHasContainer(false);
    public static final AntimatterToolType KNIFE = new AntimatterToolType(Ref.ID, "knife", 2, 2, 5, 2.1F, -2.0F).setToolClass(MaterialSword.class).addEffectiveBlocks(Blocks.COBWEB).setRepairability(false);
    public static final AntimatterToolType PLUNGER = new AntimatterToolType(Ref.ID, "plunger", 5, 5, 10, 0.0F, -2.9F).setUseSound(SoundEvents.BUCKET_EMPTY).setPrimaryRequirement(MaterialTag.RUBBERTOOLS).setRepairability(false);
    public static final AntimatterToolType CHAINSAW = new AntimatterToolType(Ref.ID, "chainsaw", 2, 1, 5, 3.0F, -2.0F).setPowered(100000, 1, 2, 3).addEffectiveMaterials(WOOD, PLANT, REPLACEABLE_PLANT, BAMBOO, LEAVES).addToolTypes("axe", "saw");
    public static final AntimatterToolType ELECTRIC_WRENCH = new AntimatterToolType(Ref.ID, "electric_wrench", WRENCH).setTag(WRENCH).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).addToolTypes("wrench");
    public static final AntimatterToolType ELECTRIC_SCREWDRIVER = new AntimatterToolType(Ref.ID, "electric_screwdriver", SCREWDRIVER).setTag(SCREWDRIVER).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).setOverlayLayers(2);
    public static final AntimatterToolType JACKHAMMER = new AntimatterToolType(Ref.ID, "jackhammer", 2, 2, 10, 1.0F, -3.2F).setPowered(100000, 1, 2, 3).setUseSound(Ref.DRILL).addEffectiveMaterials(net.minecraft.block.material.Material.STONE, DIRT, net.minecraft.block.material.Material.SAND, GRASS);
    public static final AntimatterToolType BUZZSAW = new AntimatterToolType(Ref.ID, "buzzsaw", 2, 2, 2, 0.5F, -2.7F).setTag(SAW).setPowered(100000, 1, 2, 3).setOverlayLayers(2).addToolTypes("saw");
    public static final AntimatterArmorType HELMET = new AntimatterArmorType(Ref.ID, "helmet", 40, 2, 0.0F, 0.0F, EquipmentSlotType.HEAD);
    public static final AntimatterArmorType CHESTPLATE = new AntimatterArmorType(Ref.ID, "chestplate", 40, 6, 0.0F, 0.0F, EquipmentSlotType.CHEST);
    public static final AntimatterArmorType LEGGINGS = new AntimatterArmorType(Ref.ID, "leggings", 40, 5, 0.0F, 0.0F, EquipmentSlotType.LEGS);
    public static final AntimatterArmorType BOOTS = new AntimatterArmorType(Ref.ID, "boots", 40, 2, 0.0F, 0.0F, EquipmentSlotType.FEET);

    //public static Machine<?> MACHINE_INVALID = new Machine<>(Ref.ID, "invalid");

    //Vanilla Metal/Gem Materials
    public static Material Iron = AntimatterAPI.register(Material.class, new Material(Ref.ID, "iron", 0xc8c8c8, METALLIC, Fe)).asMetal(1811, 0).asOre(1, 5, true).asPlasma().addTools(IRON.getAttackDamageBonus(), IRON.getSpeed(), 256, IRON.getLevel(), of(Enchantments.SHARPNESS, 1));
    public static Material Gold = AntimatterAPI.register(Material.class, new Material(Ref.ID, "gold", 0xffe650, SHINY, Au)).asMetal(1337, 0).asOre(1, 5, true).addTools(GOLD.getAttackDamageBonus(), GOLD.getSpeed(), GOLD.getUses(), GOLD.getLevel()).harvestLevel(2);
    //cause 1.18
    public static Material Copper = AntimatterAPI.register(Material.class, new Material(Ref.ID, "copper", 0xff6400, SHINY, Cu)).asMetal(1357, 0).asOre(1, 5, true);

    public static Material Glowstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "glowstone", 0xffff00, SHINY)).asDust();
    public static Material Sugar = AntimatterAPI.register(Material.class, new Material(Ref.ID, "sugar", 0xfafafa, DULL)).asDust();
    public static Material Bone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "bone", 0xb3b3b3, DULL)).addHandleStat(12, 0.0F);
    public static Material Wood = AntimatterAPI.register(Material.class, new Material(Ref.ID, "wood", 0x643200, TextureSet.WOOD)).asDust(PLATE).addTools(ItemTier.WOOD.getAttackDamageBonus(), ItemTier.WOOD.getSpeed(), 16, ItemTier.WOOD.getLevel(), of(), SOFT_HAMMER).addHandleStat(12, 0.0F);
    public static Material Blaze = AntimatterAPI.register(Material.class, new Material(Ref.ID, "blaze", 0xffc800, NONE)).asDust().addHandleStat(-10, -0.5F, of(Enchantments.FIRE_ASPECT, 1));
    public static Material Flint = AntimatterAPI.register(Material.class, new Material(Ref.ID, "flint", 0x002040, FLINT)).asDust(GEM, MaterialTag.FLINT).addTools(1.25F, 2.5F, 128, 1, of(Enchantments.FIRE_ASPECT, 1), PICKAXE, AXE, SHOVEL, SWORD, HOE, MORTAR, KNIFE);

    public static Material Charcoal = AntimatterAPI.register(Material.class, new Material(Ref.ID, "charcoal", 0x644646, LIGNITE)).asDust(BLOCK);
    public static Material Coal = AntimatterAPI.register(Material.class, new Material(Ref.ID, "coal", 0x464646, LIGNITE)).asGemBasic(false).asOre(0, 2, true, ORE_STONE);
    public static Material Diamond = AntimatterAPI.register(Material.class, new Material(Ref.ID, "diamond", /*0x3de0e5*/0xc8ffff, DIAMOND)).asGemBasic(false).asOre(3, 7, true).addTools(ItemTier.DIAMOND.getAttackDamageBonus(), ItemTier.DIAMOND.getSpeed(), ItemTier.DIAMOND.getUses(), ItemTier.DIAMOND.getLevel());
    public static Material Emerald = AntimatterAPI.register(Material.class, new Material(Ref.ID, "emerald", 0x50ff50, GEM_V)).asGemBasic(false).asOre(3, 7, true).harvestLevel(2);
    public static Material EnderPearl = AntimatterAPI.register(Material.class, new Material(Ref.ID, "enderpearl", 0x6cdcc8, SHINY)).asGemBasic(false);
    public static Material EnderEye = AntimatterAPI.register(Material.class, new Material(Ref.ID, "endereye", 0xa0fae6, SHINY)).asGemBasic(false);
    public static Material Lapis = AntimatterAPI.register(Material.class, new Material(Ref.ID, "lapis", 0x4646dc, LAPIS)).asGemBasic(false).asOre(2, 5, true).harvestLevel(1);
    public static Material Redstone = AntimatterAPI.register(Material.class, new Material(Ref.ID, "redstone", 0xc80000, REDSTONE)).asDust().asOre(1, 5, true).harvestLevel(2);
    public static Material Quartz = AntimatterAPI.register(Material.class, new Material(Ref.ID, "quartz", 0xe6d2d2, NONE)).asDust();
    public static Material Netherite = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherite", 0x504650, DULL)).asMetal(2246, 1300).addTools(3.0F, 10, 500, NETHERITE.getLevel(), of(Enchantments.FIRE_ASPECT, 3)).addArmor(new int[]{0, 1, 1, 0}, 0.5F, 0.1F, 20);
    public static Material NetherizedDiamond = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherized_diamond", 0x5a505a, DIAMOND)).asGemBasic(false).addTools(4.0F, 12, NETHERITE.getUses(), NETHERITE.getLevel(), of(Enchantments.FIRE_ASPECT, 3, Enchantments.SHARPNESS, 4)).addArmor(new int[]{1, 1, 2, 1}, 3.0F, 0.1F, 37, of(Enchantments.ALL_DAMAGE_PROTECTION, 4));
    public static Material NetheriteScrap = AntimatterAPI.register(Material.class, new Material(Ref.ID, "netherite_scrap", 0x6e505a, ROUGH)).asDust(CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, RAW_ORE, DUST_IMPURE, DUST_PURE);

    public static Material Lava = AntimatterAPI.register(Material.class, new Material(Ref.ID, "lava", 0xff4000, NONE)).asFluid(0, 1300);
    public static Material Water = AntimatterAPI.register(Material.class, new Material(Ref.ID, "water", 0x0000ff, NONE)).asFluid();

    public static CoverFactory COVEROUTPUT = CoverFactory.builder(CoverOutput::new).addTextures(new Texture(Ref.ID, "block/cover/output")).build(Ref.ID, "output");
    public static CoverFactory COVERDEBUG = CoverFactory.builder(CoverDebug::new).addTextures(new Texture(Ref.ID, "block/cover/debug")).build(Ref.ID, "debug_cover");
    public static ItemCover COVERDEBUG_ITEM = new ItemCover(Ref.ID, "debug_cover");

    public static CoverFactory COVERINPUT = CoverFactory.builder(CoverInput::new).addTextures(new Texture(Ref.ID, "block/cover/input")).build(Ref.ID, "input");
    public static CoverFactory COVERMUFFLER = CoverFactory.builder(CoverMuffler::new).addTextures(new Texture(Ref.ID, "block/cover/muffler")).build(Ref.ID, "muffler");
    public static CoverFactory COVERDYNAMO = CoverFactory.builder(CoverDynamo::new).addTextures(new Texture(Ref.ID, "block/cover/dynamo")).build(Ref.ID, "dynamo");
    public static CoverFactory COVERENERGY = CoverFactory.builder(CoverEnergy::new).addTextures(new Texture(Ref.ID, "block/cover/energy")).build(Ref.ID, "energy");

    public static BlockProxy PROXY_INSTANCE = new BlockProxy(Ref.ID, "proxy", AbstractBlock.Properties.of(net.minecraft.block.material.Material.STONE).strength(1.0f, 1.0f).noOcclusion());


    public static MenuHandlerMachine<? extends TileEntityMachine, ? extends ContainerBasicMachine> BASIC_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_basic") {
        @Nullable
        @Override
        public ContainerMachine<?> getMenu(IGuiHandler tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMachine ? new ContainerBasicMachine((TileEntityMachine<?>) tile, playerInv, this, windowId) : null;
        }
    };

    public static MenuHandlerPipe<?> PIPE_MENU_HANDLER = new MenuHandlerPipe<>(Ref.ID, "container_pipe");

    public static MenuHandlerCover<ContainerCover> COVER_MENU_HANDLER = new MenuHandlerCover<ContainerCover>(Ref.ID, "container_cover") {
        @Override
        public ContainerCover getMenu(IGuiHandler tile, PlayerInventory playerInv, int windowId) {
            return new ContainerCover((ICover) tile, playerInv, this, windowId);
        }
    };

    public static MenuHandlerMachine<? extends TileEntityMultiMachine, ? extends ContainerMultiMachine> MULTI_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_multi") {
        @Override
        public ContainerMultiMachine getMenu(IGuiHandler tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMultiMachine ? new ContainerMultiMachine((TileEntityMultiMachine<?>) tile, playerInv, this, windowId) : null;
        }
    };

    public static MenuHandlerMachine<? extends TileEntityHatch, ? extends ContainerHatch> HATCH_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_hatch") {
        @Override
        public ContainerHatch getMenu(IGuiHandler tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityHatch ? new ContainerHatch((TileEntityHatch<?>) tile, playerInv, this, windowId) : null;
        }

        @Override
        public Object screen() {
            return ClientData.SCREEN_HATCH;
        }
    };

    public static void init(Dist side) {
        AXE.addBehaviour(BehaviourLogStripping.INSTANCE, BehaviourTreeFelling.INSTANCE);
        PICKAXE.addBehaviour(BehaviourTorchPlacing.INSTANCE);
        CHAINSAW.addBehaviour(BehaviourTreeFelling.INSTANCE, BehaviourLogStripping.INSTANCE, new BehaviourAOEBreak(1, 1, 1));
        DRILL.addBehaviour(new BehaviourAOEBreak(1, 1, 1), BehaviourTorchPlacing.INSTANCE);
        JACKHAMMER.addBehaviour(new BehaviourAOEBreak(1, 0, 2));
        PLUNGER.addBehaviour(BehaviourWaterlogToggle.INSTANCE);
        KNIFE.addBehaviour(BehaviourPumpkinCarving.INSTANCE);
        for (AntimatterToolType type : AntimatterAPI.all(AntimatterToolType.class)) {
            if (type.getToolTypes().contains("shovel")) type.addBehaviour(BehaviourVanillaShovel.INSTANCE);
            if (type.getToolTypes().contains("hoe")) type.addBehaviour(BehaviourBlockTilling.INSTANCE);
            if (type.isPowered()) type.addBehaviour(BehaviourPoweredDebug.INSTANCE);
        }

        NUGGET.forceOverride(Iron, Items.IRON_NUGGET);
        NUGGET.forceOverride(Gold, Items.GOLD_NUGGET);
        INGOT.forceOverride(Iron, Items.IRON_INGOT);
        INGOT.forceOverride(Gold, Items.GOLD_INGOT);
        INGOT.forceOverride(Netherite, Items.NETHERITE_INGOT);
        DUST.forceOverride(Redstone, Items.REDSTONE);
        DUST.forceOverride(Glowstone, Items.GLOWSTONE_DUST);
        DUST.forceOverride(Blaze, Items.BLAZE_POWDER);
        DUST.forceOverride(Sugar, Items.SUGAR);
        GEM.forceOverride(Flint, Items.FLINT);
        GEM.forceOverride(Diamond, Items.DIAMOND);
        GEM.forceOverride(Emerald, Items.EMERALD);
        GEM.forceOverride(Lapis, Items.LAPIS_LAZULI);
        GEM.forceOverride(Coal, Items.COAL);
        GEM.forceOverride(Charcoal, Items.CHARCOAL);
        GEM.forceOverride(EnderEye, Items.ENDER_EYE);
        GEM.forceOverride(EnderPearl, Items.ENDER_PEARL);

        ROD.forceOverride(Blaze, Items.BLAZE_ROD);
        ROD.forceOverride(Bone, Items.BONE);
        ROD.forceOverride(Wood, Items.STICK);

        BLOCK.forceOverride(Iron, Items.IRON_BLOCK);
        BLOCK.forceOverride(Gold, Items.GOLD_BLOCK);
        BLOCK.forceOverride(Diamond, Items.DIAMOND_BLOCK);
        BLOCK.forceOverride(Emerald, Items.EMERALD_BLOCK);
        BLOCK.forceOverride(Lapis, Items.LAPIS_BLOCK);
        BLOCK.forceOverride(Netherite, Items.NETHERITE_BLOCK);

        ROTOR.dependents(PLATE, SCREW, RING);
        SCREW.dependents(BOLT);
        BOLT.dependents(ROD);

        if (side == Dist.CLIENT) {
            clientInit();
        }
    }

    public static void postInit(Dist side) {
        LIQUID.all().stream().filter(l -> !l.getId().equals("water") || !l.getId().equals("lava")).forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, LIQUID)));
        GAS.all().forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, GAS)));
        PLASMA.all().forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, PLASMA)));
        AntimatterAPI.all(Material.class, Material::setChemicalFormula);
        if (AntimatterConfig.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) ORE.all().forEach(m -> m.flags(ORE_SMALL));
    }

    private static void clientInit() {
        WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || (b instanceof BlockPipe && b.getHarvestTool(b.defaultBlockState()) == WRENCH.getToolType()), BehaviourExtendedHighlight.PIPE_FUNCTION));
        SCREWDRIVER.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION));
        ELECTRIC_WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || (b instanceof BlockPipe && b.getHarvestTool(b.defaultBlockState()) == WRENCH.getToolType()), BehaviourExtendedHighlight.PIPE_FUNCTION));
        WIRE_CUTTER.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockPipe && b.getHarvestTool(b.defaultBlockState()) == WIRE_CUTTER.getToolType(), BehaviourExtendedHighlight.PIPE_FUNCTION));
        CROWBAR.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION));

        MaterialType.buildTooltips();
    }
}
