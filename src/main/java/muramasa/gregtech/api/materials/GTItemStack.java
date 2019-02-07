package muramasa.gregtech.api.materials;

import net.minecraft.item.ItemStack;

public class GTItemStack {

    private ItemStack stack;
    private boolean visible;

    public GTItemStack(ItemStack stack, boolean visible) {
        this.stack = stack;
        this.visible = visible;
    }

    public ItemStack get() {
        return stack;
    }

    public boolean isVisible() {
        return visible;
    }
}
