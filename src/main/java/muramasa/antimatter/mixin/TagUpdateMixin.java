package muramasa.antimatter.mixin;

import muramasa.antimatter.recipe.loader.AntimatterRecipeLoader;
import net.minecraft.resources.DataPackRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackRegistries.class)
public class TagUpdateMixin {
    @Inject(/*remap = false,*/method = "Lnet/minecraft/resources/DataPackRegistries;updateTags()V", at=@At("RETURN"))
    private void updateTagsMixin(CallbackInfo info) {
        DataPackRegistries reg = (DataPackRegistries) (Object) this;
        AntimatterRecipeLoader.postTagReload(reg);
    }
}
