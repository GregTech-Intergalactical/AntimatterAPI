package muramasa.antimatter.client.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.blocks.BlockDynamic;
import muramasa.antimatter.client.baked.BakedDynamic;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class ModelDynamic extends ModelBase {

    private BlockDynamic block;

    public ModelDynamic(BlockDynamic block) {
        this.block = block;
        this.block.onConfigBuild();
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        Int2ObjectOpenHashMap<IBakedModel> bakedLookup = new Int2ObjectOpenHashMap<>();
        block.getConfigLookup().forEach((i, t) -> {
            ModelContainer model = load(mod("block/preset/simple"));
            for (int j = 0; j < t.length; j++) {
                model.tex(Ref.DIRECTIONS[j].getName(), t[j]);
            }
            bakedLookup.put((int) i, model.bake(bakery, getter, sprite, format));
        });
        return new BakedDynamic(block, bakedLookup, load(block.getRegistryName()).bake(bakery, getter, sprite, format));
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return Arrays.asList(block.getConfigTextures());
    }
}
