package muramasa.antimatter.gui.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

public interface IClickableSlot {
    ItemStack clickSlot(int clickedButton, ClickType clickType, PlayerEntity playerEntity, Container container);
}
