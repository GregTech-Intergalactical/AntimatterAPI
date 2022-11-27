package muramasa.antimatter;

import muramasa.antimatter.block.BlockProxy;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.cover.CoverDebug;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.CoverHeat;
import muramasa.antimatter.cover.CoverInput;
import muramasa.antimatter.cover.CoverMuffler;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.CoverPlate;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterMaterialFluid;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.MenuHandlerPipe;
import muramasa.antimatter.gui.container.ContainerBasicMachine;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.container.ContainerHatch;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.item.CoverMaterialItem;
import muramasa.antimatter.item.DebugScannerItem;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeFluid;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.ore.VanillaStoneType;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.BlockStateElement;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.behaviour.BehaviourBlockTilling;
import muramasa.antimatter.tool.behaviour.BehaviourExtendedHighlight;
import muramasa.antimatter.tool.behaviour.BehaviourPoweredDebug;
import muramasa.antimatter.tool.behaviour.BehaviourVanillaShovel;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import tesseract.FluidPlatformUtils;

import javax.annotation.Nullable;

;

public class Data {

    public static final net.minecraft.world.level.material.Material WRENCH_MATERIAL = new net.minecraft.world.level.material.Material(MaterialColor.METAL, false, true, true, true, false, false, PushReaction.NORMAL);


    //Item Types
    public static MaterialTypeItem<?> DUST = new MaterialTypeItem<>("dust", 2, true, Ref.U);
    public static MaterialTypeItem<?> DUST_SMALL = new MaterialTypeItem<>("dust_small", 2, true, Ref.U4);
    public static MaterialTypeItem<?> DUST_TINY = new MaterialTypeItem<>("dust_tiny", 2, true, Ref.U9);
    public static MaterialTypeItem<?> DUST_IMPURE = new MaterialTypeItem<>("dust_impure", 2, true, Ref.U);
    public static MaterialTypeItem<?> DUST_PURE = new MaterialTypeItem<>("dust_pure", 2, true, Ref.U);
    public static MaterialTypeItem<MaterialTypeBlock.IOreGetter> ROCK = new MaterialTypeItem<>("rock", 2, false, Ref.U9, (domain, type, mat) -> {
        AntimatterAPI.all(StoneType.class, s -> AntimatterAPI.register(BlockSurfaceRock.class, new BlockSurfaceRock(domain, mat, s)));
        new MaterialItem(domain, type, mat);
    });
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
    public static MaterialTypeItem<?> PLATE = new MaterialTypeItem<>("plate", 2, true, Ref.U, (a, b, c) -> CoverFactory.builder((u, v, t, w) -> new CoverPlate(u, v, t, w, b, c)).item((u, v) -> new CoverMaterialItem(u.getDomain(), b, u, c)).build(Ref.ID, "plate_" + c.getId()));
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
    public static MaterialTypeBlock<MaterialTypeBlock.IOreGetter> ORE = new MaterialTypeBlock<>("ore", 1, true, -1, (domain, type, mat) -> AntimatterAPI.all(StoneType.class, s -> new BlockOre(domain, mat, s, type)));
    public static MaterialTypeBlock<MaterialTypeBlock.IOreGetter> ORE_SMALL = new MaterialTypeBlock<>("ore_small", 1, false, -1, (domain, type, mat) -> AntimatterAPI.all(StoneType.class, s -> new BlockOre(domain, mat, s, type)));
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> ORE_STONE = new MaterialTypeBlock<>("ore_stone", 1, true, -1,(domain, type, mat) -> new BlockOreStone(domain, mat));
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> BLOCK = new MaterialTypeBlock<>("block", 1, false, -1, BlockStorage::new);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> RAW_ORE_BLOCK = new MaterialTypeBlock<>("raw_ore_block", 2, false, -1, BlockStorage::new);
    public static MaterialTypeBlock<MaterialTypeBlock.IBlockGetter> FRAME = new MaterialTypeBlock<>("frame", 1, true, -1, BlockStorage::new);

