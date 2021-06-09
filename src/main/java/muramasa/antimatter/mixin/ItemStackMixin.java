package muramasa.antimatter.mixin;

import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> {
    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @Inject(method = "damageItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 1))
    public<T extends LivingEntity> void inject(int amount, T entity, Consumer<T> consumer, CallbackInfo ci){
        ItemStack invoker = ((ItemStack)(Object)this);
        if (invoker.getItem() instanceof IAntimatterTool){
            if (entity instanceof PlayerEntity) {
                ((IAntimatterTool)invoker.getItem()).onItemBreak(invoker, (PlayerEntity) entity);
            }
        }
    }
}
