package muramasa.antimatter.dynamic;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ModelConfig {

    protected int[] config = BlockDynamic.DEFAULT_CONFIG;
    protected int modelIndex = -1;

    public ModelConfig() {

    }

    public ModelConfig set(int[] config) {
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

    public List<BakedQuad> getQuads(List<BakedQuad> quads, Int2ObjectOpenHashMap<IBakedModel[]> bakedConfigs, BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        IBakedModel[] baked;
        if (side == null) {
            for (int i = 0; i < config.length; i++) {
                baked = bakedConfigs.get(config[i]);
                if (baked != null) {
                    addBaked(quads, baked, state, null, rand, data);
                    if (i == 0) setModelIndex(config[i]);
                }
            }
        } else {
            baked = bakedConfigs.get(config[side.getIndex()]);
            if (baked != null) {
                addBaked(quads, baked, state, null, rand, data);
            }
        }
        return quads;
    }

    public void addBaked(List<BakedQuad> quads, IBakedModel[] baked, BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        for (int j = 0; j < baked.length; j++) {
            quads.addAll(baked[j].getQuads(state, side, rand, data));
        }
    }

    public boolean isInvalid() {
        return config == null || config.length == 0 || config[0] == -1;
    }
}
