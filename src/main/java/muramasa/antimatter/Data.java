package muramasa.antimatter;

import muramasa.antimatter.behaviour.IItemUse;
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
import muramasa.antimatter.materials.MaterialTag;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.tileentities.TileEntityRecipeMachine;
import muramasa.antimatter.tileentities.multi.TileEntityHatch;
import muramasa.antimatter.tileentities.multi.TileEntityMultiMachine;
import muramasa.antimatter.tools.base.AntimatterToolType;
import muramasa.antimatter.tools.base.MaterialSword;
import muramasa.antimatter.tools.base.MaterialTool;
import muramasa.antimatter.tools.behaviour.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.UseAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

import static muramasa.antimatter.materials.TextureSet.NONE;
import static net.minecraft.block.material.Material.*;

public class Data {

    static {
        StructureBuilder.addGlobalElement("A", StructureElement.AIR);
        StructureBuilder.addGlobalElement(" ", StructureElement.IGNORE);
    }

    public static ItemBasic DEBUG_SCANNER = new DebugScannerItem(Ref.ID, "debug_scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");

    public static Material NULL = new Material(Ref.ID, "null", 0xffffff, NONE);

    public static final AntimatterToolType SWORD = new AntimatterToolType(Ref.ID, "sword", 2, 1, 10, 4.0F, -2.4F).setToolClass(MaterialSword.class);
    public static final AntimatterToolType PICKAXE = new AntimatterToolType(Ref.ID, "pickaxe", 1, 2, 10, 1.5F, -2.8F).addEffectiveMaterials(PACKED_ICE, IRON, ROCK, ANVIL, PISTON);
    public static final AntimatterToolType SHOVEL = new AntimatterToolType(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F).addEffectiveMaterials(CLAY, SAND, SNOW, SNOW_BLOCK, EARTH);
    public static final AntimatterToolType AXE = new AntimatterToolType(Ref.ID, "axe", 1, 1, 10, 3.0F, -3.0F).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO);
    public static final AntimatterToolType HOE = new AntimatterToolType(Ref.ID, "hoe", 1, 2, 10, -1.0F, -3.0F);
    public static final AntimatterToolType HAMMER = new AntimatterToolType(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F).addToolTypes("pickaxe").addEffectiveMaterials(IRON, ROCK).setUseSound(SoundEvents.BLOCK_ANVIL_PLACE);
    public static final AntimatterToolType WRENCH = new AntimatterToolType(Ref.ID, "wrench", 2, 2, 2, 1.5F, -2.8F).setUseSound(Ref.WRENCH).setOverlayLayers(0);
    public static final AntimatterToolType SAW = new AntimatterToolType(Ref.ID, "saw", 2, 2, 2, 1.75F, -3.0F);
    public static final AntimatterToolType FILE = new AntimatterToolType(Ref.ID, "file", 2, 2, 2, 1.0F, -2.4F);
    public static final AntimatterToolType CROWBAR = new AntimatterToolType(Ref.ID, "crowbar", 2, 10, 5, 2.0F, -3.0F).setUseSound(SoundEvents.ENTITY_ITEM_BREAK).setSecondaryRequirement(MaterialTag.RUBBERTOOLS);
    public static final AntimatterToolType DRILL = new AntimatterToolType(Ref.ID, "drill", 2, 2, 10, 0.0F, -1.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addToolTypes("pickaxe").addEffectiveMaterials(PACKED_ICE, IRON, ROCK, ANVIL, PISTON);
    public static final AntimatterToolType SCREWDRIVER = new AntimatterToolType(Ref.ID, "screwdriver", 2, 2, 2, 0.0F, -1.0F).setUseSound(Ref.WRENCH);
    public static final AntimatterToolType MORTAR = new AntimatterToolType(Ref.ID, "mortar", 5, 5, 2, -3.0F, -1.0F).setUseSound(SoundEvents.BLOCK_GRINDSTONE_USE).setBlockBreakability(false);
    public static final AntimatterToolType WIRE_CUTTER = new AntimatterToolType(Ref.ID, "wire_cutter", 5, 3, 2, 0.5F, -1.5F).setUseSound(SoundEvents.ENTITY_SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CARPET);
    public static final AntimatterToolType KNIFE = new AntimatterToolType(Ref.ID, "knife", 2, 2, 5, 1.8F, -1.8F).setToolClass(MaterialSword.class);
    public static final AntimatterToolType PLUNGER = new AntimatterToolType(Ref.ID, "plunger", 5, 5, 10, -1.0F, -3.0F).setUseSound(SoundEvents.ITEM_BUCKET_EMPTY).setPrimaryRequirement(MaterialTag.RUBBERTOOLS);
    public static final AntimatterToolType CHAINSAW = new AntimatterToolType(Ref.ID, "chainsaw", 2, 1, 5, 2.0F, -3.0F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.BOW).addEffectiveMaterials(WOOD, PLANTS, TALL_PLANTS, BAMBOO, LEAVES).addToolTypes("axe", "saw");
    public static final AntimatterToolType ELECTRIC_WRENCH = new AntimatterToolType(Ref.ID, "electric_wrench", 2, 2, 2, 1.5F, -2.8F).setInheritTag(WRENCH).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH);
    public static final AntimatterToolType ELECTRIC_SCREWDRIVER = new AntimatterToolType(Ref.ID, "electric_screwdriver", 2, 2, 2, 0.0F, -1.0F).setInheritTag(SCREWDRIVER).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).setOverlayLayers(2);
    public static final AntimatterToolType JACKHAMMER = new AntimatterToolType(Ref.ID, "jackhammer", 2, 2, 10, 1.0F, -3.2F).setPowered(100000, 1, 2, 3).setUseAction(UseAction.SPEAR).setUseSound(Ref.DRILL).addEffectiveMaterials(ROCK, EARTH, SAND, ORGANIC);
    public static final AntimatterToolType BUZZSAW = new AntimatterToolType(Ref.ID, "buzzsaw", 2, 2, 2, 0.5F, -2.7F).setPowered(100000, 1, 2, 3).setOverlayLayers(2);

    public static Machine MACHINE_INVALID = new Machine(Ref.ID, "invalid");

    public static Cover COVER_NONE = new CoverNone();
    public static Cover COVER_OUTPUT = new CoverOutput();

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
        AXE.addBehaviour(new BehaviourLogStripping());
        AXE.addBehaviour(new BehaviourTreeFelling());
        CHAINSAW.addBehaviour(new BehaviourTreeFelling());
        CHAINSAW.addBehaviour(new BehaviourLogStripping());
        CHAINSAW.addBehaviour(new BehaviourAOEBreak(1, 1, 1));
        DRILL.addBehaviour(new BehaviourAOEBreak(1, 1, 1));
        JACKHAMMER.addBehaviour(new BehaviourAOEBreak(1, 0, 2));
        WRENCH.addBehaviour(new BehaviourBlockRotate());
        PLUNGER.addBehaviour(new BehaviourWaterlogToggle());
        IItemUse<MaterialTool> shovelBehaviour = (instance, c) -> {
            if (c.getFace() == Direction.DOWN) return ActionResultType.PASS;
            BlockState state = c.getWorld().getBlockState(c.getPos());
            BlockState changedState = null;
            if (state.getBlock() == Blocks.GRASS_BLOCK && c.getWorld().isAirBlock(c.getPos().up())) {
                c.getWorld().playSound(c.getPlayer(), c.getPos(), SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                changedState = Blocks.GRASS_PATH.getDefaultState();
            }
            else if (state.getBlock() instanceof CampfireBlock && state.get(CampfireBlock.LIT)) {
                c.getWorld().playEvent(c.getPlayer(), 1009, c.getPos(), 0);
                changedState = state.with(CampfireBlock.LIT, false);
            }
            if (changedState != null) {
                c.getWorld().setBlockState(c.getPos(), changedState, 11);
                c.getItem().damageItem(instance.getType().getUseDurability(), c.getPlayer(), (p) -> p.sendBreakAnimation(c.getHand()));
                return ActionResultType.SUCCESS;
            }
            else return ActionResultType.PASS;
        };

        AntimatterAPI.all(AntimatterToolType.class).stream().filter(t -> t.getToolTypes().contains("shovel")).forEach(t -> t.addBehaviour(shovelBehaviour));

        AntimatterAPI.all(AntimatterToolType.class).stream().filter(t -> t.getToolTypes().contains("hoe")).forEach(t -> t.addBehaviour(new BehaviourBlockTilling()));
        AntimatterAPI.all(AntimatterToolType.class).stream().filter(t -> t.isPowered()).forEach(t -> t.addBehaviour(new BehaviourPoweredDebug()));

    }
}
