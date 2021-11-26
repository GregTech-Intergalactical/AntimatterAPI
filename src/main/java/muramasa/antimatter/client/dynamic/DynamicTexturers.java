package muramasa.antimatter.client.dynamic;

import com.mojang.datafixers.util.Either;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
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
                    TransformationMatrix base = RenderHelper.faceRotation(t.source.side(), t.key.hFacing != null ? t.key.hFacing : (t.source.side().getAxis() == Axis.Y ? (t.state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? t.state.getValue(BlockStateProperties.HORIZONTAL_FACING) : null) : null));
                    IBakedModel b = t.sourceModel.bake(ModelLoader.instance(), ModelLoader.defaultTextureGetter(),
                            new SimpleModelTransform(base), t.source.getModel(t.type, t.currentDir, Direction.SOUTH));

                    List<BakedQuad> ret = new ObjectArrayList<>();
                    for (Direction dir : Ref.DIRS) {
                        ret.addAll(b.getQuads(t.state, dir, t.rand, t.data));
                    }
                    ret.addAll(b.getQuads(t.state, null, t.rand, t.data));
                    return t.source.transformQuads(t.state, ret);
                }
                return Collections.emptyList();
            }, t -> {
                if (t.data.hasProperty(AntimatterProperties.MULTI_TEXTURE_PROPERTY)) {
                    t.model.textureMap.put("base", Either.left(ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(t.dir))));
                } else {
                    t.model.textureMap.put("base", Either.left(ModelUtils.getBlockMaterial(t.key.machineTexture)));
                }
        t.source.setTextures(
                (name, texture) -> t.model.textureMap.put(name, Either.left(ModelUtils.getBlockMaterial(texture))));
    });

    public static final DynamicTextureProvider<TileEntityMachine<?>, TileEntityMachine.DynamicKey> TILE_DYNAMIC_TEXTURER = new DynamicTextureProvider<TileEntityMachine<?>, TileEntityMachine.DynamicKey>(
            t -> {
                IBakedModel b = t.sourceModel.bake(ModelLoader.instance(), ModelLoader.defaultTextureGetter(),
                        new SimpleModelTransform(RenderHelper.faceRotation(t.state)),
                        new ResourceLocation(t.source.getId()));
                List<BakedQuad> list = new ObjectArrayList<>(10);
                for (Direction dir : Ref.DIRS) {
                    list.addAll(b.getQuads(t.state, dir, t.rand, t.data));
                }
                list.addAll(b.getQuads(t.state, null, t.rand, t.data));
                return list;
            }, t -> {
        t.model.textureMap.put("base", Either.left(
                ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_TEXTURE_PROPERTY).apply(t.dir))));
        t.model.textureMap.put("overlay",
                Either
                        .left(ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MACHINE_PROPERTY).type.getOverlayTextures(
                                t.data.getData(AntimatterProperties.MACHINE_PROPERTY).state, t.source.getMachineTier())[Direction
                                .rotate(Utils.getModelRotation(Utils.dirFromState(t.source.getBlockState())).getRotation()
                                        .inverse().getMatrix(), t.dir)
                                .get3DDataValue()])));
    });
}
