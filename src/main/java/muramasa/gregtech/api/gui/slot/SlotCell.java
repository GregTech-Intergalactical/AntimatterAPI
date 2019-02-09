package muramasa.gregtech.api.gui.slot;

import muramasa.gregtech.api.enums.ItemList;
import muramasa.gregtech.api.items.MetaItem;
import muramasa.gregtech.api.materials.Prefix;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotCell extends SlotItemHandler {

    public SlotCell(IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return ItemList.Empty_Cell.isItemEqual(stack) || MetaItem.hasPrefix(stack, Prefix.CELL);
    }
}
