package muramasa.gtu.client.render.models;

import muramasa.antimatter.client.model.ModelBase;
import muramasa.gtu.client.render.bakedmodels.BakedNichrome;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ModelNichrome extends ModelBase {

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        return new BakedNichrome(
            load(mod("block/preset/simple"))
                .tex("all", mc("block/bedrock"))
                .tex("up", mc("block/diamond_block"))
                .bake(bakery, getter, sprite, format)
        );
    }
}
