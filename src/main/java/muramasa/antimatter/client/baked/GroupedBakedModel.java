package muramasa.antimatter.client.baked;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GroupedBakedModel extends AntimatterBakedModel<GroupedBakedModel> {

    protected final Map<String, IBakedModel> models;
    protected BakedQuad[][] CACHE = new BakedQuad[7][];
    protected BakedQuad[][] CACHE_ITEM = new BakedQuad[7][];

    public GroupedBakedModel(TextureAtlasSprite p, Map<String, IBakedModel> models) {
        super(p);
        this.models = models;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        int offset = side == null ? 6 : side.get3DDataValue();
        BakedQuad[] arr = CACHE[offset];
        if (arr == null) {
            CACHE[offset] = models.values().stream().flatMap(t -> t.getQuads(state, side, rand, data).stream()).toArray(BakedQuad[]::new);
            arr = CACHE[offset];
        }
        return Arrays.asList(arr);
    }

    @Override
    public List<BakedQuad> getItemQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        int offset = side == null ? 6 : side.get3DDataValue();
        BakedQuad[] arr = CACHE_ITEM[offset];
        if (arr == null) {
            CACHE_ITEM[offset] = models.values().stream().flatMap(t -> t.getQuads(null, side, rand).stream()).toArray(BakedQuad[]::new);
            arr = CACHE_ITEM[offset];
        }
        return Arrays.asList(arr);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
