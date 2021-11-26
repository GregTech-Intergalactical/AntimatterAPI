package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
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
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class MachineBakedModel extends AntimatterBakedModel<MachineBakedModel> {

    private final ImmutableMap<MachineState, IBakedModel[]> sides;
    public MachineBakedModel(TextureAtlasSprite particle, ImmutableMap<MachineState, IBakedModel[]> sides) {
        super(particle);
        this.sides = sides;
    }    


    protected List<BakedQuad> getCoverQuads(BlockState state, Direction side, Random rand, AntimatterProperties.MachineProperties data, IModelData extra) {
   
        ICover cover = data.covers[side.get3DDataValue()];
        if (cover.isEmpty()) return Collections.emptyList();
        Texture tex = extra.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)
                ? extra.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(side)
                : data.machTexture.apply(side);
        return data.coverTexturer.apply(side).getQuads("cover", new ObjectArrayList<>(), state, cover,
                new ICover.DynamicKey(Utils.dirFromState(state), tex, cover.getId()), side.get3DDataValue(),
                extra);
    }
    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, Random rand, IModelData data) {
        if (side == null) {
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ObjectArrayList<>(20);
        AntimatterProperties.MachineProperties props = data.getData(AntimatterProperties.MACHINE_PROPERTY);
        List<BakedQuad> coverQuads = getCoverQuads(state, side, rand, props, data);
        if (true) return coverQuads;

        if (data.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)) {
            Function<Direction, Texture> ft = data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY);
            return props.machineTexturer.getQuads("machine", new ObjectArrayList<>(), state, props.getTile(), new TileEntityMachine.DynamicKey(new ResourceLocation(props.type.getId()), ft.apply(side), Utils.dirFromState(state), props.state), side.get3DDataValue(), data);
        }

        IBakedModel model = getModel(state, side, props.state);
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

    public IBakedModel getModel(BlockState state, Direction dir, MachineState m) {
        return sides.get(m)[Utils.coverRotateFacing(dir, Utils.dirFromState(state)).get3DDataValue()];
    }
/*
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
*/

    @Override
    public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData d) {
        final IModelData data = super.getModelData(world, pos, state, d);
        TileEntityMachine<?> machine = (TileEntityMachine<?>) world.getBlockEntity(pos);
        ICover[] covers = machine.coverHandler.map(t -> t.getAll()).orElse(new ICover[]{ICover.empty,ICover.empty,ICover.empty,ICover.empty,ICover.empty,ICover.empty});
        Machine<?> m = machine.getMachineType();
        Function<Direction, Texture> mText = a -> {
            Texture[] tex = machine.getMachineType().getBaseTexture(machine.getMachineTier());
            if (tex.length == 1) return tex[0];
            return tex[a.get3DDataValue()];
        };
        MachineState st = machine.getMachineState();
        Function<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> tx = a -> machine.coverHandler.map(t -> t.getTexturer(a)).orElse(null);
        AntimatterProperties.MachineProperties mh = new AntimatterProperties.MachineProperties(machine,m, covers, st, mText, machine.multiTexturer.get(), tx);
        data.setData(AntimatterProperties.MACHINE_PROPERTY, mh);
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
