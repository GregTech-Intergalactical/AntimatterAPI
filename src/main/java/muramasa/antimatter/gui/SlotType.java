package muramasa.antimatter.gui;

import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.slot.*;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class SlotType {

    public static SlotType IT_IN = new SlotType("item_in", (t, i, d) -> Optional.of(new SlotInput(t,t.itemHandler.map(MachineItemHandler::getInputHandler).orElse(null), i, d.getX(), d.getY())));
    public static SlotType IT_OUT = new SlotType("item_out", (t, i, d) -> Optional.of(new SlotOutput(t,t.itemHandler.map(MachineItemHandler::getOutputHandler).orElse(null), i, d.getX(), d.getY())));
    public static SlotType FL_IN = new SlotType("fluid_in", (t, i, d) -> Optional.of(new SlotFakeFluid(t, MachineFluidHandler.FluidDirection.INPUT,i, d.getX(), d.getY())));
    //Cheat using same ID to get working counter.
    public static SlotType FL_OUT = new SlotType("fluid_in", (t, i, d) -> Optional.of(new SlotFakeFluid(t,MachineFluidHandler.FluidDirection.OUTPUT, i, d.getX(), d.getY())));
    public static SlotType CELL_IN = new SlotType("cell_in", (t, i, d) -> Optional.of(new SlotCell(t.itemHandler.map(MachineItemHandler::getCellInputHandler).orElse(null), i, d.getX(), d.getY())));
    public static SlotType CELL_OUT = new SlotType("cell_out", (t, i, d) -> Optional.of(new SlotCell(t.itemHandler.map(MachineItemHandler::getCellOutputHandler).orElse(null), i, d.getX(), d.getY())));
    public static SlotType ENERGY = new SlotType("energy", (t, i, d) -> Optional.of(new SlotEnergy(t.itemHandler.map(MachineItemHandler::getChargeHandler).orElse(null), i, d.getX(), d.getY())));

    protected String id;
    protected ISlotSupplier slotSupplier;

    public SlotType(String id, ISlotSupplier slotSupplier) {
        this.id = id;
        this.slotSupplier = slotSupplier;
    }

    public String getId() {
        return id;
    }

    public ISlotSupplier getSlotSupplier() {
        return slotSupplier;
    }

    public interface ISlotSupplier {

        Optional<SlotItemHandler> get(TileEntityMachine tile, int index, SlotData data);
    }
}
