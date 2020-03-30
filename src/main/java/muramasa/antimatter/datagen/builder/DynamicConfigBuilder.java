package muramasa.antimatter.datagen.builder;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.texture.Texture;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class DynamicConfigBuilder {

    protected String parent;
    protected ImmutableMap<String, String> textures = ImmutableMap.of();
    protected int[] rotations = new int[0];

    public DynamicConfigBuilder() {

    }

    public DynamicConfigBuilder(String parent) {
        this.parent = parent;
    }

    public DynamicConfigBuilder setTextures(ImmutableMap<String, String> map) {
        this.textures = map;
        return this;
    }

    public DynamicConfigBuilder[] list(DynamicConfigBuilder... builders) {
        return builders;
    }

    public DynamicConfigBuilder of(ResourceLocation loc) {
        return of(loc.toString());
    }

    public DynamicConfigBuilder of(String parent) {
        return new DynamicConfigBuilder(parent);
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

    public boolean hasRots() {
        return rotations != null && rotations.length > 0;
    }
}
