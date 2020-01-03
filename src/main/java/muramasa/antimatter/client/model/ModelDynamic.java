package muramasa.antimatter.client.model;

import muramasa.antimatter.blocks.BlockDynamic;
import muramasa.antimatter.client.baked.BakedDynamic;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.vertex.VertexFormat;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ModelDynamic extends ModelBase {

    private BlockDynamic block;

    public ModelDynamic(BlockDynamic block) {
        this.block = block;
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function spriteGetter, ISprite sprite, VertexFormat format) {
        return new BakedDynamic(block);
    }
}
