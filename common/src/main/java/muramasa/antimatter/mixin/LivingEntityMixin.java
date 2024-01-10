package muramasa.antimatter.mixin;

import muramasa.antimatter.entity.IRadiationEntity;
import muramasa.antimatter.util.CodeUtils;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IRadiationEntity {
    @Shadow
    public abstract boolean addEffect(MobEffectInstance mobEffectInstance);

    @Unique
    private byte radiation = 0;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void changeRadiation(int radiation) {
        this.radiation = CodeUtils.bind7(this.radiation + radiation);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInject(CallbackInfo callbackInfo){
        if (getLevel() != null && getLevel().getGameTime() % 50 == 0) {
            MobEffect ic2Radiation = Registry.MOB_EFFECT.get(new ResourceLocation("ic2", "radiation"));
            if (radiation >= 100) {
                this.addEffect(new MobEffectInstance(ic2Radiation != null ? ic2Radiation : MobEffects.WITHER, 100));
                this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 2));
                this.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 2));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                this.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 2));
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
            } else if (radiation >= 75) {
                this.addEffect(new MobEffectInstance(ic2Radiation != null ? ic2Radiation : MobEffects.POISON, 100));
                this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            } else if (radiation >= 50) {
                this.addEffect(new MobEffectInstance(ic2Radiation != null ? ic2Radiation : MobEffects.POISON, 100));
                this.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100));
                this.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));
                this.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100));
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100));
            } else if (radiation >= 25) {
                this.addEffect(new MobEffectInstance(ic2Radiation != null ? ic2Radiation : MobEffects.POISON, 100));
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void injectSave(CompoundTag nbt, CallbackInfo info){
        nbt.putByte("antimatter.radiation", radiation);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void injectRead(CompoundTag nbt, CallbackInfo info){
        radiation = nbt.getByte("antimatter.radiation");
    }
}
