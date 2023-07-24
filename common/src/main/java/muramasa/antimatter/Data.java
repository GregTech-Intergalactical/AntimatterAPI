package muramasa.antimatter;

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
import muramasa.antimatter.machine.types.BasicMachine;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tile.single.TileEntityInfiniteStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;

;import static muramasa.antimatter.gui.ButtonBody.*;
import static muramasa.antimatter.gui.ButtonBody.APAD_RIGHT;
import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.GUI;
import static muramasa.antimatter.machine.Tier.MAX;

public class Data {

    public static final Material WRENCH_MATERIAL = new Material(MaterialColor.METAL, false, true, true, true, false, false, PushReaction.NORMAL);

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

    public static final BasicMachine CREATIVE_GENERATOR = new BasicMachine(Ref.ID, "creative_generator").addFlags(ENERGY, GUI).setTiers(MAX).setAllowVerticalFacing(true).allowFrontIO().setTile(TileEntityInfiniteStorage::new)
            .noCovers();

    public static void init(Side side) {
        if (side.isClient()){
            CREATIVE_GENERATOR.addGuiCallback(t -> {
                t.addButton(10, 18, 14, 14, APAD_LEFT)
                        .addButton(25, 18, 14, 14, PAD_LEFT)
                        .addButton(10, 33, 14, 14, APAD_LEFT)
                        .addButton(25, 33, 14, 14, PAD_LEFT)
                        .addButton(10, 48, 14, 14, APAD_LEFT)
                        .addButton(25, 48, 14, 14, PAD_LEFT)
                        .addButton(10, 63, 14, 14, APAD_LEFT)
                        .addButton(25, 63, 14, 14, PAD_LEFT)
                        .addButton(137, 18, 14, 14, PAD_RIGHT)
                        .addButton(152, 18, 14, 14, APAD_RIGHT)
                        .addButton(137, 33, 14, 14, PAD_RIGHT)
                        .addButton(152, 33, 14, 14, APAD_RIGHT)
                        .addButton(137, 48, 14, 14, PAD_RIGHT)
                        .addButton(152, 48, 14, 14, APAD_RIGHT)
                        .addButton(137, 63, 14, 14, PAD_RIGHT)
                        .addButton(152, 63, 14, 14, APAD_RIGHT);
            });
        }
    }

    public static void postInit() {
        AntimatterMaterialTypes.postInit();
    }
}
