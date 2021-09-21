package muramasa.antimatter.mixin;

import muramasa.antimatter.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// Because the potentially dangerous alternative prefix warning is stupid and annoying
@Mixin(GameData.class)
public class GameDataMixin {


    //@Inject(remap = false, method = "checkPrefix", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/ModLoadingContext;getActiveNamespace()Ljava/lang/String;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void injectCheckPrefix(String name, boolean warnOverrides, CallbackInfoReturnable<ResourceLocation> info, int index, String oldPrefix, String prefix){
        if (prefix.equals(Ref.ID)){
            info.setReturnValue(new ResourceLocation(oldPrefix, name));
        }
    }
}
