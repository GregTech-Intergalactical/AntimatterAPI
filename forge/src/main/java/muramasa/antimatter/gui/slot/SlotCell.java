package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import net.minecraftforge.items.IItemHandler;

public class SlotCell extends AbstractSlot<SlotCell> {

    public SlotCell(SlotType<SlotCell> type, IGuiHandler tile, IItemHandler stackHandler, int index, int x, int y) {
        super(type, tile, stackHandler, index, x, y);
    }

}
