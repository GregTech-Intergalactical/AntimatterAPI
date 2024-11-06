package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.worldgen.IAntimatterWorldgenFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.Feature;

public interface IAntimatterFeature extends ISharedAntimatterObject, IAntimatterWorldgenFunction {
    Feature<?> asFeature();
}
