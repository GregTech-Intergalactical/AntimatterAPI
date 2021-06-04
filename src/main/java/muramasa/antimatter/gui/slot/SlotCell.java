package muramasa.antimatter.gui.slot;

import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraftforge.items.IItemHandler;

public class SlotCell extends AbstractSlot<SlotCell> {

    public SlotCell(SlotType<SlotCell> type, TileEntityMachine<?> tile, IItemHandler stackHandler, int index, int x, int y) {
        super(type, tile, stackHandler, index, x, y);
    }

}
