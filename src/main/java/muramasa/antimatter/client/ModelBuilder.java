package muramasa.antimatter.client;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.client.baked.QuadContainer;
import muramasa.gtu.Ref;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.List;
import java.util.function.Function;

public class ModelBuilder {

    private IModel model;
    private Direction[] rotations = new Direction[0];

    public ModelBuilder() {

    }

    public ModelBuilder(IModel model) {
        this.model = model;
    }

    public IModel get() {
        return model;
    }

    public ModelBuilder of(ResourceLocation loc) {
        try {
            return new ModelBuilder(ModelLoaderRegistry.getModel(loc));
        } catch (Exception e) {
            System.err.println("ModelBase.load() failed due to " + e + ":");
            e.printStackTrace();
            return new ModelBuilder(ModelLoaderRegistry.getMissingModel());
        }
    }

    public ModelBuilder of(String path) {
        return of(new ResourceLocation(Ref.MODID, path));
    }

    public ModelBuilder tex(String element, Block block) {
        return tex(element, new ResourceLocation(block.getRegistryName().getNamespace(), "block/" + block.getRegistryName().getPath()));
    }

    public ModelBuilder tex(Direction[] dirs, ResourceLocation[] textures) {
        for (int i = 0; i < textures.length; i++) {
            tex(dirs[i].getName(), textures[i]);
        }
        return this;
    }

    public ModelBuilder tex(ResourceLocation loc, String... elements) {
        for (int i = 0; i < elements.length; i++) {
            tex(elements[i], loc);
        }
        return this;
    }

    public ModelBuilder tex(String[] elements, ResourceLocation[] textures) {
        for (int i = 0; i < elements.length; i++) {
            tex(elements[i], textures[i]);
        }
        return this;
    }

    public ModelBuilder tex(String element, String texture) {
        return tex(element, new ResourceLocation(texture));
    }

    public ModelBuilder tex(String element, ResourceLocation texture) {
        try {
            model = model.retexture(ImmutableMap.of(element, texture.toString()));
            return this;
        } catch (Exception e) {
            System.err.println("ModelContainer.tex() failed due to " + e + ":");
            e.printStackTrace();
            return this;
        }
    }

    public ModelBuilder rot(Direction... rotations) {
        this.rotations = rotations;
        return this;
    }

    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        if (rotations.length > 0) {
//            Matrix4f mat = new Matrix4f(ModelUtils.FACING_TO_MATRIX[directions[0].getIndex()]);
//            for (int i = 1; i < directions.length; i++) {
//                mat.mul(new Matrix4f(ModelUtils.FACING_TO_MATRIX[directions[i].getIndex()]));
//            }
//            TRSRTransformation trans = TRSRTransformation.from(rotations[0]);
//            for (int i = 1; i < rotations.length; i++) {
                //trans.mul(null, ModelUtils.FACING_TO_MATRIX[rotations[i].getIndex()].);
//                trans.compose(TRSRTransformation.from(rotations[i]));
//            }

//            return model.bake(bakery, getter, new ISprite() {
//                @Override
//                public IModelState getState() {
//                    return trans;
//                }
//            }, format);

            //TODO find a better solution
            List<BakedQuad> originalQuads = model.bake(bakery, getter, sprite, format).getQuads(null, null, Ref.RNG, ModelUtils.EMPTY_MODEL_DATA);
            List<BakedQuad> transformedQuads = ModelUtils.trans(originalQuads, rotations);
            return new QuadContainer(transformedQuads);

        }
        return model.bake(bakery, getter, sprite, format);
    }
}
