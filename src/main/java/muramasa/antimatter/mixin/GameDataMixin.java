package muramasa.antimatter.mixin;

import muramasa.antimatter.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

// Because the potentially dangerous alternative prefix warning is stupid and annoying
@Mixin(GameData.class)
public class GameDataMixin {

    @Inject(remap = false, method = "checkPrefix", at = @At(value = "HEAD"), cancellable = true)
    private static void injectCheckPrefix(String name, boolean warnOverrides, CallbackInfoReturnable<ResourceLocation> info) {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
        String name2 = index == -1 ? name : name.substring(index + 1);
        String prefix = ModLoadingContext.get().getActiveNamespace();
        if (prefix.equals(Ref.ID)) {
            info.setReturnValue(new ResourceLocation(oldPrefix, name2));
        }
    }
}
