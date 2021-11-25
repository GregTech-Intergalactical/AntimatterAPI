package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class MachineBakedModel extends AntimatterBakedModel<MachineBakedModel> {

    private final ImmutableMap<MachineState, BakedMachineSide[]> sides;
    public MachineBakedModel(TextureAtlasSprite particle, ImmutableMap<MachineState, BakedMachineSide[]> sides) {
        super(particle);
        this.sides = sides;
    }    

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, Random rand, IModelData data) {
        if (side == null) {
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ObjectArrayList<>(20);
        IBakedModel model = getModel(state, side, data.getData(AntimatterProperties.MACHINE_STATE));
        for (Direction dir : Ref.DIRS) {
            quads.addAll(model.getQuads(state, dir, rand, data));
        }
        quads.addAll(model.getQuads(state, null, rand, data));

        Matrix4f f = new Matrix4f();
        f.setIdentity();
        TransformationMatrix mat = new TransformationMatrix(f);
        mat = mat.blockCornerToCenter();
        if (state.hasProperty(BlockMachine.HORIZONTAL_FACING)) {
            mat = mat.compose(RenderHelper.faceRotation(state.getValue(BlockMachine.HORIZONTAL_FACING), state.getValue(BlockStateProperties.FACING)));
        } else {
            mat = mat.compose(RenderHelper.faceRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING), null));
        }
        mat = mat.blockCenterToCorner();
        QuadTransformer transformer = new QuadTransformer(mat);
        return transformer.processMany(quads);
    }

    public BakedMachineSide getModel(BlockState state, Direction dir, MachineState m) {
        return sides.get(m)[Utils.coverRotateFacing(dir, Utils.dirFromState(state)).get3DDataValue()];
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
        TileEntityMachine<?> machine = (TileEntityMachine<?>) world.getBlockEntity(pos);
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
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    public List<BakedQuad> getItemQuads(Direction side, Random rand, IModelData data) {
        return Collections.EMPTY_LIST;
    }

}
