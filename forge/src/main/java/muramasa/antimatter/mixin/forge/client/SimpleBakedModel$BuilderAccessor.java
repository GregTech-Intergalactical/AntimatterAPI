package muramasa.antimatter.mixin.forge.client;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleBakedModel.Builder.class)
public interface SimpleBakedModel$BuilderAccessor {
    @Invoker("<init>")
    static SimpleBakedModel.Builder antimatter$create(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides) {
        throw new RuntimeException("mixin failed!");
    }
}
