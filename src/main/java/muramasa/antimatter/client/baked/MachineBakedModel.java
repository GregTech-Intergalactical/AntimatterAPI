package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MachineBakedModel extends RotatableCoveredBakedModel {

    public MachineBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        BlockMachine bm = (BlockMachine) state.getBlock();
        List<BakedQuad> retValue = new ArrayList<>();
        if (data.hasProperty(AntimatterProperties.MACHINE_TEXTURE)) {
            Texture tex = data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE);
            if (tex != null) {
                TileEntityMachine t = data.getData(AntimatterProperties.MACHINE_TILE);
                MachineCoverHandler<TileEntityMachine> covers = t.coverHandler.orElse(null);
                for (int i = 0; i < Ref.DIRS.length; i++) {
                    Direction d = Ref.DIRS[i];
                    CoverInstance<TileEntityMachine> c = covers == null ? null : covers.get(Ref.DIRS[i]);
                    if (c == null || c.skipRender()) {
                        TileEntityMachine.DynamicKey key = new TileEntityMachine.DynamicKey(new ResourceLocation(bm.getType().getId()), tex, state.get(BlockStateProperties.HORIZONTAL_FACING), data.getData(AntimatterProperties.MACHINE_STATE));

                        List<BakedQuad> quads = t.multiTexturer.getQuads(state, t, key, i, data);
                        assert quads.size() == 0 || quads.get(0).getFace() == Ref.DIRS[i];
                        retValue.addAll(t.multiTexturer.getQuads(state, t, key, i, data));
                    }
                }
                if (covers != null) retValue = attachCoverQuads(retValue, state, side, data);
                return retValue;
            }
        }
        return super.getBlockQuads(state, side, rand, data);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        return super.getModelData(world, pos, state, data);
    }
}
