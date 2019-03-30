package muramasa.gregtech.client.render.models;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import muramasa.gregtech.client.render.bakedmodels.BakedPipe;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import scala.actors.threadpool.Arrays;

import java.util.Collection;
import java.util.function.Function;

public class ModelPipe extends ModelBase {

    public static final ResourceLocation PIPE = new ResourceLocation(Ref.MODID, "blocks/pipe/pipe");
    public static final ResourceLocation WIRE = new ResourceLocation(Ref.MODID, "blocks/pipe/wire");
    public static final ResourceLocation CABLE = new ResourceLocation(Ref.MODID, "blocks/pipe/cable");

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IBakedModel[][] BAKED = new IBakedModel[PipeSize.VALUES.length][10];
        for (PipeSize size : PipeSize.values()) {
            BAKED[size.ordinal()][0] = new BakedBase(tex(load("pipe/" + size.getName() + "/base"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][1] = new BakedBase(tex(load("pipe/" + size.getName() + "/single"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][2] = new BakedBase(tex(load("pipe/" + size.getName() + "/line"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][3] = new BakedBase(tex(load("pipe/" + size.getName() + "/elbow"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][4] = new BakedBase(tex(load("pipe/" + size.getName() + "/side"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][5] = new BakedBase(tex(load("pipe/" + size.getName() + "/corner"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][6] = new BakedBase(tex(load("pipe/" + size.getName() + "/arrow"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][7] = new BakedBase(tex(load("pipe/" + size.getName() + "/cross"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][8] = new BakedBase(tex(load("pipe/" + size.getName() + "/five"), "0", PIPE).bake(state, format, getter));
            BAKED[size.ordinal()][9] = new BakedBase(tex(load("pipe/" + size.getName() + "/all"), "0", PIPE).bake(state, format, getter));
        }
        return new BakedPipe(BAKED);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Arrays.asList(new ResourceLocation[]{PIPE, WIRE, CABLE});
    }
}
