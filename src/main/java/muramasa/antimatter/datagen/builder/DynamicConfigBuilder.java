package muramasa.antimatter.datagen.builder;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Dir;
import muramasa.antimatter.util.Utils;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public class DynamicConfigBuilder {

    protected String parent;
    protected ImmutableMap<String, String> textures = ImmutableMap.of();
    protected int[] rotations = new int[0];
    protected ResourceLocation loader;

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
    @Nullable
    public ResourceLocation getLoader() {
        return loader;
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

    public DynamicConfigBuilder loader(AntimatterModelLoader<?> loader) {
        this.loader = loader.getLoc();
        return this;
    }

    public DynamicConfigBuilder rot(Direction dir) {
        switch (dir) {
            case NORTH:
                this.rotations = new int[]{0, 0, 0};
                break;
            case WEST:
                this.rotations = new int[]{0, 90, 0};
                break;
            case SOUTH:
                this.rotations = new int[]{0, 180, 0};
                break;
            case EAST:
                this.rotations = new int[]{0, 270, 0};
                break;
        }
        return this;
    }

    public DynamicConfigBuilder rot(Direction dir, Direction h) {
        this.rotations = Utils.rotationVector(dir, h);
        return this;
    }

    public boolean hasRots() {
        return rotations != null && rotations.length > 0;
    }
}
