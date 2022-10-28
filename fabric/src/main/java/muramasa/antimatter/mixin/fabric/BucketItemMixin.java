package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.item.IContainerItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BucketItem.class)
public class BucketItemMixin implements IContainerItem {
    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return stack.getItem() != Items.BUCKET && !(stack.getItem() instanceof MobBucketItem);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(Items.BUCKET);
    }
}
