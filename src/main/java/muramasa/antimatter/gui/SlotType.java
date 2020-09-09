package muramasa.antimatter.gui;

import muramasa.antimatter.gui.slot.SlotCell;
import muramasa.antimatter.gui.slot.SlotEnergy;
import muramasa.antimatter.gui.slot.SlotInput;
import muramasa.antimatter.gui.slot.SlotOutput;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class SlotType {

    public static SlotType IT_IN = new SlotType("item_in", (t, i, d) -> Optional.of(new SlotInput(t.itemHandler.get().getInputWrapper(), i, d.getX(), d.getY())));
    public static SlotType IT_OUT = new SlotType("item_out", (t, i, d) -> Optional.of(new SlotOutput(t.itemHandler.get().getOutputWrapper(), i, d.getX(), d.getY())));
    public static SlotType FL_IN = new SlotType("fluid_in", (t, i, d) -> Optional.empty());
    public static SlotType FL_OUT = new SlotType("fluid_out", (t, i, d) -> Optional.empty());
    public static SlotType CELL_IN = new SlotType("cell_in", (t, i, d) -> Optional.of(new SlotCell(t.itemHandler.get().getCellWrapper(), i, d.getX(), d.getY())));
    public static SlotType CELL_OUT = new SlotType("cell_out", (t, i, d) -> Optional.of(new SlotOutput(t.itemHandler.get().getOutputWrapper(), i, d.getX(), d.getY())));
    public static SlotType ENERGY = new SlotType("energy", (t, i, d) -> Optional.of(new SlotEnergy(t.itemHandler.get().getChargeWrapper(), i, d.getX(), d.getY())));

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
