package net.minecraftforge.mixin.client;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleBakedModel.Builder.class)
public interface SimpleBakedModelBuilderAccessor {
    @Invoker("<init>")
    static SimpleBakedModel.Builder invokeInit(boolean bl, boolean bl2, boolean bl3, ItemTransforms itemTransforms, ItemOverrides itemOverrides){
        throw new AssertionError();
    }
}
