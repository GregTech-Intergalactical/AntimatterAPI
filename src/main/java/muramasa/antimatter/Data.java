package muramasa.antimatter;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.cover.CoverNone;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.gui.container.*;
import muramasa.antimatter.gui.screen.*;
import muramasa.antimatter.item.DebugScannerItem;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.MaterialSword;
import muramasa.antimatter.tool.behaviour.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.UseAction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

import static muramasa.antimatter.material.TextureSet.NONE;
import static net.minecraft.block.material.Material.*;

public class Data {

    public static final Material NULL = new Material(Ref.ID, "null", 0xffffff, NONE).addTools(5.0F, 5, Integer.MAX_VALUE, 3, ImmutableMap.of(Enchantments.FORTUNE, 3)).addHandleStat(0, 0.0F);

    static {
        StructureBuilder.addGlobalElement("A", StructureElement.AIR);
        StructureBuilder.addGlobalElement(" ", StructureElement.IGNORE);
        NULL.remove(MaterialType.ROD);
    }

    public static DebugScannerItem DEBUG_SCANNER = new DebugScannerItem(Ref.ID, "debug_scanner").tip(TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");

    public static final AntimatterToolType SWORD = new AntimatterToolType(Ref.ID, "sword", 2, 1, 10, 3.0F, -2.4F).setToolClass(MaterialSword.class);
    public static final AntimatterToolType PICKAXE = new AntimatterToolType(Ref.ID, "pickaxe", 1, 2, 10, 1.0F, -2.8F).addEffectiveMaterials(PACKED_ICE, IRON, ROCK, ANVIL, PISTON);
    public static final AntimatterToolType SHOVEL = new AntimatterToolType(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F).addEffectiveMaterials(CLAY, SAND, SNOW, SNOW_BLOCK, EARTH);
    public static final AntimatterToolType AXE = new AntimatterToolType(Ref.ID, "axe", 1, 1, 10, 5.0F, -3.0F).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO);
    public static final AntimatterToolType HOE = new AntimatterToolType(Ref.ID, "hoe", 1, 2, 10, 0.0F, -3.0F);
    public static final AntimatterToolType HAMMER = new AntimatterToolType(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F).addToolTypes("pickaxe").addEffectiveMaterials(IRON, ROCK).setUseSound(SoundEvents.BLOCK_ANVIL_PLACE);
    public static final AntimatterToolType WRENCH = new AntimatterToolType(Ref.ID, "wrench", 2, 2, 2, 1.5F, -2.8F).setUseSound(Ref.WRENCH).setOverlayLayers(0);
    public static final AntimatterToolType SAW = new AntimatterToolType(Ref.ID, "saw", 2, 2, 2, 2.0F, -2.8F);
    public static final AntimatterToolType FILE = new AntimatterToolType(Ref.ID, "file", 2, 2, 2, -2.0F, -2.4F);
    public static final AntimatterToolType CROWBAR = new AntimatterToolType(Ref.ID, "crowbar", 2, 10, 5, 1.0F, -2.0F).setUseSound(SoundEvents.ENTITY_ITEM_BREAK).setSecondaryRequirement(MaterialTag.RUBBERTOOLS);
    public static final AntimatterToolType DRILL = new AntimatterToolType(Ref.ID, "drill", 2, 2, 10, 3.0F, -4.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addToolTypes("pickaxe").addEffectiveMaterials(PACKED_ICE, IRON, ROCK, ANVIL, PISTON);
    public static final AntimatterToolType SCREWDRIVER = new AntimatterToolType(Ref.ID, "screwdriver", 2, 2, 2, 0.0F, -1.0F).setUseSound(Ref.WRENCH);
    public static final AntimatterToolType MORTAR = new AntimatterToolType(Ref.ID, "mortar", 5, 5, 2, -2.0F, 0.0F).setUseSound(SoundEvents.BLOCK_GRINDSTONE_USE).setBlockBreakability(false);
    public static final AntimatterToolType WIRE_CUTTER = new AntimatterToolType(Ref.ID, "wire_cutter", 5, 3, 2, 0.0F, -1.5F).setUseSound(SoundEvents.ENTITY_SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CARPET);
    public static final AntimatterToolType KNIFE = new AntimatterToolType(Ref.ID, "knife", 2, 2, 5, 2.1F, -2.0F).setToolClass(MaterialSword.class);
    public static final AntimatterToolType PLUNGER = new AntimatterToolType(Ref.ID, "plunger", 5, 5, 10, 0.0F, -2.9F).setUseSound(SoundEvents.ITEM_BUCKET_EMPTY).setPrimaryRequirement(MaterialTag.RUBBERTOOLS);
    public static final AntimatterToolType CHAINSAW = new AntimatterToolType(Ref.ID, "chainsaw", 2, 1, 5, 3.0F, -4.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.BOW).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO, LEAVES).addToolTypes("axe", "saw");
    public static final AntimatterToolType ELECTRIC_WRENCH = new AntimatterToolType(Ref.ID, "electric_wrench", WRENCH).setTag(WRENCH).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH);
    public static final AntimatterToolType ELECTRIC_SCREWDRIVER = new AntimatterToolType(Ref.ID, "electric_screwdriver", SCREWDRIVER).setTag(SCREWDRIVER).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).setOverlayLayers(2);
    public static final AntimatterToolType JACKHAMMER = new AntimatterToolType(Ref.ID, "jackhammer", 2, 2, 10, 1.0F, -3.2F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addEffectiveMaterials(ROCK, EARTH, SAND, ORGANIC);
    public static final AntimatterToolType BUZZSAW = new AntimatterToolType(Ref.ID, "buzzsaw", 2, 2, 2, 0.5F, -2.7F).setPowered(100000, 1, 2, 3).setOverlayLayers(2);

    public static Machine<?> MACHINE_INVALID = new Machine<>(Ref.ID, "invalid");

    //TODO: deal with default? Singleton of Cover&CoverInstance is not done.
    public static Cover COVERNONE = new CoverNone();
    public static Cover COVEROUTPUT = new CoverOutput();

    public static CoverInstance COVER_EMPTY = new CoverInstance(COVERNONE);
    public static CoverInstance COVER_OUTPUT = new CoverInstance(COVEROUTPUT);
    public static MenuHandlerMachine<?> BASIC_MENU_HANDLER = new MenuHandlerMachine<ContainerMachine>(Ref.ID, "container_basic") {

        @Nullable
        @Override
        public ContainerMachine getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMachine ? new ContainerBasicMachine((TileEntityMachine) tile, playerInv, this, windowId) : null;
        }

        @Override
        public ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
            return new ScreenBasicMachine(container, inv, name);
        }

    };

    public static MenuHandlerCover COVER_MENU_HANDLER = new MenuHandlerCover<ContainerCover>(Ref.ID, "container_cover") {

        @Override
        public ContainerCover getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return new ContainerCover((CoverInstance) tile,  playerInv, this, windowId);
        }

        @Override
        public ScreenCover getScreen(ContainerCover container, PlayerInventory inv, ITextComponent name) {
            return new ScreenCover(container,inv,name);
        }
    };

    public static MenuHandlerMachine MULTI_MENU_HANDLER = new MenuHandlerMachine<ContainerMachine>(Ref.ID, "container_multi") {
        @Override
        public ContainerMachine getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityMultiMachine ? new ContainerMultiMachine((TileEntityMultiMachine) tile, playerInv, this, windowId) : null;

        }

        @Nullable
        @Override
        public ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
            return new ScreenMultiMachine(container, inv, name);
        }
    };

    public static MenuHandlerMachine HATCH_MENU_HANDLER = new MenuHandlerMachine(Ref.ID, "container_hatch") {
        @Override
        public ContainerMachine getMenu(Object tile, PlayerInventory playerInv, int windowId) {
            return tile instanceof TileEntityHatch ? new ContainerHatchMachine((TileEntityHatch) tile, playerInv, this, windowId) : null;
        }

        @Override
        public ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
            return new ScreenHatchMachine((ContainerMachine)container, inv, name);
        }
    };

    public static void init() {

        AXE.addBehaviour(BehaviourLogStripping.INSTANCE, BehaviourTreeFelling.INSTANCE);
        CHAINSAW.addBehaviour(BehaviourTreeFelling.INSTANCE, BehaviourLogStripping.INSTANCE, new BehaviourAOEBreak(1, 1, 1));
        DRILL.addBehaviour(new BehaviourAOEBreak(1, 1, 1));
        JACKHAMMER.addBehaviour(new BehaviourAOEBreak(1, 0, 2));
        WRENCH.addBehaviour(BehaviourBlockRotate.INSTANCE);
        WRENCH.addBehaviour(new BehaviourConnection(tile -> tile instanceof TileEntityMachine || tile instanceof TileEntityFluidPipe));
        ELECTRIC_WRENCH.addBehaviour(new BehaviourConnection(tile -> tile instanceof TileEntityMachine || tile instanceof TileEntityFluidPipe));
        PLUNGER.addBehaviour(BehaviourWaterlogToggle.INSTANCE);
        WIRE_CUTTER.addBehaviour(new BehaviourConnection(tile -> (tile instanceof TileEntityCable)));
        for (AntimatterToolType type : AntimatterAPI.all(AntimatterToolType.class)) {
            if (type.getToolTypes().contains("shovel")) type.addBehaviour(BehaviourVanillaShovel.INSTANCE);
            if (type.getToolTypes().contains("hoe")) type.addBehaviour(BehaviourBlockTilling.INSTANCE);
            if (type.isPowered()) type.addBehaviour(BehaviourPoweredDebug.INSTANCE);
        }

    }
}
