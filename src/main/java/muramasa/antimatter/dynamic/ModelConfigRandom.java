package muramasa.antimatter.dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import speiger.src.collections.ints.maps.impl.hash.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ModelConfigRandom extends ModelConfig {

    @Override
    public List<BakedQuad> getQuads(List<BakedQuad> quads, Int2ObjectOpenHashMap<IBakedModel[]> bakedConfigs, BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        setModelIndex(config[rand.nextInt(config.length)]);
        IBakedModel[] baked = bakedConfigs.get(getModelIndex());
        if (baked != null) {
            for (int i = 0; i < baked.length; i++) {
                quads.addAll(baked[i].getQuads(state, side, rand, data));
            }
        }
        return quads;
    }
}
