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
import java.util.HashMap;
import java.util.function.Function;

public class ModelPipe extends ModelBase {

    public static final ResourceLocation PIPE = new ResourceLocation(Ref.MODID, "blocks/pipe/pipe");
    public static final ResourceLocation WIRE = new ResourceLocation(Ref.MODID, "blocks/pipe/wire");
    public static final ResourceLocation CABLE = new ResourceLocation(Ref.MODID, "blocks/pipe/cable");

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        HashMap<String, IBakedModel> BAKED = new HashMap<>();

        for (PipeSize size : PipeSize.values()) {
            BAKED.put("base_" + size.getName(), new BakedBase(tex(load("pipe/" + size.getName() + "/base"), "0", PIPE).bake(state, format, getter)));
            BAKED.put("line_" + size.getName(), new BakedBase(tex(load("pipe/" + size.getName() + "/line"), "0", PIPE).bake(state, format, getter)));
        }

        return new BakedPipe(BAKED);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Arrays.asList(new ResourceLocation[]{PIPE, WIRE, CABLE});
    }
}
