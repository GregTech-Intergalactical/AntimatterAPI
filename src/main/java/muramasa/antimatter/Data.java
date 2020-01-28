package muramasa.antimatter;

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
import muramasa.antimatter.machines.types.Machine;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.structure.StructureBuilder;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.tileentities.TileEntityRecipeMachine;
import muramasa.antimatter.tileentities.multi.TileEntityHatch;
import muramasa.antimatter.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

import static muramasa.antimatter.materials.TextureSet.DULL;

public class Data {

    static {
        StructureBuilder.addGlobalElement("A", StructureElement.AIR);
        StructureBuilder.addGlobalElement("X", StructureElement.X);
    }

    public static Material NULL = new Material("null", 0xffffff, DULL);

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

    }
}
