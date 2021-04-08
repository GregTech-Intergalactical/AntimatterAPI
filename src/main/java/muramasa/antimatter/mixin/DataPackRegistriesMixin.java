package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterAPI;
import net.minecraft.item.Item;
import net.minecraft.resources.DataPackRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackRegistries.class)
public class DataPackRegistriesMixin {

    @Inject(at = @At("RETURN"), method="Lnet/minecraft/resources/DataPackRegistries;updateTags()V")
    private void onUpdateTags(CallbackInfo info) {
        AntimatterAPI.onRecipeCompile(((DataPackRegistries)(Object)this).getRecipeManager(), Item::getTags);
    }
}
