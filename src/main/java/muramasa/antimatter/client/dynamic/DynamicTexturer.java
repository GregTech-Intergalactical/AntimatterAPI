package muramasa.antimatter.client.dynamic;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;

public class DynamicTexturer<T extends IDynamicModelProvider, U> {
    private List<BakedQuad>[] cache;
    private U previousKey;
    private final DynamicTextureProvider<T, U> provider;

    public DynamicTexturer(DynamicTextureProvider<T, U> provider) {
        this.provider = provider;
    }

    public List<BakedQuad> getQuads(String type, List<BakedQuad> currentList, BlockState state, T t, U key, int dir, IModelData data) {
        if (key.equals(previousKey)) {
            currentList.addAll(cache[dir]);
            return currentList;
        }
        List<BakedQuad>[] quads = this.provider.getQuads(type, state, t, key, data);
        this.previousKey = key;
        this.cache = quads;
        currentList.addAll(quads[dir]);

        return currentList;
    }

    public void invalidate() {
        this.previousKey = null;
        this.cache = null;
    }

}