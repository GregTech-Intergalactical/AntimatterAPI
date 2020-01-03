package muramasa.antimatter.client.baked;

import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.blocks.BlockDynamic;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BakedDynamic extends BakedBase {

    private BlockDynamic block;

    public BakedDynamic(BlockDynamic block) {
        super(block.getData().getBase(0));
        this.block = block;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        if (((BlockDynamic) state.getBlock()).getLookup().size() == 0) {
            data.setData(AntimatterProperties.DYNAMIC_CONFIG, new int[1]);
        } else {
            data.setData(AntimatterProperties.DYNAMIC_CONFIG, ((BlockDynamic) state.getBlock()).getConfig(state, world, new BlockPos.MutableBlockPos(pos), pos));
        }
        return data;
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (state == null) return Collections.emptyList();
        List<BakedQuad> quads = block.addDefaultModel() ? new LinkedList<>(block.getBaked().getQuads(state, side, rand)) : new LinkedList<>();
        if (data.hasProperty(AntimatterProperties.DYNAMIC_CONFIG)) {
            int[] ct = data.getData(AntimatterProperties.DYNAMIC_CONFIG);
            IBakedModel baked;
            for (int i = 0; i < ct.length; i++) {
                if (ct[i] == 0) continue;
                baked = block.getLookup().get(ct[i]);
                if (baked != null) quads.addAll(baked.getQuads(state, side, rand));
            }
            if (quads.size() > 0) return quads;
        }
        quads.addAll(block.getBaked().getQuads(state, side, rand));
        return quads;
    }
}
