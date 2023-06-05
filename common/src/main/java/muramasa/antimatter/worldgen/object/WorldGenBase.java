package muramasa.antimatter.worldgen.object;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldGenBase<T extends WorldGenBase<?>> implements IAntimatterObject {

    private final String id;
    private boolean enabled = true;
    private final Set<ResourceLocation> dimensions;
    private boolean custom;
    public final Class<? extends WorldGenBase<?>> toRegister;

    @SafeVarargs
    public WorldGenBase(String id, Class<? extends WorldGenBase<?>> c, ResourceKey<Level>... dimensions) {
        this.id = id;
        this.dimensions = Arrays.stream(dimensions).map(ResourceKey::location).collect(Collectors.toCollection(ObjectOpenHashSet::new));
        this.toRegister = c;
    }

    public WorldGenBase(String id, Class<? extends WorldGenBase<?>> c, List<ResourceKey<Level>> dimensions) {
        this.id = id;
        this.dimensions = dimensions.stream().map(ResourceKey::location).collect(Collectors.toCollection(ObjectOpenHashSet::new));
        this.toRegister = c;
        //AntimatterWorldGenerator.register(c, this);
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<ResourceLocation> getDims() {
        return dimensions;
    }

    public boolean isCustom() {
        return custom;
    }

    public WorldGenBase<T> asCustom() {
        this.custom = true;
        return this;
    }

    public WorldGenBase<T> onDataOverride(JsonObject json) {
        if (json.has("enabled")) enabled = json.get("enabled").getAsBoolean();
        return this;
    }

    public WorldGenBase<T> build() {
        if (dimensions == null) throw new IllegalStateException("WorldGenBase - " + id + ": dimensions cannot be null");
        return this;
    }

    public Predicate<Holder<Biome>> getValidBiomes() {
        return b -> true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldGenBase)) return false;
        WorldGenBase<?> other = (WorldGenBase<?>) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
