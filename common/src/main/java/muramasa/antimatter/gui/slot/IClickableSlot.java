package muramasa.antimatter.gui.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public interface IClickableSlot {
    ItemStack clickSlot(int clickedButton, ClickType clickType, Player playerEntity, AbstractContainerMenu container);
}
