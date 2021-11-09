package muramasa.antimatter.client.dynamic;

import com.mojang.datafixers.util.Either;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynamicTexturers {

    /**
     * Dynamic texture implementations.
     **/

    public static final ResourceLocation PIPE_COVER_MODEL = new ResourceLocation(Ref.ID, "block/cover/cover_pipe");
    public static final DynamicTextureProvider<ICover, ICover.DynamicKey> COVER_DYNAMIC_TEXTURER = new DynamicTextureProvider<ICover, ICover.DynamicKey>(
            t -> {
                TransformationMatrix base = Utils.getModelRotation(t.currentDir).getRotation();
                if (t.state.hasProperty(BlockMachine.HORIZONTAL_FACING)) {
                    switch (t.state.get(BlockMachine.HORIZONTAL_FACING)) {
                        case NORTH:
                            break;
                        case SOUTH:
                            base = base.compose(new TransformationMatrix(null, new Quaternion(new Vector3f(0, 0, 1), 180f, true), null, null));
                            break;
                        case WEST:
                            base = base.compose(new TransformationMatrix(null, new Quaternion(new Vector3f(0, 0, 1), -90f, true), null, null));
                            break;
                        case EAST:
                            base = base.compose(new TransformationMatrix(null, new Quaternion(new Vector3f(0, 0, 1), 90f, true), null, null));
                            break;
                    }
                }
                IBakedModel b = t.sourceModel.bakeModel(ModelLoader.instance(), ModelLoader.defaultTextureGetter(),
                        new SimpleModelTransform(base), t.source.getModel(t.type, t.currentDir, Direction.NORTH));
                return t.source.transformQuads(t.state, Stream.concat(b.getQuads(t.state, t.currentDir, t.rand, t.data).stream(), b.getQuads(t.state, null, t.rand, t.data).stream()).collect(Collectors.toList()));
            }, t -> {
        t.model.textures.put("base", Either.left(ModelUtils.getBlockMaterial(t.key.machineTexture)));
        t.source.setTextures(
                (name, texture) -> t.model.textures.put(name, Either.left(ModelUtils.getBlockMaterial(texture))));
    });

    public static final DynamicTextureProvider<TileEntityMachine<?>, TileEntityMachine.DynamicKey> TILE_DYNAMIC_TEXTURER = new DynamicTextureProvider<TileEntityMachine<?>, TileEntityMachine.DynamicKey>(
            t -> {
                IBakedModel b = t.sourceModel.bakeModel(ModelLoader.instance(), ModelLoader.defaultTextureGetter(),
                        new SimpleModelTransform(
                                Utils.getModelRotationCoverClient(Utils.dirFromState(t.state)).getRotation().inverse()),
                        new ResourceLocation(t.source.getId()));
                return b.getQuads(t.state, null, t.rand, t.data);
            }, t -> {
        t.model.textures.put("base", Either.left(
                ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE).apply(t.dir))));
        t.model.textures.put("overlay",
                Either
                        .left(ModelUtils.getBlockMaterial(t.data.getData(AntimatterProperties.MACHINE_TYPE).getOverlayTextures(
                                t.data.getData(AntimatterProperties.MACHINE_STATE), t.source.getMachineTier())[Direction
                                .rotateFace(Utils.getModelRotation(Utils.dirFromState(t.source.getBlockState())).getRotation()
                                        .inverse().getMatrix(), t.dir)
                                .getIndex()])));
    });
}
