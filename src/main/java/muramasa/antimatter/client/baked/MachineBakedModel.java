package muramasa.antimatter.client.baked;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.DirectionalQuadTransformer;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class MachineBakedModel extends AntimatterBakedModel<MachineBakedModel> {

    private final ImmutableMap<MachineState, BakedModel[]> sides;
    public MachineBakedModel(TextureAtlasSprite particle, ImmutableMap<MachineState, BakedModel[]> sides) {
        super(particle);
        this.sides = sides;
    }    


    protected List<BakedQuad> getCoverQuads(BlockState state, Direction side, Random rand, AntimatterProperties.MachineProperties data, IModelData extra) {
   
        ICover cover = data.covers[side.get3DDataValue()];
        if (cover.isEmpty()) return Collections.emptyList();
        Texture tex = extra.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)
                ? extra.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(side)
                : data.machTexture.apply(side);
        List<BakedQuad> list = new ObjectArrayList<>();
        for (Direction s : Ref.DIRS) {
            list = data.coverTexturer.apply(side).getQuads("cover", list, state, cover,
            new ICover.DynamicKey(state, tex, cover.getId()), s.get3DDataValue(),
            extra);
        }
        return list;
    }
    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, Random rand, IModelData data) {
        if (side == null) {
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ObjectArrayList<>(20);
        AntimatterProperties.MachineProperties props = data.getData(AntimatterProperties.MACHINE_PROPERTY);
        List<BakedQuad> coverQuads = getCoverQuads(state, side, rand, props, data);
        if (!coverQuads.isEmpty()) return coverQuads;

        if (data.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)) {
            Function<Direction, Texture> ft = data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY);
            return props.machineTexturer.getQuads("machine", new ObjectArrayList<>(), state, props.type, new TileEntityMachine.DynamicKey(new ResourceLocation(props.type.getId()), ft.apply(side), side, props.state), side.get3DDataValue(), data);
        }

        BakedModel model = getModel(state, side, props.state);
        for (Direction dir : Ref.DIRS) {
            quads.addAll(model.getQuads(state, dir, rand, data));
        }
        quads.addAll(model.getQuads(state, null, rand, data));

        Matrix4f f = new Matrix4f();
        f.setIdentity();
        Transformation mat = new Transformation(f);
        mat = mat.blockCornerToCenter();
        mat = mat.compose(RenderHelper.faceRotation(state));
        mat = mat.blockCenterToCorner();
        DirectionalQuadTransformer transformer = new DirectionalQuadTransformer(mat);
        return transformer.processMany(quads, side);
    }

    public BakedModel getModel(BlockState state, Direction dir, MachineState m) {
        Vec3i vector3i = dir.getNormal();
        Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
        vector4f.transform(RenderHelper.faceRotation(state).inverse().getMatrix());
        Direction side = Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
        return sides.get(m)[side.get3DDataValue()];
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
    public IModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, IModelData d) {
        final IModelData data = super.getModelData(world, pos, state, d);
        TileEntityMachine<?> machine = (TileEntityMachine<?>) world.getBlockEntity(pos);
        ICover[] covers = machine.coverHandler.map(t -> t.getAll()).orElse(new ICover[]{ICover.empty,ICover.empty,ICover.empty,ICover.empty,ICover.empty,ICover.empty});
        Machine<?> m = machine.getMachineType();
        Function<Direction, Texture> mText = a -> {
            Texture[] tex = machine.getMachineType().getBaseTexture(machine.getMachineTier());
            if (tex.length == 1) return tex[0];
            return tex[a.get3DDataValue()];
        };
        MachineState st = machine.getMachineState().getTextureState();
        Function<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> tx = a -> machine.coverHandler.map(t -> t.getTexturer(a)).orElse(null);
        AntimatterProperties.MachineProperties mh = new AntimatterProperties.MachineProperties(m, machine.getMachineTier(), covers, st, mText, machine.multiTexturer.get(), tx);
        data.setData(AntimatterProperties.MACHINE_PROPERTY, mh);
        return data;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
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
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public List<BakedQuad> getItemQuads(Direction side, Random rand, IModelData data) {
        return Collections.EMPTY_LIST;
    }

}
