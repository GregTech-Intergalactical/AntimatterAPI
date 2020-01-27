package muramasa.antimatter.client;

import com.mojang.datafixers.util.Either;
import muramasa.gtu.Ref;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ModelBuilder {

    private IUnbakedModel model;
    private Set<Material> textures = new HashSet<>();
    private Direction[] rotations = new Direction[0];

    public ModelBuilder() {

    }

    public ModelBuilder(IUnbakedModel model) {
        this.model = model;
    }

    public IUnbakedModel get() {
        return model;
    }

    public Set<Material> getTextures() {
        return textures;
    }

    public ModelBuilder of(IUnbakedModel model) {
        this.model = model;
        return this;
    }

    public ModelBuilder of(String path) {
        return of(new ResourceLocation(Ref.MODID, path));
    }

    public ModelBuilder of(ResourceLocation loc) {
        assert ModelLoader.instance() != null;
        IUnbakedModel unbaked = ModelLoader.instance().getModelOrMissing(loc);
        if (unbaked instanceof BlockModel) {
            BlockModel toCopy = ((BlockModel) unbaked);
            HashMap<String, Either<Material, String>> textures = new HashMap<>();
            toCopy.textures.forEach(textures::put);
            model = new BlockModel(toCopy.getParentLocation(), toCopy.getElements(), textures, toCopy.ambientOcclusion, toCopy.func_230176_c_(), toCopy.getAllTransforms(), toCopy.getOverrides());
            return this;
        }
        return new ModelBuilder(ModelLoader.instance().getModelOrMissing(ModelBakery.MODEL_MISSING));
    }

    public ModelBuilder simple() {
        return of("block/preset/simple");
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
        return tex(element, new ResourceLocation(texture.replace("mc", "minecraft")));
    }

    public ModelBuilder tex(String element, ResourceLocation texture) {
        if (model instanceof BlockModel) {
            ((BlockModel) model).textures.put(element, Either.left(ModelUtils.getBlockMaterial(texture)));
        }
        textures.add(ModelUtils.getBlockMaterial(texture));
        return this;
    }

    public ModelBuilder rot(Direction... rotations) {
        this.rotations = rotations;
        return this;
    }

    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
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

//            //TODO find a better solution
//            List<BakedQuad> originalQuads = model.bake(owner, bakery, getter, transform, overrides, loc).getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE);
//            List<BakedQuad> transformedQuads = ModelUtils.trans(originalQuads, rotations);
//            return new QuadContainer(transformedQuads);

            TransformationMatrix rotatedTrans = TransformationMatrix.func_227983_a_(); //Identity;
            rotatedTrans = rotatedTrans.blockCenterToCorner();
            for (int i = 0; i < rotations.length; i++) {
                rotatedTrans = rotatedTrans.compose(new TransformationMatrix(ModelUtils.FACING_TO_MATRIX[rotations[i].getIndex()]));
            }
            return model.func_225613_a_(bakery, getter, new SimpleModelTransform(rotatedTrans), loc);

        }
        return model.func_225613_a_(bakery, getter, transform, loc);
    }
}
