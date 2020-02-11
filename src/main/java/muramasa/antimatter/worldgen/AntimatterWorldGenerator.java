package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.feature.AntimatterFeature;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;

import java.util.*;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    public static Map<BlockState, BlockState> STATES_TO_PURGE = new HashMap<>();

    public static void init() {
        try {
            AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
            AntimatterAPI.all(AntimatterFeature.class).stream().filter(AntimatterFeature::enabled).forEach(feat -> {
                feat.onDataOverride(new JsonObject());
                feat.init();
            });
            WorldGenHelper.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
    }

    public static void register(Class<?> c, WorldGenBase<?> base) {
        AntimatterFeature<?> feature = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        if (feature != null) base.getDims().forEach(d -> feature.getRegistry().computeIfAbsent((int) d, k -> new LinkedList<>()).add(base));
    }

    public static <T> List<T> all(Class<T> c, int dim) {
        AntimatterFeature<?> feat = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        return feat != null ? feat.getRegistry().computeIfAbsent(dim, k -> new LinkedList<>()).stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }
}