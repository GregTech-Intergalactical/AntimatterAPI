package muramasa.antimatter.registration;

import net.minecraft.item.ItemStack;

public interface IAntimatterObject {

    String getId();

    default ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }
}
