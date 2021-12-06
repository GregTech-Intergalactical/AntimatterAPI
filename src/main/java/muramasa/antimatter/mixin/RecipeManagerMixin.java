package muramasa.antimatter.mixin;

import com.google.gson.JsonElement;
import muramasa.antimatter.AntimatterDynamics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(value = RecipeManager.class)
public class RecipeManagerMixin {
    /* Inject recipes into recipe map. */
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;)V", at = @At("HEAD"))
    private void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn, CallbackInfo info) {
        AntimatterDynamics.onRecipeManagerBuild(rec -> objectIn.put(rec.getId(), rec.serializeRecipe()));
    }
}