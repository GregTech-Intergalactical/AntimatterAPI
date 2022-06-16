package net.minecraftforge.mixin.client;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BakedModel.class)
public interface MixinBakedModel extends IForgeBakedModel {
    /**
     * @author Trinsdar
     * @reason Because forge changes it to be default, which means some forge mods may not implement this.
     */
    //todo figure this out
    @Overwrite
    default ItemTransforms getTransforms(){
        return ItemTransforms.NO_TRANSFORMS;
    }
}
