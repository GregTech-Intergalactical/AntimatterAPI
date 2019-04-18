package muramasa.gtu.api.interfaces;

import net.minecraft.item.ItemStack;

public interface IGregTechObject {

    String getName();

    default ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }
}
