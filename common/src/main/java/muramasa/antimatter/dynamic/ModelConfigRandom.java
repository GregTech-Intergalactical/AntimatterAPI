package muramasa.antimatter.dynamic;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.ModelUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ModelConfigRandom extends ModelConfig {

    @Override
    public ModelConfig set(BlockPos pos, int[] config) {
        super.set(pos, config);
        this.modelIndex = new Random(pos.asLong()).nextInt(config.length);
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(List<BakedQuad> quads, Int2ObjectOpenHashMap<BakedModel[]> bakedConfigs, BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos) {
        //setModelIndex(config[rand.nextInt(config.length)]);
        BakedModel[] baked = bakedConfigs.get(getModelIndex());
        if (baked != null) {
            for (int i = 0; i < baked.length; i++) {
                quads.addAll(ModelUtils.getQuadsFromBaked(baked[i], state, side, rand, level, pos));
            }
        }
        return quads;
    }
}
