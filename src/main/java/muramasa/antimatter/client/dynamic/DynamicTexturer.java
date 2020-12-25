package muramasa.antimatter.client.dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;

//TODO: Clean this up should it not require the cache anymore.
public class DynamicTexturer<T extends IDynamicModelProvider,U> {
    private List<BakedQuad>[] cache;
    private U previousKey;
    private final DynamicTextureProvider<T,U> provider;

    public DynamicTexturer(DynamicTextureProvider<T,U> provider) {
        this.provider = provider;
    }

    public List<BakedQuad> getQuads(BlockState state, T t, U key, int dir, IModelData data) {
        if (key.equals(previousKey)) return cache[dir];
        List<BakedQuad>[] quads = this.provider.getQuads(state,t,key,data);
        this.previousKey = key;
        this.cache = quads;
        return quads[dir];
    }

}