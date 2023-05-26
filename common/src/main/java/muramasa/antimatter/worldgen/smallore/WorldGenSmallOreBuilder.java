package muramasa.antimatter.worldgen.smallore;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static muramasa.antimatter.Ref.GSON;

public class WorldGenSmallOreBuilder {
    @Nullable
    private Material material;
    @Nullable
    private Integer amountPerChunk;
    @Nullable
    private Integer maxY;
    @Nullable
    private Integer minY;
    @Nullable String id;
    List<ResourceLocation> dimensions = new ArrayList<>();
    List<String> biomes = new ArrayList<>();
    boolean dimensionBlacklist = false, biomeBlacklist = true;

    public WorldGenSmallOreBuilder() {
    }

    final public WorldGenSmallOre buildMaterial() {
        if (this.amountPerChunk == null) {
            throw new RuntimeException("weight is required");
        }
        if (this.material == null) {
            throw new RuntimeException("material is required");
        }
        if (this.dimensions.isEmpty()) {
            this.dimensions.add(new ResourceLocation("overworld"));
        }
        WorldGenSmallOre smallOre =  new WorldGenSmallOre(
                id != null ? id : material.getId(),
                this.material,
                this.minY != null ? this.minY : Integer.MIN_VALUE,
                this.maxY != null ? this.maxY : Integer.MAX_VALUE,
                amountPerChunk,
                this.dimensions,
                this.biomes,
                this.biomeBlacklist
        );
        AntimatterWorldGenerator.writeJson(smallOre.toJson(), smallOre.getId(), "small_ore");
        return AntimatterWorldGenerator.readJson(WorldGenSmallOre.class, smallOre, WorldGenSmallOre::fromJson, "small_ore");
    }



    private WorldGenVein readJson(WorldGenVein original){
        File dir = new File(AntimatterPlatformUtils.getConfigDir().toFile(), "antimatter/small_ore/overrides");
        File target = new File(dir, id + ".json");


        if(target.exists()) {
            try {
                Reader reader = Files.newBufferedReader(target.toPath());
                JsonObject parsed = JsonParser.parseReader(reader).getAsJsonObject();
                WorldGenVein read = WorldGenVein.fromJson(this.id, parsed);
                reader.close();
                return read;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return original;
    }

    final public WorldGenSmallOreBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    final public WorldGenSmallOreBuilder withAmountPerChunk(int amountPerChunk) {
        this.amountPerChunk = amountPerChunk;
        return this;
    }

    final public WorldGenSmallOreBuilder atHeight(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }

    final public WorldGenSmallOreBuilder withCustomId(String id){
        this.id = id;
        return this;
    }

    final public WorldGenSmallOreBuilder withBiomes(String... biomes) {
        Collections.addAll(this.biomes, biomes);
        return this;
    }

    final public WorldGenSmallOreBuilder withDimensions(ResourceLocation... dimensions) {
        Collections.addAll(this.dimensions, dimensions);
        return this;
    }

    final public WorldGenSmallOreBuilder setBiomeBlacklist(boolean blacklist) {
        this.biomeBlacklist = blacklist;
        return this;
    }
}
