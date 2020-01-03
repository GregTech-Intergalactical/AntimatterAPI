//package muramasa.antimatter.client.bakedblockold;
//
//import muramasa.gtu.client.render.ModelUtils;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.block.model.ItemOverrideList;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModel;
//import net.minecraftforge.common.model.IModelState;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.function.Function;
//
//@Deprecated
//public class ModelTextureData implements IModel {
//
//    protected BlockBakedOld block;
//    protected ItemOverrideList item;
//
//    public ModelTextureData(BlockBakedOld block) {
//        this.block = block;
//    }
//
//    @Override
//    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
//        IBakedModel baked = ModelUtils.load(block.getModel()).bake(state, format, getter);
//        return new BakedTextureData(baked, block.getOverride(baked));
//    }
//
//    @Override
//    public Collection<ResourceLocation> getTextures() {
//        return new ArrayList<>(block.getTextures());
//    }
//}