    //Fluid Types
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> LIQUID = new MaterialTypeFluid<>("liquid", 1, true, -1);
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> GAS = new MaterialTypeFluid<>("gas", 1, true, -1);
    public static MaterialTypeFluid<MaterialTypeFluid.IFluidGetter> PLASMA = new MaterialTypeFluid<>("plasma", 1, true, -1);

    public static StoneType STONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "stone", AntimatterMaterials.Stone, new Texture("minecraft", "block/stone"), SoundType.STONE, false).setState(Blocks.STONE));

    public static StoneType GRANITE = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "granite", AntimatterMaterials.Granite, "block/stone/", new Texture("minecraft", "block/granite"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || Ref.debugStones).setState(Blocks.GRANITE));
    public static StoneType DIORITE = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "diorite", AntimatterMaterials.Diorite, "block/stone/", new Texture("minecraft", "block/diorite"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || Ref.debugStones).setState(Blocks.DIORITE));
    public static StoneType ANDESITE = AntimatterAPI.register(StoneType.class, new VanillaStoneType(Ref.ID, "andesite", AntimatterMaterials.Andesite, "block/stone/", new Texture("minecraft", "block/andesite"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || Ref.debugStones).setState(Blocks.ANDESITE));
    public static StoneType DEEPSLATE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "deepslate", AntimatterMaterials.Deepslate, new Texture("minecraft", "block/deepslate"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || Ref.debugStones).setState(Blocks.DEEPSLATE));
    public static StoneType TUFF = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "tuff", AntimatterMaterials.Tuff, new Texture("minecraft", "block/tuff"), SoundType.STONE, AntimatterConfig.WORLD.VANILLA_STONE_GEN || Ref.debugStones).setState(Blocks.TUFF));

    public static StoneType GRAVEL = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "gravel", AntimatterMaterials.Gravel, new Texture("minecraft", "block/gravel"), SoundType.GRAVEL, false).setState(Blocks.GRAVEL).setGravity(true).setBlockMaterial(net.minecraft.world.level.material.Material.SAND).setHardnessAndResistance(0.6F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL));
    public static StoneType DIRT = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "dirt", AntimatterMaterials.Dirt, new Texture("minecraft", "block/dirt"), SoundType.GRAVEL, false).setState(Blocks.DIRT).setBlockMaterial(net.minecraft.world.level.material.Material.DIRT).setHardnessAndResistance(0.5F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL));
    public static StoneType SAND = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sand", AntimatterMaterials.Sand, new Texture("minecraft", "block/sand"), SoundType.SAND, false).setState(Blocks.SAND).setGravity(true).setBlockMaterial(net.minecraft.world.level.material.Material.SAND).setHardnessAndResistance(0.5F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL));
    public static StoneType SAND_RED = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sand_red", AntimatterMaterials.RedSand, new Texture("minecraft", "block/red_sand"), SoundType.SAND, false).setState(Blocks.RED_SAND).setGravity(true).setBlockMaterial(net.minecraft.world.level.material.Material.SAND).setHardnessAndResistance(0.5F).setRequiresTool(false).setType(BlockTags.MINEABLE_WITH_SHOVEL));
    public static StoneType SANDSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "sandstone", AntimatterMaterials.Sandstone, new Texture("minecraft", "block/sandstone"), SoundType.STONE, false).setState(Blocks.SANDSTONE));
    public static StoneType BASALT_VANILLA = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "vanilla_basalt", AntimatterMaterials.Basalt, new Texture("minecraft", "block/basalt_side"), SoundType.BASALT, false).setState(Blocks.BASALT).setHardnessAndResistance(1.25F, 4.2F));
    public static StoneType BLACKSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "blackstone", AntimatterMaterials.Blackstone, new Texture("minecraft", "block/blackstone"), SoundType.STONE, false).setState(Blocks.BLACKSTONE));

    public static StoneType NETHERRACK = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "netherrack", AntimatterMaterials.Netherrack, new Texture("minecraft", "block/netherrack"), SoundType.NETHERRACK, false).setState(Blocks.NETHERRACK).setHardnessAndResistance(0.4F));
    public static StoneType ENDSTONE = AntimatterAPI.register(StoneType.class, new StoneType(Ref.ID, "endstone", AntimatterMaterials.Endstone, new Texture("minecraft", "block/end_stone"), SoundType.STONE, false).setState(Blocks.END_STONE).setHardnessAndResistance(3.0F, 9.0F));

    static {
        StructureBuilder.addGlobalElement("A", BlockStateElement.AIR);
        StructureBuilder.addGlobalElement(" ", StructureElement.IGNORE);

        ROCK.set((m, s) -> {
            if (m == null || s == null || !ROCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ROCK, m, s);
            BlockSurfaceRock rock = AntimatterAPI.get(BlockSurfaceRock.class, "surface_rock_" + m.getId() + "_" + s.getId());
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
        RAW_ORE_BLOCK.set(m -> {
            if (m != null) {
                Item item = AntimatterAPI.getReplacement(RAW_ORE_BLOCK, m);
                if (item instanceof BlockItem) {
                    return new MaterialTypeBlock.Container(((BlockItem) item).getBlock().defaultBlockState());
                }
            }
            if (m == null || !RAW_ORE_BLOCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(RAW_ORE_BLOCK, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, RAW_ORE_BLOCK.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();
        FRAME.set(m -> {
            if (m == null || !FRAME.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(FRAME, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, FRAME.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }).blockType();

        LIQUID.set((m, i) -> {
            if (m == null || !LIQUID.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(LIQUID, m);
            if (m.getId().equals("water")) return FluidPlatformUtils.createFluidStack(Fluids.WATER, i);
            else if (m.getId().equals("lava")) return FluidPlatformUtils.createFluidStack(Fluids.LAVA, i);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, LIQUID.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return FluidPlatformUtils.createFluidStack(fluid.getFluid(), i);
        });
        GAS.set((m, i) -> {
            if (m == null || !GAS.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(GAS, m);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, GAS.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return FluidPlatformUtils.createFluidStack(fluid.getFluid(), i);
        });
        PLASMA.set((m, i) -> {
            if (m == null || !PLASMA.allowGen(m)) return MaterialTypeFluid.getEmptyFluidAndLog(PLASMA, m);
            AntimatterFluid fluid = AntimatterAPI.get(AntimatterFluid.class, PLASMA.getId() + "_" + m.getId());
            if (fluid == null) throw new IllegalStateException("Tried to get null fluid");
            return FluidPlatformUtils.createFluidStack(fluid.getFluid(), i);
        });
    }

    public static DebugScannerItem DEBUG_SCANNER = new DebugScannerItem(Ref.ID, "debug_scanner").tip(ChatFormatting.AQUA + "" + ChatFormatting.ITALIC + "Development Item");

    //public static Machine<?> MACHINE_INVALID = new Machine<>(Ref.ID, "invalid");

    public static CoverFactory COVEROUTPUT = CoverFactory.builder(CoverOutput::new).addTextures(new Texture(Ref.ID, "block/cover/output")).build(Ref.ID, "output");
    public static CoverFactory COVERHEAT = CoverFactory.builder(CoverHeat::new).addTextures(new Texture(Ref.ID, "block/cover/output")).build(Ref.ID, "heat");
    public static CoverFactory COVERDEBUG = CoverFactory.builder(CoverDebug::new).addTextures(new Texture(Ref.ID, "block/cover/debug")).build(Ref.ID, "debug_cover");
    public static ItemCover COVERDEBUG_ITEM = new ItemCover(Ref.ID, "debug_cover");

    public static CoverFactory COVERINPUT = CoverFactory.builder(CoverInput::new).addTextures(new Texture(Ref.ID, "block/cover/input")).build(Ref.ID, "input");
    public static CoverFactory COVERMUFFLER = CoverFactory.builder(CoverMuffler::new).addTextures(new Texture(Ref.ID, "block/cover/muffler")).build(Ref.ID, "muffler");
    public static CoverFactory COVERDYNAMO = CoverFactory.builder(CoverDynamo::new).addTextures(new Texture(Ref.ID, "block/cover/dynamo")).build(Ref.ID, "dynamo");
    public static CoverFactory COVERENERGY = CoverFactory.builder(CoverEnergy::new).addTextures(new Texture(Ref.ID, "block/cover/energy")).build(Ref.ID, "energy");

    public static BlockProxy PROXY_INSTANCE = new BlockProxy(Ref.ID, "proxy", BlockBehaviour.Properties.of(net.minecraft.world.level.material.Material.STONE).strength(1.0f, 1.0f).noOcclusion());


    public static MenuHandlerMachine<? extends TileEntityMachine, ? extends ContainerBasicMachine> BASIC_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_basic") {
        @Nullable
        @Override
        public ContainerMachine<?> getMenu(IGuiHandler tile, Inventory playerInv, int windowId) {
            return tile instanceof TileEntityMachine ? new ContainerBasicMachine((TileEntityMachine<?>) tile, playerInv, this, windowId) : null;
        }
    };

    public static MenuHandlerPipe<?> PIPE_MENU_HANDLER = new MenuHandlerPipe<>(Ref.ID, "container_pipe");

    public static MenuHandlerCover<ContainerCover> COVER_MENU_HANDLER = new MenuHandlerCover<>(Ref.ID, "container_cover") {
        @Override
        public ContainerCover getMenu(IGuiHandler tile, Inventory playerInv, int windowId) {
            return new ContainerCover((ICover) tile, playerInv, this, windowId);
        }
    };

    public static MenuHandlerMachine<? extends TileEntityMultiMachine, ? extends ContainerMultiMachine> MULTI_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_multi") {
        @Override
        public ContainerMultiMachine getMenu(IGuiHandler tile, Inventory playerInv, int windowId) {
            return tile instanceof TileEntityMultiMachine ? new ContainerMultiMachine((TileEntityMultiMachine<?>) tile, playerInv, this, windowId) : null;
        }

        @Override
        public String screenID() {
            return "multi";
        }
    };

    public static MenuHandlerMachine<? extends TileEntityHatch, ? extends ContainerHatch> HATCH_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_hatch") {
        @Override
        public ContainerHatch getMenu(IGuiHandler tile, Inventory playerInv, int windowId) {
            return tile instanceof TileEntityHatch ? new ContainerHatch((TileEntityHatch<?>) tile, playerInv, this, windowId) : null;
        }

        @Override
        public String screenID() {
            return "hatch";
        }
    };

    public static void init(Side side) {

        NUGGET.replacement(AntimatterMaterials.Iron, () -> Items.IRON_NUGGET);
        NUGGET.replacement(AntimatterMaterials.Gold, () -> Items.GOLD_NUGGET);
        INGOT.replacement(AntimatterMaterials.Iron, () -> Items.IRON_INGOT);
        INGOT.replacement(AntimatterMaterials.Gold, () -> Items.GOLD_INGOT);
        INGOT.replacement(AntimatterMaterials.Netherite, () -> Items.NETHERITE_INGOT);
        INGOT.replacement(AntimatterMaterials.Copper, () -> Items.COPPER_INGOT);


        DUST.replacement(AntimatterMaterials.Redstone, () -> Items.REDSTONE);
        DUST.replacement(AntimatterMaterials.Glowstone, () -> Items.GLOWSTONE_DUST);
        DUST.replacement(AntimatterMaterials.Blaze, () -> Items.BLAZE_POWDER);
        DUST.replacement(AntimatterMaterials.Sugar, () -> Items.SUGAR);
        RAW_ORE.replacement(AntimatterMaterials.Iron, () -> Items.RAW_IRON);
        RAW_ORE.replacement(AntimatterMaterials.Copper, () -> Items.RAW_COPPER);
        RAW_ORE.replacement(AntimatterMaterials.Gold, () -> Items.RAW_GOLD);
        GEM.replacement(AntimatterMaterials.Flint, () -> Items.FLINT);
        GEM.replacement(AntimatterMaterials.Diamond, () -> Items.DIAMOND);
        GEM.replacement(AntimatterMaterials.Emerald, () -> Items.EMERALD);
        GEM.replacement(AntimatterMaterials.Lapis, () -> Items.LAPIS_LAZULI);
        GEM.replacement(AntimatterMaterials.Coal, () -> Items.COAL);
        GEM.replacement(AntimatterMaterials.Charcoal, () -> Items.CHARCOAL);
        GEM.replacement(AntimatterMaterials.EnderEye, () -> Items.ENDER_EYE);
        GEM.replacement(AntimatterMaterials.EnderPearl, () -> Items.ENDER_PEARL);

        ROD.replacement(AntimatterMaterials.Blaze, () -> Items.BLAZE_ROD);
        ROD.replacement(AntimatterMaterials.Bone, () -> Items.BONE);
        ROD.replacement(AntimatterMaterials.Wood, () -> Items.STICK);

        BLOCK.replacement(AntimatterMaterials.Iron, () -> Items.IRON_BLOCK);
        BLOCK.replacement(AntimatterMaterials.Copper, () -> Items.COPPER_BLOCK);
        BLOCK.replacement(AntimatterMaterials.Gold, () -> Items.GOLD_BLOCK);
        BLOCK.replacement(AntimatterMaterials.Diamond, () -> Items.DIAMOND_BLOCK);
        BLOCK.replacement(AntimatterMaterials.Emerald, () -> Items.EMERALD_BLOCK);
        BLOCK.replacement(AntimatterMaterials.Lapis, () -> Items.LAPIS_BLOCK);
        BLOCK.replacement(AntimatterMaterials.Netherite, () -> Items.NETHERITE_BLOCK);
        RAW_ORE_BLOCK.replacement(AntimatterMaterials.Iron, () -> Items.RAW_IRON_BLOCK);
        RAW_ORE_BLOCK.replacement(AntimatterMaterials.Copper, () -> Items.RAW_COPPER_BLOCK);
        RAW_ORE_BLOCK.replacement(AntimatterMaterials.Gold, () -> Items.RAW_GOLD_BLOCK);

        ROTOR.dependents(PLATE, SCREW, RING);
        SCREW.dependents(BOLT);
        BOLT.dependents(ROD);
        RING.dependents(ROD);
        CRUSHED.dependents(CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, DUST_IMPURE);
        DUST_PURE.dependents(DUST);
        DUST_IMPURE.dependents(DUST_PURE);
        DUST.dependents(DUST_SMALL, DUST_TINY);
        GEAR_SMALL.dependents(PLATE);
        GEAR.dependents(PLATE, ROD);

        DUST_TINY.setHidden();
        DUST_SMALL.setHidden();
        DRILLBIT.setHidden().unSplitName();
        CHAINSAWBIT.setHidden().unSplitName();
        WRENCHBIT.setHidden().unSplitName();
        BUZZSAW_BLADE.setHidden().unSplitName();
        RAW_ORE.unSplitName();
        RAW_ORE_BLOCK.unSplitName();
    }

    public static void postInit() {
        LIQUID.all().stream().filter(l -> !l.getId().equals("water") && !l.getId().equals("lava")).forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, LIQUID)));
        GAS.all().forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, GAS)));
        PLASMA.all().forEach(m -> AntimatterAPI.register(AntimatterFluid.class, new AntimatterMaterialFluid(Ref.SHARED_ID, m, PLASMA)));
        AntimatterAPI.all(Material.class, Material::setChemicalFormula);
        if (AntimatterConfig.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) ORE.all().forEach(m -> m.flags(ORE_SMALL));
    }
}
