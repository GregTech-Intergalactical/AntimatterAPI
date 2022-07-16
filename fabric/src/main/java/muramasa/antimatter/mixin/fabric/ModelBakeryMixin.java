package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.client.fabric.ModelUtilsImpl;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectStaticModelBakery(ResourceManager resourceManager, BlockColors blockColors, ProfilerFiller profilerFiller, int i, CallbackInfo ci){
        ModelUtilsImpl.BAKERY = (ModelBakery)(Object)this;
    }
}
