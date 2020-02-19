package muramasa.antimatter;

import com.google.common.collect.ImmutableList;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverNone;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.antimatter.gui.container.ContainerBasicMachine;
import muramasa.antimatter.gui.container.ContainerHatchMachine;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.gui.screen.ScreenBasicMachine;
import muramasa.antimatter.gui.screen.ScreenHatchMachine;
import muramasa.antimatter.gui.screen.ScreenMachine;
import muramasa.antimatter.gui.screen.ScreenMultiMachine;
import muramasa.antimatter.items.DebugScannerItem;
import muramasa.antimatter.items.ItemBasic;
import muramasa.antimatter.machines.types.Machine;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.tileentities.TileEntityRecipeMachine;
import muramasa.antimatter.tileentities.multi.TileEntityHatch;
import muramasa.antimatter.tileentities.multi.TileEntityMultiMachine;
import muramasa.antimatter.tree.BlockRubberLeaves;
import muramasa.antimatter.tree.BlockRubberLog;
import muramasa.antimatter.tree.BlockRubberSapling;
import muramasa.antimatter.tree.RubberTree;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.PineFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;

import javax.annotation.Nullable;

import static muramasa.antimatter.materials.TextureSet.DULL;

public class Data {

    static {
        StructureBuilder.addGlobalElement("A", StructureElement.AIR);
        StructureBuilder.addGlobalElement(" ", StructureElement.IGNORE);
    }

    public static ItemBasic DEBUG_SCANNER = new DebugScannerItem(Ref.ID, "debug_scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");

    public static Material NULL = new Material(Ref.ID, "null", 0xffffff, DULL);

    public static Machine MACHINE_INVALID = new Machine(Ref.ID, "invalid");

    public static Cover COVER_NONE = new CoverNone();
    public static Cover COVER_OUTPUT = new CoverOutput();

    // Rubber Tree
    final public static BlockRubberLeaves RUBBER_LEAVES = new BlockRubberLeaves();
    final public static BlockRubberLog RUBBER_LOG = new BlockRubberLog();
    final public static BlockRubberSapling RUBBER_SAPLING = new BlockRubberSapling();
    final public static TreeFeatureConfig RUBBER_TREE_CONFIG_BLOB = (new TreeFeatureConfig.Builder(RubberTree.trunkBlocks, new SimpleBlockStateProvider(RUBBER_LEAVES.getDefaultState()),
            new BlobFoliagePlacer(2, 0))).baseHeight(6).heightRandA(1). // total height
            trunkHeight(2).trunkHeightRandom(1) // bare trunk height
            .trunkTopOffset(2) // depresses trunk top within leaves
            .ignoreVines().decorators(ImmutableList.of(new BeehiveTreeDecorator(0.02F))).setSapling(RUBBER_SAPLING).build();
    final public static TreeFeatureConfig RUBBER_TREE_CONFIG_SPRUCE = (new TreeFeatureConfig.Builder(RubberTree.trunkBlocks, new SimpleBlockStateProvider(RUBBER_LEAVES.getDefaultState()),
            new SpruceFoliagePlacer(2, 0))).baseHeight(7).heightRandA(1).trunkHeight(2).trunkHeightRandom(1).trunkTopOffset(1)
            .ignoreVines().setSapling(RUBBER_SAPLING).build();

    public static MenuHandler BASIC_MENU_HANDLER = new MenuHandler(Ref.ID, "container_basic") {
        @Nullable
        @Override
        public Container getMenu(TileEntity tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityRecipeMachine ? new ContainerBasicMachine((TileEntityRecipeMachine) tile, playerInv, this, windowId) : null;
        }

        @Nullable
        @Override
        public ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
            return new ScreenBasicMachine(container, inv, name);
        }
    };

    public static MenuHandler MULTI_MENU_HANDLER = new MenuHandler(Ref.ID, "container_multi") {
        @Nullable
        @Override
        public Container getMenu(TileEntity tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMultiMachine ? new ContainerMultiMachine((TileEntityMultiMachine) tile, playerInv, this, windowId) : null;
        }

        @Nullable
        @Override
        public ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
            return new ScreenMultiMachine(container, inv, name);
        }
    };

    public static MenuHandler HATCH_MENU_HANDLER = new MenuHandler(Ref.ID, "container_hatch") {
        @Nullable
        @Override
        public Container getMenu(TileEntity tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityHatch ? new ContainerHatchMachine((TileEntityHatch) tile, playerInv, this, windowId) : null;
        }

        @Override
        public ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
            return new ScreenHatchMachine(container, inv, name);
        }
    };

    public static void init() {

    }
}
