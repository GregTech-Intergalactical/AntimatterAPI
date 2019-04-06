package muramasa.gtu.api.gui.slot;

import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.materials.Prefix;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

//TODO use
public class SlotCell extends SlotItemHandler {

    public SlotCell(IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return ItemType.EmptyCell.isEqual(stack) || MaterialItem.hasPrefix(stack, Prefix.Cell);
    }
}
