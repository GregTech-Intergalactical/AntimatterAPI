package muramasa.antimatter.mixin;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<ItemStack> {
    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @Shadow
    public abstract boolean attemptDamageItem(int amount, Random rand, @Nullable ServerPlayerEntity damager);

    /*@Redirect(method = "damageItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;attemptDamageItem(ILjava/util/Random;Lnet/minecraft/entity/player/ServerPlayerEntity;)B"))
    public<T extends LivingEntity> boolean applyRedirect(ItemStack invoker, int amount, Random rand, @Nullable ServerPlayerEntity damager, int amount2, Consumer<T> consumer, T entity){
        boolean broke = attemptDamageItem(amount, rand, damager);
        if (broke && invoker.getItem() instanceof IAntimatterTool){
            if (entity instanceof PlayerEntity) {
                ((IAntimatterTool)invoker.getItem()).onItemBreak(invoker, (PlayerEntity) entity);
            }
        }
        return broke;
    }*/

    @Inject(method = "damageItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;attemptDamageItem(ILjava/util/Random;Lnet/minecraft/entity/player/ServerPlayerEntity;)B", shift = At.Shift.AFTER))
    public<T extends LivingEntity> void inject(CallbackInfo ci, int amount, Consumer<T> consumer, T entity){
        ItemStack invoker = ((ItemStack)(Object)this);
        if (invoker.getItem() instanceof IAntimatterTool){
            if (entity instanceof PlayerEntity) {
                ((IAntimatterTool)invoker.getItem()).onItemBreak(invoker, (PlayerEntity) entity);
            }
        }
    }
}
