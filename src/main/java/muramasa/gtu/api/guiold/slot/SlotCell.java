package muramasa.gtu.api.guiold.slot;

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
        //TODO return ItemType.EmptyCell.isEqual(stack) || MaterialItem.hasType(stack, Prefix.Cell);
        return false;
    }
}
