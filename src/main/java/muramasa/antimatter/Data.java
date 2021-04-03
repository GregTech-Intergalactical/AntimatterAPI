package muramasa.antimatter;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.cover.*;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.container.*;
import muramasa.antimatter.item.DebugScannerItem;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.*;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.MaterialSword;
import muramasa.antimatter.tool.behaviour.*;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static muramasa.antimatter.material.TextureSet.NONE;
import static net.minecraft.block.material.Material.*;

public class Data {

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
    public static MaterialTypeItem<?> INGOT = new MaterialTypeItem<>("ingot", 2, true, Ref.U);
    public static MaterialTypeItem<?> INGOT_HOT = new MaterialTypeItem<>("ingot_hot", 2, true, Ref.U);
    public static MaterialTypeItem<?> NUGGET = new MaterialTypeItem<>("nugget", 2, true, Ref.U9);
    public static MaterialTypeItem<?> GEM = new MaterialTypeItem<>("gem", 2, true, Ref.U);
    public static MaterialTypeItem<?> GEM_BRITTLE = new MaterialTypeItem<>("gem_brittle", 2, true, Ref.U);
    public static MaterialTypeItem<?> GEM_POLISHED = new MaterialTypeItem<>("gem_polished", 2, true, Ref.U);
    public static MaterialTypeItem<?> LENS = new MaterialTypeItem<>("lens", 2, true, Ref.U * 3 / 4);
    public static MaterialTypeItem<?> PLATE = new MaterialTypeItem<>("plate", 2, true, Ref.U, (a,b,c) -> new CoverMaterialItem(a,b,new CoverPlate(a, b, c),c));
    public static MaterialTypeItem<?> PLATE_DENSE = new MaterialTypeItem<>("plate_dense", 2, true, Ref.U * 9);
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

    public static final Material NULL = new Material(Ref.ID, "null", 0xffffff, NONE).addTools(5.0F, 5, Integer.MAX_VALUE, 3, ImmutableMap.of(Enchantments.FORTUNE, 3)).addHandleStat(0, 0.0F);

