package muramasa.antimatter.registration.forge;

import com.mojang.serialization.Codec;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class AntimatterBiomeModifier implements BiomeModifier {
    public static final Codec<AntimatterBiomeModifier> CODEC = Codec.unit(AntimatterBiomeModifier::new);
    @Override
    public void modify(Holder<Biome> arg, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD){
            AntimatterWorldGenerator.reloadEvent(arg.unwrapKey().get().location(), builder.getClimateSettings().build(), builder.getSpecialEffects().build(), builder.getGenerationSettings(), builder.getMobSpawnSettings());
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
