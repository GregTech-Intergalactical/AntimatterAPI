package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.registration.ISharedAntimatterObject;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public interface IAntimatterFeature extends ISharedAntimatterObject {
    Feature<?> asFeature();

   void build(BiomeLoadingEvent ev);
}
