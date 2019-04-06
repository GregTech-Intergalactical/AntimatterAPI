package muramasa.gregtech.client.render.models;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import muramasa.gregtech.client.render.bakedmodels.BakedPipe;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ModelPipe implements IModel {

    public static final Texture PIPE = new Texture("blocks/pipe/pipe_side");
    public static final Texture WIRE = new Texture("blocks/pipe/wire_side");
    public static final Texture CABLE = new Texture("blocks/pipe/cable_side");

    public static Texture[] PIPE_FACE = new Texture[] {
        new Texture("blocks/pipe/pipe_vtiny"),
        new Texture("blocks/pipe/pipe_tiny"),
        new Texture("blocks/pipe/pipe_small"),
        new Texture("blocks/pipe/pipe_normal"),
        new Texture("blocks/pipe/pipe_large"),
        new Texture("blocks/pipe/pipe_huge")
    };

    public static Texture[] CABLE_FACE = new Texture[] {
        new Texture("blocks/pipe/cable_vtiny"),
        new Texture("blocks/pipe/cable_tiny"),
        new Texture("blocks/pipe/cable_small"),
        new Texture("blocks/pipe/cable_normal"),
        new Texture("blocks/pipe/cable_large"),
        new Texture("blocks/pipe/cable_huge")
    };

    public static Texture[] WIRE_FACE = new Texture[] {
        WIRE, WIRE, WIRE, WIRE, WIRE, WIRE
    };

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IBakedModel[][] BAKED = new IBakedModel[PipeSize.VALUES.length][10];
        for (PipeSize size : PipeSize.values()) {
            BAKED[size.ordinal()][0] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/base").bake(state, format, getter));
            BAKED[size.ordinal()][1] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/single").bake(state, format, getter));
            BAKED[size.ordinal()][2] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/line").bake(state, format, getter));
            BAKED[size.ordinal()][3] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/elbow").bake(state, format, getter));
            BAKED[size.ordinal()][4] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/side").bake(state, format, getter));
            BAKED[size.ordinal()][5] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/corner").bake(state, format, getter));
            BAKED[size.ordinal()][6] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/arrow").bake(state, format, getter));
            BAKED[size.ordinal()][7] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/cross").bake(state, format, getter));
            BAKED[size.ordinal()][8] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/five").bake(state, format, getter));
            BAKED[size.ordinal()][9] = new BakedBase(ModelBase.load("pipe/" + size.getName() + "/all").bake(state, format, getter));
        }
        return new BakedPipe(BAKED);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        List<ResourceLocation> locs = new LinkedList<>();
        locs.add(PIPE.getLoc());
        locs.add(WIRE.getLoc());
        locs.add(CABLE.getLoc());
        for (int i = 0; i < PipeSize.VALUES.length; i++) {
            locs.add(PIPE_FACE[i].getLoc());
            locs.add(CABLE_FACE[i].getLoc());
        }
        return locs;
    }
}
