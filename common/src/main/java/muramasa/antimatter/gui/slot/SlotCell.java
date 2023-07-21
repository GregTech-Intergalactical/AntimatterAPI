package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import tesseract.api.item.ExtendedItemContainer;

public class SlotCell extends AbstractSlot<SlotCell> {

    public SlotCell(SlotType<SlotCell> type, IGuiHandler tile, ExtendedItemContainer stackHandler, int index, int x, int y) {
        super(type, tile, stackHandler, index, x, y);
    }

}
