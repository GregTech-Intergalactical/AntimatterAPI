package muramasa.gtu.api.registration;

import net.minecraft.item.ItemStack;

public interface IGregTechObject {

    String getId();

    default ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }
}
