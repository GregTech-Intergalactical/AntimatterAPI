package muramasa.antimatter.client.dynamic;

import com.mojang.datafixers.util.Either;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicTexturers {

    /**
     * Dynamic texture implementations.
     **/

    public static final ResourceLocation PIPE_COVER_MODEL = new ResourceLocation(Ref.ID, "block/cover/cover_pipe");
    public static final DynamicTextureProvider<ICover, ICover.DynamicKey> COVER_DYNAMIC_TEXTURER = new DynamicTextureProvider<ICover, ICover.DynamicKey>(
            t -> {
                if (t.currentDir == t.source.side()) {
                    IUnbakedModel m = ModelLoader.instance().getModel(t.source.getModel(t.type, Direction.SOUTH));
                    BlockModel model = (BlockModel) m;
                    if (t.data.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)) {
                        model.textureMap.put("base", Either.left(ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(t.source.side()))));
                    } else {
                        model.textureMap.put("base", Either.left(ModelUtils.getBlockMaterial(t.key.machineTexture)));
                    }
            t.source.setTextures(
                    (name, texture) -> model.textureMap.put(name, Either.left(ModelUtils.getBlockMaterial(texture))));
                    TransformationMatrix base = RenderHelper.faceRotation(t.source.side(), t.key.hFacing != null ? t.key.hFacing : (t.source.side().getAxis() == Axis.Y ? (t.state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? t.state.getValue(BlockStateProperties.HORIZONTAL_FACING) : null) : null));
                    IBakedModel b = model.bake(ModelLoader.instance(), model, ModelLoader.defaultTextureGetter(),
                            new SimpleModelTransform(base), t.source.getModel(t.type, Direction.SOUTH), true);

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
                Vector3i vector3i = t.currentDir.getNormal();
                Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
                vector4f.transform(RenderHelper.faceRotation(t.state).inverse().getMatrix());
                Direction side = Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
                IUnbakedModel model = ModelLoader.instance().getModel(t.source.getModel(t.type, side));
                BlockModel m = (BlockModel) model;
                m.textureMap.put("base", Either.left(
                    ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(side))));
                   AntimatterProperties.MachineProperties prop = t.data.getData(AntimatterProperties.MACHINE_PROPERTY);
                m.textureMap.put("overlay",
                    Either
                            .left(ModelUtils.getBlockMaterial(prop.type.getOverlayTextures(
                                    prop.state, prop.tier)[side.get3DDataValue()])));
                IBakedModel b = model.bake(ModelLoader.instance(), ModelLoader.defaultTextureGetter(),
                        new SimpleModelTransform(RenderHelper.faceRotation(t.state)),
                        new ResourceLocation(t.source.getId()));
                List<BakedQuad> list = new ObjectArrayList<>(10);
                for (Direction dir : Ref.DIRS) {
                    list.addAll(b.getQuads(t.state, dir, t.rand, t.data));
                }
                list.addAll(b.getQuads(t.state, null, t.rand, t.data));
                return list;
            });
}
