package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.Ref;
import muramasa.antimatter.item.ItemBattery;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemBattery.class)
public class ItemBatteryMixin extends Item {
    public ItemBatteryMixin(Properties arg) {
        super(arg);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (stack.getTag() != null){
            long energy = stack.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA).getLong(Ref.KEY_ITEM_ENERGY);
            if (energy > 0) return 1;
        }
        return super.getItemStackLimit(stack);
    }
}
