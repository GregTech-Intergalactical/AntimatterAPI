package muramasa.antimatter.datagen.builder;

import muramasa.antimatter.texture.Texture;
import net.minecraft.util.Direction;

public class DynamicConfigBuilder {

    protected AntimatterBlockModelBuilder modelBuilder;
    protected int id;
    protected String parent;
    protected Texture[] textures;
    protected Direction[] rotations;

    public DynamicConfigBuilder(AntimatterBlockModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    public DynamicConfigBuilder model(int id, String parent) {
        this.id = id;
        this.parent = parent;
        return this;
    }

    public DynamicConfigBuilder tex(Texture... textures) {
        this.textures = textures;
        return this;
    }

    public DynamicConfigBuilder rot(Direction... rotations) {
        this.rotations = rotations;
        return this;
    }
}
