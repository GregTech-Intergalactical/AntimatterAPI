package muramasa.itech.api.materials;

import net.minecraft.item.ItemStack;

public class GTItemStack {

    private ItemStack stack;
    private boolean showInCreative;

    public GTItemStack(ItemStack stack, boolean showInCreative) {
        this.stack = stack;
        this.showInCreative = showInCreative;
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean doesShowInCreative() {
        return showInCreative;
    }
}
