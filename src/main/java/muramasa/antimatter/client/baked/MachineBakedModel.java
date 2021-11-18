package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class MachineBakedModel extends CoveredBakedModel {

    public MachineBakedModel(TextureAtlasSprite particle, Int2ObjectOpenHashMap<IBakedModel[]> bakedTuple) {
        super(particle, bakedTuple);
    }

    public List<BakedQuad> attachMultiQuads(List<BakedQuad> quads, BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        BlockMachine bm = (BlockMachine) state.getBlock();
        if (data.hasProperty(AntimatterProperties.MACHINE_TEXTURE)) {
            Function<Direction, Texture> fn = data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE);
            if (fn != null) {
                Texture tex = fn.apply(side);
                TileEntityBase tile = data.getData(AntimatterProperties.TILE_PROPERTY);
                if (!(tile instanceof TileEntityMachine)) return quads;
                TileEntityMachine<?> t = (TileEntityMachine) tile;
                MachineCoverHandler<?> covers = t.coverHandler.orElse(null);
                ICover c = covers == null ? null : covers.get(side);
                if (c == null || c.isEmpty()) {
                    TileEntityMachine.DynamicKey key = new TileEntityMachine.DynamicKey(new ResourceLocation(bm.getType().getId()), tex, Utils.dirFromState(state), data.getData(AntimatterProperties.MACHINE_STATE));
                    quads = t.multiTexturer.get().getQuads("machine", quads, state, t, key, side.get3DDataValue(), data);
                    assert quads.size() == 0 || quads.get(0).getDirection() == side;
                }
            }
        }
        return quads;
    }


    @Override
    public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData data) {
        data = super.getModelData(world, pos, state, data);
        TileEntityMachine machine = (TileEntityMachine) data.getData(AntimatterProperties.TILE_PROPERTY);
        data.setData(AntimatterProperties.MACHINE_TYPE, machine.getMachineType());
        data.setData(AntimatterProperties.MACHINE_TEXTURE, a -> {
            Texture[] tex = machine.getMachineType().getBaseTexture(machine.getMachineTier());
            if (tex.length == 1) return tex[0];
            return tex[a.get3DDataValue()];
        });
        data.setData(AntimatterProperties.MACHINE_STATE, machine.getMachineState());
        return data;
    }

    @Override
    protected List<BakedQuad> attachQuadsForSide(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        List<BakedQuad> quads = super.attachQuadsForSide(state, side, rand, data);
        return attachMultiQuads(quads, state, side, rand, data);
    }

}
