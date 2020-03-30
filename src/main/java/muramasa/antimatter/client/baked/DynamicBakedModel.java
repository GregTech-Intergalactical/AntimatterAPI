package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.block.BlockDynamic;
import muramasa.antimatter.client.ModelConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DynamicBakedModel extends AntimatterBakedModel<DynamicBakedModel> {

    private IBakedModel bakedDefault;
    private Int2ObjectOpenHashMap<IBakedModel[]> bakedConfigs;
    private boolean hasConfig;
    private BlockPos.Mutable mutablePos = new BlockPos.Mutable();
    private IModelData configData = new ModelDataMap.Builder().withInitial(AntimatterProperties.DYNAMIC_CONFIG, new ModelConfig()).build();

    public DynamicBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple.getA());
        this.bakedDefault = bakedTuple.getA();
        this.bakedConfigs = bakedTuple.getB();
        this.hasConfig = bakedConfigs.size() > 0;
    }

    public IBakedModel getBakedDefault() {
        return bakedDefault;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        if (!hasConfig || !(state.getBlock() instanceof BlockDynamic)) return data;
        mutablePos.setPos(pos);
        configData.setData(AntimatterProperties.DYNAMIC_CONFIG, ((BlockDynamic) state.getBlock()).getConfig(state, world, mutablePos, pos));
        return configData;
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (!hasConfig) return bakedDefault.getQuads(state, side, rand, data);
        List<BakedQuad> quads = new LinkedList<>();
        ModelConfig config = data.getData(AntimatterProperties.DYNAMIC_CONFIG);
        if (config == null || config.isInvalid()) return bakedDefault.getQuads(state, side, rand, data);
        return config.getQuads(quads, bakedConfigs, state, side, rand, data);
    }

    @Override
    public List<BakedQuad> getItemQuads(@Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return bakedDefault.getQuads(null, side, rand, data);
    }
}
