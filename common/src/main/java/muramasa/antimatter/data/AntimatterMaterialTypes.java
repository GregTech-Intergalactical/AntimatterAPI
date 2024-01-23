package muramasa.antimatter.data;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.CoverPlate;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterMaterialFluid;
import muramasa.antimatter.item.CoverMaterialItem;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeFluid;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.util.Utils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import tesseract.FluidPlatformUtils;

import static muramasa.antimatter.Ref.*;

public class AntimatterMaterialTypes {
    //Item Types
    public static MaterialTypeItem<?> DUST = new MaterialTypeItem<>("dust", 2, true, U);
    public static MaterialTypeItem<?> DUST_SMALL = new MaterialTypeItem<>("dust_small", 2, true, Ref.U4);
    public static MaterialTypeItem<?> DUST_TINY = new MaterialTypeItem<>("dust_tiny", 2, true, Ref.U9);
    public static MaterialTypeItem<?> DUST_IMPURE = new MaterialTypeItem<>("dust_impure", 2, true, -1);
    public static MaterialTypeItem<?> DUST_PURE = new MaterialTypeItem<>("dust_pure", 2, true, -1);
    public static MaterialTypeItem<MaterialTypeBlock.IOreGetter> ROCK = new MaterialTypeItem<>("rock", 2, false, Ref.U4, (domain, type, mat) -> {
        AntimatterAPI.all(StoneType.class).stream().filter(StoneType::doesGenerateOre).filter(s -> s != AntimatterStoneTypes.BEDROCK).forEach(s -> AntimatterAPI.register(BlockSurfaceRock.class, new BlockSurfaceRock(domain, mat, s)));
        new MaterialItem(domain, type, mat);
    });
    public static MaterialTypeItem<?> CRUSHED = new MaterialTypeItem<>("crushed", 2, true, -1);
    public static MaterialTypeItem<?> CRUSHED_REFINED = new MaterialTypeItem<>("crushed_refined", 2, true, -1);
    public static MaterialTypeItem<?> CRUSHED_PURIFIED = new MaterialTypeItem<>("crushed_purified", 2, true, -1);
    public static MaterialTypeItem<?> RAW_ORE = new MaterialTypeItem<>("raw_ore", 2, true, -1);
    public static MaterialTypeItem<?> INGOT = new MaterialTypeItem<>("ingot", 2, true, U);
    public static MaterialTypeItem<?> INGOT_HOT = new MaterialTypeItem<>("ingot_hot", 2, true, U);
    public static MaterialTypeItem<?> NUGGET = new MaterialTypeItem<>("nugget", 2, true, Ref.U9);
    public static MaterialTypeItem<?> GEM = new MaterialTypeItem<>("gem", 2, true, U);
    public static MaterialTypeItem<?> GEM_EXQUISITE = new MaterialTypeItem<>("gem_exquisite", 2, true, U * 4);
    public static MaterialTypeItem<?> GEM_FLAWLESS = new MaterialTypeItem<>("gem_flawless", 2, true, U * 2);
    public static MaterialTypeItem<?> GEM_FLAWED = new MaterialTypeItem<>("gem_flawed", 2, true, U2);
    public static MaterialTypeItem<?> GEM_CHIPPED = new MaterialTypeItem<>("gem_chipped", 2, true, U4);
    public static MaterialTypeItem<?> LENS = new MaterialTypeItem<>("lens", 2, true, U * 3 / 4);
    public static MaterialTypeItem<?> PLATE = new MaterialTypeItem<>("plate", 2, true, U, (a, b, c) -> CoverFactory.builder((u, v, t, w) -> new CoverPlate(u, v, t, w, b, c)).item((u, v) -> new CoverMaterialItem(u.getDomain(), b, u, c)).build(Ref.ID, "plate_" + c.getId()));
    public static MaterialTypeItem<?> PLATE_DENSE = new MaterialTypeItem<>("plate_dense", 2, true, U * 9);
    public static MaterialTypeItem<?> PLATE_TINY = new MaterialTypeItem<>("plate_tiny", 2, true, U9);
    public static MaterialTypeItem<?> ITEM_CASING = new MaterialTypeItem<>("casing_item", 1, true, U2);
    public static MaterialTypeItem<?> ROD = new MaterialTypeItem<>("rod", 2, true, U2);
    public static MaterialTypeItem<?> ROD_LONG = new MaterialTypeItem<>("rod_long", 2, true, U);
    public static MaterialTypeItem<?> RING = new MaterialTypeItem<>("ring", 2, true, Ref.U4);
    public static MaterialTypeItem<?> FOIL = new MaterialTypeItem<>("foil", 2, true, U4);
    public static MaterialTypeItem<?> BOLT = new MaterialTypeItem<>("bolt", 2, true, U8);
    public static MaterialTypeItem<?> SCREW = new MaterialTypeItem<>("screw", 2, true, Ref.U8);
    public static MaterialTypeItem<?> GEAR = new MaterialTypeItem<>("gear", 2, true, U * 4);
    public static MaterialTypeItem<?> GEAR_SMALL = new MaterialTypeItem<>("gear_small", 2, true, U);
    public static MaterialTypeItem<?> WIRE_FINE = new MaterialTypeItem<>("wire_fine", 2, true, U8);
    public static MaterialTypeItem<?> SPRING = new MaterialTypeItem<>("spring", 2, true, U);
    public static MaterialTypeItem<?> ROTOR = new MaterialTypeItem<>("rotor", 2, true, (U * 4) + Ref.U4);
    public static MaterialTypeItem<?> DRILLBIT = new MaterialTypeItem<>("drill_bit", 2, true, U * 4);
    public static MaterialTypeItem<?> CHAINSAWBIT = new MaterialTypeItem<>("chainsaw_bit", 2, true, U * 2);
    public static MaterialTypeItem<?> WRENCHBIT = new MaterialTypeItem<>("wrench_bit", 2, true, U * 4);
    public static MaterialTypeItem<?> BUZZSAW_BLADE = new MaterialTypeItem<>("buzzsaw_blade", 2, true, U * 4);
    public static final MaterialTypeItem<?> PICKAXE_HEAD = new MaterialTypeItem<>("pickaxe_head", 2, true, U * 3);
    public static final MaterialTypeItem<?> AXE_HEAD = new MaterialTypeItem<>("axe_head", 2, true, U * 3);
    public static final MaterialTypeItem<?> SWORD_BLADE = new MaterialTypeItem<>("sword_blade", 2, true, U * 2);
    public static final MaterialTypeItem<?> SHOVEL_HEAD = new MaterialTypeItem<>("shovel_head", 2, true, U);
    public static final MaterialTypeItem<?> HOE_HEAD = new MaterialTypeItem<>("hoe_head", 2, true, U * 2);
    public static final MaterialTypeItem<?> HAMMER_HEAD = new MaterialTypeItem<>("hammer_head", 2, true, U * 6);
    public static final MaterialTypeItem<?> FILE_HEAD = new MaterialTypeItem<>("file_head", 2, true, U * 2);
    public static final MaterialTypeItem<?> SAW_BLADE = new MaterialTypeItem<>("saw_blade", 2, true, U * 2);
    public static final MaterialTypeItem<?> SCREWDRIVER_TIP = new MaterialTypeItem<>("screwdriver_tip", 2, true, U);
    public static final MaterialTypeItem<?> SCYTHE_BLADE = new MaterialTypeItem<>("scythe_blade", 2, true, U * 3);
    //Block Types
    public static MaterialTypeBlock<MaterialTypeBlock.IOreGetter> ORE = new MaterialTypeBlock<>("ore", 1, true, -1, (domain, type, mat) -> AntimatterAPI.all(StoneType.class).stream().filter(StoneType::doesGenerateOre).forEach(s -> new BlockOre(domain, mat, s, type)));
    public static MaterialTypeBlock<MaterialTypeBlock.IOreGetter> ORE_SMALL = new MaterialTypeBlock<>("ore_small", 1, false, -1, (domain, type, mat) -> AntimatterAPI.all(StoneType.class).stream().filter(StoneType::doesGenerateOre).forEach(s -> new BlockOre(domain, mat, s, type)));
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> ORE_STONE = new MaterialTypeBlock<>("ore_stone", 1, true, -1,(domain, type, mat) -> new BlockOreStone(domain, mat));
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> BLOCK = new MaterialTypeBlock<>("block", 1, false, U * 9, BlockStorage::new);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> RAW_ORE_BLOCK = new MaterialTypeBlock<>("raw_ore_block", 2, false, -1, BlockStorage::new);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> FRAME = new MaterialTypeBlock<>("frame", 1, true, U * 2, BlockStorage::new);
    //Fluid Types
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> LIQUID = new MaterialTypeFluid<>("liquid", 1, true, -1);
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> GAS = new MaterialTypeFluid<>("gas", 1, true, -1);
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> PLASMA = new MaterialTypeFluid<>("plasma", 1, true, -1);

