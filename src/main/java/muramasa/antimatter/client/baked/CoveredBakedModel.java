package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoveredBakedModel extends AttachableBakedModel {

    public CoveredBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
       // TileEntity tile = world.getTileEntity(pos);
        //if (tile == null) return super.getModelData(world, pos, state, data);
      //  data.setData(AntimatterProperties.MACHINE_TILE,(TileEntityMachine)tile);
        return super.getModelData(world, pos, state, data);
    }

    @Override
    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return attachCoverQuads(new ArrayList<>(), state, side, data);
    }

    protected final List<BakedQuad> attachCoverQuads(List<BakedQuad> quads, BlockState state, Direction side, @Nonnull IModelData data) {
        if (!data.hasProperty(AntimatterProperties.MACHINE_TILE)) return quads;
        MachineCoverHandler<TileEntityMachine> covers = data.getData(AntimatterProperties.MACHINE_TILE).coverHandler.orElse(null);
        if (covers == null) return quads;
        Texture tex = data.hasProperty(AntimatterProperties.MULTI_MACHINE_TEXTURE) ? data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE).apply(side) : data.getData(AntimatterProperties.MACHINE_TEXTURE).apply(side);
        CoverStack<?> c = covers.get(side);
        if (c.isEmpty()) return quads;
        quads = covers.getTexturer(side).getQuads(quads,state,c.getCover(),new BaseCover.DynamicKey(state.get(BlockStateProperties.HORIZONTAL_FACING), tex, c.getCover().getId()), side.getIndex(), data);
        return quads;
    }
}