    static {
        StructureBuilder.addGlobalElement("A", StructureElement.AIR);
        StructureBuilder.addGlobalElement(" ", StructureElement.IGNORE);
        NULL.remove(ROD);

        ROCK.set((m, s) -> {
            if (m == null || s == null || !ROCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ROCK, m, s);
            BlockSurfaceRock rock = AntimatterAPI.get(BlockSurfaceRock.class, "surface_rock_" + m.getId() + "_" + Utils.getConventionalStoneType(s));
            return new MaterialTypeBlock.Container(rock != null ? rock.getDefaultState() : Blocks.AIR.getDefaultState());
        });
        ORE.set((m, s) -> {
            if (m == null || s == null || !ORE.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ORE, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, ORE.getId() + "_" + m.getId() + "_" + Utils.getConventionalStoneType(s));
            return new MaterialTypeBlock.Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        ORE_SMALL.set((m, s) -> {
            if (m == null || s == null || !ORE_SMALL.allowGen(m))
                return MaterialTypeBlock.getEmptyBlockAndLog(ORE_SMALL, m, s);
            BlockOre block = AntimatterAPI.get(BlockOre.class, ORE_SMALL.getId() + "_" + m.getId() + "_" + Utils.getConventionalStoneType(s));
            return new MaterialTypeBlock.Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        ORE_STONE.set(m -> {
            if (m == null || !ORE_STONE.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(ORE_STONE, m);
            BlockOreStone block = AntimatterAPI.get(BlockOreStone.class, ORE_STONE.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        BLOCK.set(m -> {
            if (m == null || !BLOCK.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(BLOCK, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, BLOCK.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
        }).blockType();
        FRAME.set(m -> {
            if (m == null || !FRAME.allowGen(m)) return MaterialTypeBlock.getEmptyBlockAndLog(FRAME, m);
            BlockStorage block = AntimatterAPI.get(BlockStorage.class, FRAME.getId() + "_" + m.getId());
            return new MaterialTypeBlock.Container(block != null ? block.getDefaultState() : Blocks.AIR.getDefaultState());
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

    public static final AntimatterToolType SWORD = new AntimatterToolType(Ref.ID, "sword", 2, 1, 10, 3.0F, -2.4F).setToolClass(MaterialSword.class);
    public static final AntimatterToolType PICKAXE = new AntimatterToolType(Ref.ID, "pickaxe", 1, 2, 10, 1.0F, -2.8F).addEffectiveMaterials(PACKED_ICE, IRON, net.minecraft.block.material.Material.ROCK, ANVIL, PISTON);
    public static final AntimatterToolType SHOVEL = new AntimatterToolType(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F).addEffectiveMaterials(CLAY, SAND, SNOW, SNOW_BLOCK, EARTH);
    public static final AntimatterToolType AXE = new AntimatterToolType(Ref.ID, "axe", 1, 1, 10, 5.0F, -3.0F).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO);
    public static final AntimatterToolType HOE = new AntimatterToolType(Ref.ID, "hoe", 1, 2, 10, 0.0F, -3.0F);
    public static final AntimatterToolType HAMMER = new AntimatterToolType(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F).addToolTypes("pickaxe").addEffectiveMaterials(IRON, net.minecraft.block.material.Material.ROCK).setUseSound(SoundEvents.BLOCK_ANVIL_PLACE);
    public static final AntimatterToolType WRENCH = new AntimatterToolType(Ref.ID, "wrench", 2, 2, 2, 1.5F, -2.8F).setUseSound(Ref.WRENCH).setOverlayLayers(0);
    public static final AntimatterToolType SAW = new AntimatterToolType(Ref.ID, "saw", 2, 2, 2, 2.0F, -2.8F);
    public static final AntimatterToolType FILE = new AntimatterToolType(Ref.ID, "file", 2, 2, 2, -2.0F, -2.4F);
    public static final AntimatterToolType CROWBAR = new AntimatterToolType(Ref.ID, "crowbar", 2, 10, 5, 1.0F, -2.0F).setUseSound(SoundEvents.ENTITY_ITEM_BREAK).setSecondaryRequirement(MaterialTag.RUBBERTOOLS);
    public static final AntimatterToolType DRILL = new AntimatterToolType(Ref.ID, "drill", 2, 2, 10, 3.0F, -4.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addToolTypes("pickaxe").addEffectiveMaterials(PACKED_ICE, IRON, net.minecraft.block.material.Material.ROCK, ANVIL, PISTON);
    public static final AntimatterToolType SCREWDRIVER = new AntimatterToolType(Ref.ID, "screwdriver", 2, 2, 2, 0.0F, -1.0F).setUseSound(Ref.WRENCH);
    public static final AntimatterToolType MORTAR = new AntimatterToolType(Ref.ID, "mortar", 5, 5, 2, -2.0F, 0.0F).setUseSound(SoundEvents.BLOCK_GRINDSTONE_USE).setBlockBreakability(false);
    public static final AntimatterToolType WIRE_CUTTER = new AntimatterToolType(Ref.ID, "wire_cutter", 5, 3, 2, 0.0F, -1.5F).setUseSound(SoundEvents.ENTITY_SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CARPET);
    public static final AntimatterToolType KNIFE = new AntimatterToolType(Ref.ID, "knife", 2, 2, 5, 2.1F, -2.0F).setToolClass(MaterialSword.class);
    public static final AntimatterToolType PLUNGER = new AntimatterToolType(Ref.ID, "plunger", 5, 5, 10, 0.0F, -2.9F).setUseSound(SoundEvents.ITEM_BUCKET_EMPTY).setPrimaryRequirement(MaterialTag.RUBBERTOOLS);
    public static final AntimatterToolType CHAINSAW = new AntimatterToolType(Ref.ID, "chainsaw", 2, 1, 5, 3.0F, -4.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.BOW).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO, LEAVES).addToolTypes("axe", "saw");
    public static final AntimatterToolType ELECTRIC_WRENCH = new AntimatterToolType(Ref.ID, "electric_wrench", WRENCH).setTag(WRENCH).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH);
    public static final AntimatterToolType ELECTRIC_SCREWDRIVER = new AntimatterToolType(Ref.ID, "electric_screwdriver", SCREWDRIVER).setTag(SCREWDRIVER).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).setOverlayLayers(2);
    public static final AntimatterToolType JACKHAMMER = new AntimatterToolType(Ref.ID, "jackhammer", 2, 2, 10, 1.0F, -3.2F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addEffectiveMaterials(net.minecraft.block.material.Material.ROCK, EARTH, SAND, ORGANIC);
    public static final AntimatterToolType BUZZSAW = new AntimatterToolType(Ref.ID, "buzzsaw", 2, 2, 2, 0.5F, -2.7F).setPowered(100000, 1, 2, 3).setOverlayLayers(2);

    public static Machine<?> MACHINE_INVALID = new Machine<>(Ref.ID, "invalid");

    public static BaseCover COVERNONE = new CoverNone(); //TODO: deal with default? Singleton of Cover&CoverInstance is not done.
    public static CoverOutput COVEROUTPUT = new CoverOutput();
    public static ICover COVERINPUT = new CoverInput();
    public static ICover COVERMUFFLER = new CoverMuffler();
    public static ICover COVERDYNAMO = new CoverDynamo("dynamo");
    public static ICover COVERENERGY = new CoverEnergy();
    public static ICover COVERBUFFERONE = new CoverDynamo("buffer_one");
    public static ICover COVERBUFFERFOUR = new CoverDynamo("buffer_four");
    public static ICover COVERBUFFERNINE = new CoverDynamo("buffer_nine");

    public static CoverStack<?> COVER_EMPTY = new CoverStack<>(COVERNONE);
    public static CoverStack<?> COVER_OUTPUT = new CoverStack<>(COVEROUTPUT);


    public static MenuHandlerMachine<ContainerMachine> BASIC_MENU_HANDLER = new MenuHandlerMachine<ContainerMachine>(Ref.ID, "container_basic") {
        @Nullable
        @Override
        public ContainerMachine getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMachine ? new ContainerBasicMachine((TileEntityMachine) tile, playerInv, this, windowId) : null;
        }
    };

    public static MenuHandlerMachine<ContainerMachine> STEAM_MENU_HANDLER = new MenuHandlerMachine<ContainerMachine>(Ref.ID, "container_steam") {
        @Nullable
        @Override
        public ContainerMachine getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMachine ? new ContainerBasicMachine((TileEntityMachine) tile, playerInv, this, windowId) : null;
        }
    };

    public static MenuHandlerCover<ContainerCover> COVER_MENU_HANDLER = new MenuHandlerCover<ContainerCover>(Ref.ID, "container_cover") {
        @Override
        public ContainerCover getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return new ContainerCover((CoverStack<?>) tile, playerInv, this, windowId);
        }
    };

    public static MenuHandlerMachine<ContainerMultiMachine> MULTI_MENU_HANDLER = new MenuHandlerMachine<ContainerMultiMachine>(Ref.ID, "container_multi") {
        @Override
        public ContainerMultiMachine getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMultiMachine ? new ContainerMultiMachine((TileEntityMultiMachine) tile, playerInv, this, windowId) : null;
        }
    };

    public static MenuHandlerMachine<ContainerHatch> HATCH_MENU_HANDLER = new MenuHandlerMachine<ContainerHatch>(Ref.ID, "container_hatch") {
        @Override
        public ContainerHatch getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityHatch ? new ContainerHatch((TileEntityHatch) tile, playerInv, this, windowId) : null;
        }
    };

    public static void init(Dist side) {
        AXE.addBehaviour(BehaviourLogStripping.INSTANCE, BehaviourTreeFelling.INSTANCE);
        CHAINSAW.addBehaviour(BehaviourTreeFelling.INSTANCE, BehaviourLogStripping.INSTANCE, new BehaviourAOEBreak(1, 1, 1));
        DRILL.addBehaviour(new BehaviourAOEBreak(1, 1, 1));
        JACKHAMMER.addBehaviour(new BehaviourAOEBreak(1, 0, 2));
        PLUNGER.addBehaviour(BehaviourWaterlogToggle.INSTANCE);
        for (AntimatterToolType type : AntimatterAPI.all(AntimatterToolType.class)) {
            if (type.getToolTypes().contains("shovel")) type.addBehaviour(BehaviourVanillaShovel.INSTANCE);
            if (type.getToolTypes().contains("hoe")) type.addBehaviour(BehaviourBlockTilling.INSTANCE);
            if (type.isPowered()) type.addBehaviour(BehaviourPoweredDebug.INSTANCE);
        }
        if (side == Dist.CLIENT) clientBehaviours();
    }

    private static void clientBehaviours() {
        WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe));
        ELECTRIC_WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe));
        WIRE_CUTTER.addBehaviour(new BehaviourConnection(b -> b instanceof BlockPipe));
        CROWBAR.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe));
    }
}
