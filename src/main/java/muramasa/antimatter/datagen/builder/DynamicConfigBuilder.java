package muramasa.antimatter.datagen.builder;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.texture.Texture;

import java.util.function.Function;

public class DynamicConfigBuilder {

    protected AntimatterBlockModelBuilder modelBuilder;
    protected int id;
    protected String parent;
    protected ImmutableMap<String, String> textures;
    protected int[] rotations;

    public DynamicConfigBuilder(AntimatterBlockModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    public DynamicConfigBuilder model(int id, String parent) {
        this.id = id;
        this.parent = parent;
        return this;
    }

    public DynamicConfigBuilder tex(Texture... textures) {
        this.textures = AntimatterBlockModelBuilder.buildTextures(textures);
        return this;
    }

    public DynamicConfigBuilder tex(Function<ImmutableMap.Builder<String, Texture>, ImmutableMap.Builder<String, Texture>> func) {
        this.textures = AntimatterBlockModelBuilder.buildTextures(func.apply(new ImmutableMap.Builder<>()).build());
        return this;
    }

    public DynamicConfigBuilder tex(ImmutableMap<String, Texture> map) {
        this.textures = AntimatterBlockModelBuilder.buildTextures(map);
        return this;
    }

    public DynamicConfigBuilder rot(int x, int y, int z) {
        this.rotations = new int[]{x, y, z};
        return this;
    }
}
