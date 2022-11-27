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
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.data.AntimatterMaterialTypes;
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
import muramasa.antimatter.item.DebugScannerItem;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.material.MaterialTypeFluid;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.ore.VanillaStoneType;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.structure.BlockStateElement;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
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

    public static void init() {
    }

    public static void postInit() {
        AntimatterMaterialTypes.postInit();
    }
}
