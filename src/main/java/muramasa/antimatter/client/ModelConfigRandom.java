package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ModelConfigRandom extends ModelConfig {

    @Override
    public List<BakedQuad> getQuads(List<BakedQuad> quads, Int2ObjectOpenHashMap<IBakedModel> bakedConfigs, BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        setModelIndex(config[rand.nextInt(config.length)]);
        quads.addAll(bakedConfigs.get(getModelIndex()).getQuads(state, side, rand, data));
        return quads;
    }
}
