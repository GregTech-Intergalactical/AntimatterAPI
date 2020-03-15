package muramasa.antimatter.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;

public class PipeType implements IAntimatterObject {

    public static final PipeType ITEM = new PipeType(
        "item_pipe", 0,
        new Texture(Ref.ID, "block/pipe/pipe_side"),
        new Texture[] {
            new Texture(Ref.ID, "block/pipe/pipe_vtiny"),
            new Texture(Ref.ID, "block/pipe/pipe_tiny"),
            new Texture(Ref.ID, "block/pipe/pipe_small"),
            new Texture(Ref.ID, "block/pipe/pipe_normal"),
            new Texture(Ref.ID, "block/pipe/pipe_large"),
            new Texture(Ref.ID, "block/pipe/pipe_huge")
        }
    );

    public static final PipeType ITEM_RESTRICTIVE = new PipeType(
        "item_pipe_restrictive", 0,
        new Texture(Ref.ID, "block/pipe/pipe_side"),
        new Texture[] {
            new Texture(Ref.ID, "block/pipe/pipe_vtiny"),
            new Texture(Ref.ID, "block/pipe/pipe_tiny"),
            new Texture(Ref.ID, "block/pipe/pipe_small"),
            new Texture(Ref.ID, "block/pipe/pipe_normal"),
            new Texture(Ref.ID, "block/pipe/pipe_large"),
            new Texture(Ref.ID, "block/pipe/pipe_huge")
        }
    );

    public static final PipeType FLUID = new PipeType(
        "fluid_pipe", 0,
        //new Texture("block/pipe/wire_side"),
        new Texture(Ref.ID, "block/pipe/pipe_side"),
        new Texture[] {
            new Texture(Ref.ID, "block/pipe/pipe_vtiny"),
            new Texture(Ref.ID, "block/pipe/pipe_tiny"),
            new Texture(Ref.ID, "block/pipe/pipe_small"),
            new Texture(Ref.ID, "block/pipe/pipe_normal"),
            new Texture(Ref.ID, "block/pipe/pipe_large"),
            new Texture(Ref.ID, "block/pipe/pipe_huge")
        }
    );

    public static final PipeType WIRE = new PipeType(
        "wire", 1,
        new Texture(Ref.ID, "block/pipe/wire_side"),
        new Texture[] {
            new Texture(Ref.ID, "block/pipe/wire_side"),
            new Texture(Ref.ID, "block/pipe/wire_side"),
            new Texture(Ref.ID, "block/pipe/wire_side"),
            new Texture(Ref.ID, "block/pipe/wire_side"),
            new Texture(Ref.ID, "block/pipe/wire_side"),
            new Texture(Ref.ID, "block/pipe/wire_side")
        }
    );

    public static final PipeType CABLE = new PipeType(
        "cable", 2,
        new Texture(Ref.ID, "block/pipe/cable_side"),
        new Texture[] {
            new Texture(Ref.ID, "block/pipe/cable_vtiny"),
            new Texture(Ref.ID, "block/pipe/cable_tiny"),
            new Texture(Ref.ID, "block/pipe/cable_small"),
            new Texture(Ref.ID, "block/pipe/cable_normal"),
            new Texture(Ref.ID, "block/pipe/cable_large"),
            new Texture(Ref.ID, "block/pipe/cable_huge")
        }
    );

    private String id;
    private int modelId;
    private Texture side;
    private Texture[] faces;

    public PipeType(String id, int modelId, Texture side, Texture[] faces) {
        this.id = id;
        this.modelId = modelId;
        this.side = side;
        this.faces = faces;
        AntimatterAPI.register(PipeType.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public int getModelId() {
        return modelId;
    }

    public Texture[] getSide() {
        return new Texture[]{side};
    }

    public Texture getFace(PipeSize size) {
        return faces[size.ordinal()];
    }
}
