package muramasa.antimatter.mixin;

import com.google.gson.JsonElement;
import muramasa.antimatter.AntimatterDynamics;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(value = RecipeManager.class)
public class RecipeManagerMixin {
    /* Inject recipes into recipe map. */
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;)V", at = @At("HEAD"))
    private void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo info) {
        AntimatterDynamics.onRecipeManagerBuild(rec -> objectIn.put(rec.getId(), rec.serializeRecipe()));
    }
}