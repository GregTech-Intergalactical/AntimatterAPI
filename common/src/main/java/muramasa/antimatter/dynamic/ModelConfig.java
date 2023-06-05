package muramasa.antimatter.dynamic;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.baked.IAntimatterBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ModelConfig {

    protected int[] config = BlockDynamic.DEFAULT_CONFIG;
    protected int modelIndex = -1;

    public ModelConfig() {

    }

    public ModelConfig set(BlockPos pos, int[] config) {
        this.config = config;
        return this;
    }

    public int[] getConfig() {
        return config;
    }

    public boolean hasModelIndex() {
        return modelIndex != -1;
    }

    public void setModelIndex(int index) {
        modelIndex = index;
    }

    public int getModelIndex() {
        return modelIndex;
    }

    public List<BakedQuad> getQuads(List<BakedQuad> quads, Int2ObjectOpenHashMap<BakedModel[]> bakedConfigs, BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos) {
        BakedModel[] baked;
        if (side == null) {
            for (int i = 0; i < config.length; i++) {
                baked = bakedConfigs.get(config[i]);
                if (baked != null) {
                    addBaked(quads, baked, state, null, rand, level, pos);
                    if (i == 0) setModelIndex(config[i]);
                }
            }
        } else {
            if (config.length < 6) {
                for (int i = 0; i < config.length; i++) {
                    baked = bakedConfigs.get(config[i]);
                    if (baked != null) addBaked(quads, baked, state, side, rand, level, pos);
                }
            } else {
                //TODO: This might have to be fixed. Machine baking creates general quads using the model config as direction,
                //TODO: but since it is a general quad side has to be null! But e.g. casings have face quads so need a relevant side.
                //For now, assume that a 6 side config is machine and so use general quads.
                baked = bakedConfigs.get(config[side.get3DDataValue()]);
                if (baked != null) addBaked(quads, baked, state, null, rand, level, pos);
            }
        }
        return quads;
    }

    public void addBaked(List<BakedQuad> quads, BakedModel[] baked, BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos) {
        for (int j = 0; j < baked.length; j++) {
            quads.addAll(ModelUtils.getQuadsFromBaked(baked[j], state, side, rand, level, pos));
        }
    }

    public boolean isInvalid() {
        return config == null || config.length == 0 || config[0] == -1;
    }
}
