package muramasa.antimatter.gui;

import muramasa.antimatter.gui.slot.SlotInput;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

public class SlotType {

    public static SlotType IT_IN = new SlotType("item_in", (t, i, d) -> Optional.of(new SlotInput(t.itemHandler.get().getInputWrapper(), i, d.x, d.y)));
    public static SlotType IT_OUT = new SlotType("item_out", (t, i, d) -> Optional.of(new SlotInput(t.itemHandler.get().getOutputWrapper(), i, d.x, d.y)));
    public static SlotType FL_IN = new SlotType("fluid_in", (t, i, d) -> Optional.empty());
    public static SlotType FL_OUT = new SlotType("fluid_out", (t, i, d) -> Optional.empty());
    public static SlotType CELL_IN = new SlotType("cell_in", (t, i, d) -> Optional.of(new SlotInput(t.itemHandler.get().getCellWrapper(), i, d.x, d.y)));
    public static SlotType CELL_OUT = new SlotType("cell_out", (t, i, d) -> Optional.of(new SlotInput(t.itemHandler.get().getCellWrapper(), i, d.x, d.y)));
    public static SlotType ENERGY = new SlotType("energy", (t, i, d) -> Optional.of(new SlotInput(t.itemHandler.get().getChargeWrapper(), i, d.x, d.y)));

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
