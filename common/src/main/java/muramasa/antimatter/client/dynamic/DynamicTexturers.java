package muramasa.antimatter.client.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.client.SimpleModelState;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.mixin.client.BlockModelAccessor;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collections;
import java.util.List;

public class DynamicTexturers {

    /**
     * Dynamic texture implementations.
     **/

    public static final DynamicTextureProvider<ICover, ICover.DynamicKey> COVER_DYNAMIC_TEXTURER = new DynamicTextureProvider<ICover, ICover.DynamicKey>(
            t -> {
                if (t.currentDir == t.source.side()) {
                    UnbakedModel m = null;
                    try {
                        //for some reason first load can cause circular exception
                        m = ModelUtils.getModel(t.source.getModel(t.type, Direction.SOUTH));
                    } catch (Exception ignored) {

                    }
                    if (m == null) {
                        return Collections.emptyList();
                    }
                    BlockModel model = (BlockModel) m;
                    if (t.data.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)) {
                        ((BlockModelAccessor)model).getTextureMap().put("base", Either.left(ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(t.source.side()))));
                    } else {
                        ((BlockModelAccessor)model).getTextureMap().put("base", Either.left(ModelUtils.getBlockMaterial(t.key.machineTexture)));
                    }
                    t.source.setTextures(
                            (name, texture) -> ((BlockModelAccessor)model).getTextureMap().put(name, Either.left(ModelUtils.getBlockMaterial(texture))));
                    Transformation base = RenderHelper.faceRotation(t.source.side(), t.key.hFacing != null ? t.key.hFacing : (t.source.side().getAxis() == Axis.Y ? (t.state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? t.state.getValue(BlockStateProperties.HORIZONTAL_FACING) : null) : null));
                    BakedModel b = model.bake(ModelUtils.getModelBakery(), model, ModelUtils.getDefaultTextureGetter(),
                            new SimpleModelState(base), t.source.getModel(t.type, Direction.SOUTH), true);

                    List<BakedQuad> ret = new ObjectArrayList<>();
                    for (Direction dir : Ref.DIRS) {
                        ret.addAll(b.getQuads(t.state, dir, t.rand, t.data));
                    }
                    ret.addAll(b.getQuads(t.state, null, t.rand, t.data));
                    return t.source.transformQuads(t.state, ret);
                }
                return Collections.emptyList();
            });

    public static final DynamicTextureProvider<Machine<?>, TileEntityMachine.DynamicKey> TILE_DYNAMIC_TEXTURER = new DynamicTextureProvider<Machine<?>, TileEntityMachine.DynamicKey>(
            t -> {
                Vec3i vector3i = t.currentDir.getNormal();
                Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
                vector4f.transform(RenderHelper.faceRotation(t.state).inverse().getMatrix());
                Direction side = Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
                UnbakedModel model = ModelUtils.getModel(t.source.getModel(t.type, side));
                BlockModel m = (BlockModel) model;
                ((BlockModelAccessor)m).getTextureMap().put("base", Either.left(
                    ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(side))));
                   AntimatterProperties.MachineProperties prop = t.data.getData(AntimatterProperties.MACHINE_PROPERTY);
                ((BlockModelAccessor)m).getTextureMap().put("overlay",
                    Either
                            .left(ModelUtils.getBlockMaterial(prop.type.getOverlayTextures(
                                    prop.state, prop.tier)[side.get3DDataValue()])));
                BakedModel b = model.bake(ModelUtils.getModelBakery(), ModelUtils.getDefaultTextureGetter(),
                        new SimpleModelState(RenderHelper.faceRotation(t.state)),
                        new ResourceLocation(t.source.getId()));
                List<BakedQuad> list = new ObjectArrayList<>(10);
                for (Direction dir : Ref.DIRS) {
                    list.addAll(b.getQuads(t.state, dir, t.rand, t.data));
                }
                list.addAll(b.getQuads(t.state, null, t.rand, t.data));
                return list;
            });
}
