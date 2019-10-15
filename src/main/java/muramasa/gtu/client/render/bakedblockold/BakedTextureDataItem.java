//package muramasa.gtu.client.render.bakedblockold;
//
//import muramasa.gtu.api.texture.TextureData;
//import muramasa.gtu.client.render.bakedmodels.BakedBase;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.renderer.model.BakedQuad;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.util.Direction;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//@Deprecated
//public class BakedTextureDataItem extends BakedBase {
//
//    protected IBakedModel baked;
//    protected TextureData data;
//
//    public BakedTextureDataItem(IBakedModel baked, TextureData data) {
//        this.baked = baked;
//        this.data = data;
//    }
//
//    @Override
//    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, @Nullable Direction dir, long rand) {
//        return data.apply(baked.getQuads(state, dir, rand));
//    }
//}
