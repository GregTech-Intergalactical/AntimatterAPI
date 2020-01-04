package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.blocks.BlockDynamic;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BakedDynamic extends BakedBase {

    //private static IModelData DEFAULT_DATA = new ModelDataMap.Builder().withInitial(AntimatterProperties.DYNAMIC_CONFIG, new int[0]).build();

    private BlockDynamic block;
    private Int2ObjectOpenHashMap<IBakedModel> bakedLookup;
    private IBakedModel defaultModel;
    private boolean hasConfig;

    public BakedDynamic(BlockDynamic block, Int2ObjectOpenHashMap<IBakedModel> bakedLookup, IBakedModel defaultModel) {
        this.block = block;
        this.bakedLookup = bakedLookup;
        this.defaultModel = defaultModel;
        this.hasConfig = block.getConfigLookup().size() > 0;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        if (!hasConfig) return data;

        //TODO optimize pointless mutable creation
        return new ModelDataMap.Builder().withInitial(AntimatterProperties.DYNAMIC_CONFIG, block.getConfig(state, world, new BlockPos.MutableBlockPos(pos), pos)).build();
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (!hasConfig) return defaultModel.getQuads(state, side, rand, data);
        List<BakedQuad> quads = new LinkedList<>();
        int[] ct = data.getData(AntimatterProperties.DYNAMIC_CONFIG);
        if (ct == null) return quads;
        IBakedModel baked;
        for (int i = 0; i < ct.length; i++) {
            if (ct[i] == 0) continue;
            baked = bakedLookup.get(ct[i]);
            if (baked != null) quads.addAll(baked.getQuads(state, side, rand, data));
        }
        return quads;
    }
}
