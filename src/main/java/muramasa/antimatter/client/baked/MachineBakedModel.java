package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MachineBakedModel extends CoveredBakedModel {

    public MachineBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    public List<BakedQuad> attachMultiQuads(List<BakedQuad> quads,BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        BlockMachine bm = (BlockMachine) state.getBlock();
        if (data.hasProperty(AntimatterProperties.MACHINE_TEXTURE)) {
            Texture tex = data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE);
            if (tex != null) {
                TileEntityMachine t = data.getData(AntimatterProperties.MACHINE_TILE);
                MachineCoverHandler<TileEntityMachine> covers = t.coverHandler.orElse(null);
                CoverStack<TileEntityMachine> c = covers == null ? null : covers.get(side);
                if (c == null || c.skipRender()) {
                    TileEntityMachine.DynamicKey key = new TileEntityMachine.DynamicKey(new ResourceLocation(bm.getType().getId()), tex, state.get(BlockStateProperties.HORIZONTAL_FACING), data.getData(AntimatterProperties.MACHINE_STATE));
                    quads = t.multiTexturer.getQuads(quads, state, t, key, side.getIndex(), data);
                    assert quads.size() == 0 || quads.get(0).getFace() == side;
                }
            }
        }
        return quads;
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        data = super.getModelData(world, pos, state, data);
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityMachine)) return data;
        TileEntityMachine machine = (TileEntityMachine) tile;

        BlockPos cPos = StructureCache.get(machine.getWorld(), pos);
        if (cPos == null) return data;
        TileEntityMultiMachine mTile = (TileEntityMultiMachine) world.getTileEntity(cPos);

        data.setData(AntimatterProperties.MULTI_MACHINE_TEXTURE,mTile.getMachineType().getBaseTexture(mTile.getMachineTier()));
        return data;
    }

    @Override
    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        List<BakedQuad> quads = super.attachQuadsForSide(state, side, rand, data);
        return attachMultiQuads(quads,state,side,rand,data);
    }


}
