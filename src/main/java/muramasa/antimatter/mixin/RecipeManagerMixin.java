package muramasa.antimatter.mixin;

import com.google.gson.JsonElement;
import muramasa.antimatter.AntimatterAPI;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * KubeJS has priority 1100, let's set it higher to ensure recipes are loaded.
 */
@Mixin(value = RecipeManager.class, priority = 2000)
public class RecipeManagerMixin {
    /* Inject recipes into recipe map. */
    @Inject(/*remap = false,*/method = "apply(Ljava/util/Map;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;)V", at = @At("HEAD"))
    private void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo info) {
        AntimatterAPI.onRecipeManagerBuild(objectIn);
    }
}
