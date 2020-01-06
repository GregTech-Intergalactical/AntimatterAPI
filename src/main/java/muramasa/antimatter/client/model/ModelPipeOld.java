//package muramasa.gtu.client.render.models;
//
//import muramasa.gtu.Ref;
//import muramasa.gtu.data.Textures;
//import muramasa.antimatter.pipe.PipeSize;
//import muramasa.antimatter.client.ModelUtils;
//import muramasa.antimatter.client.baked.BakedBase;
//import muramasa.antimatter.client.baked.BakedPipe;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.block.model.ModelResourceLocation;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.util.Direction;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.model.IModel;
//import net.minecraftforge.common.model.IModelState;
//import net.minecraftforge.common.model.TRSRTransformation;
//
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.function.Function;
//
//public class ModelPipe implements IModel {
//
        //TODO just make culled versions of all pipe models, and bake and store all of them.
//    @Override
//    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
//        BakedPipe.BAKED = new IBakedModel[PipeSize.VALUES.length][10];
//        for (PipeSize size : PipeSize.VALUES) {
//            BakedPipe.BAKED[size.ordinal()][0] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/base").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][1] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/single").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][2] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/line").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][3] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/elbow").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][4] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/side").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][5] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/corner").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][6] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/arrow").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][7] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/cross").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][8] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/five").bake(state, format, getter));
//            BakedPipe.BAKED[size.ordinal()][9] = new BakedBase(ModelUtils.load("pipe/" + size.getName() + "/all").bake(state, format, getter));
//        }
//
//        IModel pipe_extra = ModelUtils.load(new ModelResourceLocation(Ref.MODID + ":machine/pipe_extra"));
//        BakedPipe.PIPE_EXTRA = new IBakedModel[6];
//        for (int i = 0; i < 6; i++) {
//            BakedPipe.PIPE_EXTRA[i] = pipe_extra.bake(TRSRTransformation.from(Ref.DIRECTIONS[i]), format, getter);
//        }
//
//        return new BakedPipe();
//    }
//
//    @Override
//    public Collection<ResourceLocation> getTextures() {
//        List<ResourceLocation> locs = new LinkedList<>();
//        locs.add(Textures.PIPE);
//        locs.add(Textures.WIRE);
//        locs.add(Textures.CABLE);
//        for (int i = 0; i < PipeSize.VALUES.length; i++) {
//            locs.add(Textures.PIPE_FACE[i]);
//            locs.add(Textures.CABLE_FACE[i]);
//        }
//        return locs;
//    }
//}
