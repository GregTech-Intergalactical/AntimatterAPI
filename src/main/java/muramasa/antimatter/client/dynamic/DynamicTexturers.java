package muramasa.antimatter.client.dynamic;

import com.mojang.datafixers.util.Either;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;

public class DynamicTexturers {

    /**
     * Dynamic texture implementations.
     **/

    public static final ResourceLocation PIPE_COVER_MODEL = new ResourceLocation(Ref.ID, "block/cover/cover_pipe");
    public static final DynamicTextureProvider<ICover, ICover.DynamicKey> COVER_DYNAMIC_TEXTURER = new DynamicTextureProvider<ICover, ICover.DynamicKey>(
            t -> {
                IBakedModel b = t.sourceModel.bakeModel(ModelLoader.instance(), ModelLoader.defaultTextureGetter(),
                        Utils.getModelRotation(t.currentDir), t.source.getModel(t.type, t.currentDir, Direction.NORTH));
                return t.source.transformQuads(t.state, b.getQuads(t.state, t.currentDir, t.rand, t.data));
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