    static {
        AntimatterMaterialTypes.ROCK.set((m, s) -> {
            if (m == null || s == null || !s.doesGenerateOre() || !AntimatterMaterialTypes.ROCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.ROCK, m, s);
            BlockSurfaceRock rock = AntimatterAPI.get(BlockSurfaceRock.class, "surface_rock_" + m.getId() + "_" + s.getId());
            return new MaterialTypeBlock.Container(rock != null ? rock.defaultBlockState() : Blocks.AIR.defaultBlockState());
        });
        AntimatterMaterialTypes.ORE.set((m, s) -> {
            if (m != null && s != null) {
                Item item = AntimatterAPI.getReplacement(AntimatterMaterialTypes.ORE, m, s);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || s == null || !s.doesGenerateOre() || !AntimatterMaterialTypes.ORE.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.ORE, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, AntimatterMaterialTypes.ORE.getId() + "_" + m.getId() + "_" + s.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        AntimatterMaterialTypes.ORE_SMALL.set((m, s) -> {
            if (m != null && s != null) {
                Item item = AntimatterAPI.getReplacement(AntimatterMaterialTypes.ORE_SMALL, m, s);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || s == null || !AntimatterMaterialTypes.ORE_SMALL.allowGen(m))
                return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.ORE_SMALL, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, AntimatterMaterialTypes.ORE_SMALL.getId() + "_" + m.getId() + "_" + Utils.getConventionalStoneType(s));
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        AntimatterMaterialTypes.ORE_STONE.set(m -> {
            if (m == null || !AntimatterMaterialTypes.ORE_STONE.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.ORE_STONE, m);
            BlockOreStone block = AntimatterAPI.get(BlockOreStone.class, AntimatterMaterialTypes.ORE_STONE.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        AntimatterMaterialTypes.BLOCK.set(m -> {
            if (m != null) {
                Item item = AntimatterAPI.getReplacement(AntimatterMaterialTypes.BLOCK, m);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || !AntimatterMaterialTypes.BLOCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.BLOCK, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, AntimatterMaterialTypes.BLOCK.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        AntimatterMaterialTypes.RAW_ORE_BLOCK.set(m -> {
            if (m != null) {
                Item item = AntimatterAPI.getReplacement(AntimatterMaterialTypes.RAW_ORE_BLOCK, m);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || !AntimatterMaterialTypes.RAW_ORE_BLOCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.RAW_ORE_BLOCK, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, AntimatterMaterialTypes.RAW_ORE_BLOCK.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        AntimatterMaterialTypes.FRAME.set(m -> {
            if (m == null || !AntimatterMaterialTypes.FRAME.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(AntimatterMaterialTypes.FRAME, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, AntimatterMaterialTypes.FRAME.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();

        AntimatterMaterialTypes.LIQUID.set((m, i) -> {
            if (m == null || !AntimatterMaterialTypes.LIQUID.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(AntimatterMaterialTypes.LIQUID, m);
            if (m.getId().equals("water")) return FluidPlatformUtils.createFluidStack(Fluids.WATER, i);
            else if (m.getId().equals("lava")) return FluidPlatformUtils.createFluidStack(Fluids.LAVA, i);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, AntimatterMaterialTypes.LIQUID.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return FluidPlatformUtils.createFluidStack(fluid.getFluid(), i);
        });
        AntimatterMaterialTypes.GAS.set((m, i) -> {
            if (m == null || !AntimatterMaterialTypes.GAS.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(AntimatterMaterialTypes.GAS, m);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, AntimatterMaterialTypes.GAS.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return FluidPlatformUtils.createFluidStack(fluid.getFluid(), i);
        });
        AntimatterMaterialTypes.PLASMA.set((m, i) -> {
            if (m == null || !AntimatterMaterialTypes.PLASMA.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(AntimatterMaterialTypes.PLASMA, m);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, AntimatterMaterialTypes.PLASMA.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return FluidPlatformUtils.createFluidStack(fluid.getFluid(), i);
        });
    }

    public static void init() {

        AntimatterMaterialTypes.NUGGET.replacement(AntimatterMaterials.Iron, () -> Items.IRON_NUGGET);
        AntimatterMaterialTypes.NUGGET.replacement(AntimatterMaterials.Gold, () -> Items.GOLD_NUGGET);
        AntimatterMaterialTypes.INGOT.replacement(AntimatterMaterials.Iron, () -> Items.IRON_INGOT);
        AntimatterMaterialTypes.INGOT.replacement(AntimatterMaterials.Gold, () -> Items.GOLD_INGOT);
        AntimatterMaterialTypes.INGOT.replacement(AntimatterMaterials.Netherite, () -> Items.NETHERITE_INGOT);
        AntimatterMaterialTypes.INGOT.replacement(AntimatterMaterials.Copper, () -> Items.COPPER_INGOT);


        AntimatterMaterialTypes.DUST.replacement(AntimatterMaterials.Redstone, () -> Items.REDSTONE);
        AntimatterMaterialTypes.DUST.replacement(AntimatterMaterials.Glowstone, () -> Items.GLOWSTONE_DUST);
        AntimatterMaterialTypes.DUST.replacement(AntimatterMaterials.Blaze, () -> Items.BLAZE_POWDER);
        AntimatterMaterialTypes.DUST.replacement(AntimatterMaterials.Sugar, () -> Items.SUGAR);
        AntimatterMaterialTypes.RAW_ORE.replacement(AntimatterMaterials.Iron, () -> Items.RAW_IRON);
        AntimatterMaterialTypes.RAW_ORE.replacement(AntimatterMaterials.Copper, () -> Items.RAW_COPPER);
        AntimatterMaterialTypes.RAW_ORE.replacement(AntimatterMaterials.Gold, () -> Items.RAW_GOLD);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Flint, () -> Items.FLINT);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Diamond, () -> Items.DIAMOND);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Emerald, () -> Items.EMERALD);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Lapis, () -> Items.LAPIS_LAZULI);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Quartz, () -> Items.QUARTZ);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Coal, () -> Items.COAL);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.Charcoal, () -> Items.CHARCOAL);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.EnderEye, () -> Items.ENDER_EYE);
        AntimatterMaterialTypes.GEM.replacement(AntimatterMaterials.EnderPearl, () -> Items.ENDER_PEARL);

        AntimatterMaterialTypes.ROD.replacement(AntimatterMaterials.Blaze, () -> Items.BLAZE_ROD);
        AntimatterMaterialTypes.ROD.replacement(AntimatterMaterials.Bone, () -> Items.BONE);
        AntimatterMaterialTypes.ROD.replacement(AntimatterMaterials.Wood, () -> Items.STICK);

        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Coal, () -> Items.COAL_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Iron, () -> Items.IRON_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Copper, () -> Items.COPPER_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Gold, () -> Items.GOLD_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Diamond, () -> Items.DIAMOND_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Emerald, () -> Items.EMERALD_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Lapis, () -> Items.LAPIS_BLOCK);
        AntimatterMaterialTypes.BLOCK.replacement(AntimatterMaterials.Netherite, () -> Items.NETHERITE_BLOCK);
        AntimatterMaterialTypes.RAW_ORE_BLOCK.replacement(AntimatterMaterials.Iron, () -> Items.RAW_IRON_BLOCK);
        AntimatterMaterialTypes.RAW_ORE_BLOCK.replacement(AntimatterMaterials.Copper, () -> Items.RAW_COPPER_BLOCK);
        AntimatterMaterialTypes.RAW_ORE_BLOCK.replacement(AntimatterMaterials.Gold, () -> Items.RAW_GOLD_BLOCK);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Coal, AntimatterStoneTypes.STONE, () -> Items.COAL_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Coal, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_COAL_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Iron, AntimatterStoneTypes.STONE, () -> Items.IRON_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Iron, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_IRON_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Copper, AntimatterStoneTypes.STONE, () -> Items.COPPER_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Copper, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_COPPER_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Gold, AntimatterStoneTypes.STONE, () -> Items.GOLD_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Gold, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_GOLD_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Redstone, AntimatterStoneTypes.STONE, () -> Items.REDSTONE_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Redstone, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_REDSTONE_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Emerald, AntimatterStoneTypes.STONE, () -> Items.EMERALD_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Emerald, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_EMERALD_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Lapis, AntimatterStoneTypes.STONE, () -> Items.LAPIS_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Lapis, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_LAPIS_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Diamond, AntimatterStoneTypes.STONE, () -> Items.DIAMOND_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Diamond, AntimatterStoneTypes.DEEPSLATE, () -> Items.DEEPSLATE_DIAMOND_ORE);
        AntimatterMaterialTypes.ORE.replacement(AntimatterMaterials.Quartz, AntimatterStoneTypes.NETHERRACK, () -> Items.NETHER_QUARTZ_ORE);

        AntimatterMaterialTypes.ROTOR.dependents(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.SCREW, AntimatterMaterialTypes.RING);
        AntimatterMaterialTypes.SCREW.dependents(AntimatterMaterialTypes.BOLT);
        AntimatterMaterialTypes.BOLT.dependents(AntimatterMaterialTypes.ROD);
        AntimatterMaterialTypes.RING.dependents(AntimatterMaterialTypes.ROD);
        AntimatterMaterialTypes.ROD_LONG.dependents(ROD);
        AntimatterMaterialTypes.ROD.dependents(ROD_LONG);
        AntimatterMaterialTypes.CRUSHED.dependents(AntimatterMaterialTypes.CRUSHED_PURIFIED, AntimatterMaterialTypes.CRUSHED_REFINED, AntimatterMaterialTypes.DUST_IMPURE);
        AntimatterMaterialTypes.DUST_PURE.dependents(AntimatterMaterialTypes.DUST);
        AntimatterMaterialTypes.DUST_IMPURE.dependents(AntimatterMaterialTypes.DUST_PURE);
        AntimatterMaterialTypes.DUST.dependents(AntimatterMaterialTypes.DUST_SMALL, AntimatterMaterialTypes.DUST_TINY);
        AntimatterMaterialTypes.GEAR_SMALL.dependents(AntimatterMaterialTypes.PLATE);
        AntimatterMaterialTypes.GEAR.dependents(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.ROD);
        AntimatterMaterialTypes.GEM_EXQUISITE.dependents(GEM_FLAWLESS, GEM_FLAWED, GEM_CHIPPED,  GEM);

        AntimatterMaterialTypes.WIRE_FINE.setIgnoreTextureSets();
        AntimatterMaterialTypes.DUST_TINY.setHidden();
        AntimatterMaterialTypes.DUST_SMALL.setHidden();
        AntimatterMaterialTypes.DRILLBIT.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.CHAINSAWBIT.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.WRENCHBIT.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.BUZZSAW_BLADE.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.PICKAXE_HEAD.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.SHOVEL_HEAD.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.SWORD_BLADE.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.AXE_HEAD.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.HOE_HEAD.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.HAMMER_HEAD.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.FILE_HEAD.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.SAW_BLADE.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.SCREWDRIVER_TIP.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.SCYTHE_BLADE.unSplitName().setIgnoreTextureSets();
        AntimatterMaterialTypes.RAW_ORE.unSplitName();
        AntimatterMaterialTypes.RAW_ORE_BLOCK.unSplitName();
    }

    public static void postInit() {
        AntimatterMaterialTypes.LIQUID.all().stream().filter(l -> !l.getId().equals("water") && !l.getId().equals("lava")).forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, AntimatterMaterialTypes.LIQUID)));
        AntimatterMaterialTypes.GAS.all().forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, AntimatterMaterialTypes.GAS)));
        AntimatterMaterialTypes.PLASMA.all().forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, AntimatterMaterialTypes.PLASMA)));
        //if (AntimatterConfig.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) AntimatterMaterialTypes.ORE.all().forEach(m -> m.flags(AntimatterMaterialTypes.ORE_SMALL));
    }
}
