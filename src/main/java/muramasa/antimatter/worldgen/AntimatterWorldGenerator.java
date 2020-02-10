package muramasa.antimatter.worldgen;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.feature.IAntimatterFeature;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;

import java.util.*;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    public static Map<BlockState, BlockState> STATES_TO_PURGE = new HashMap<>();

    public static void init() {
        try {
            AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
            AntimatterAPI.all(IAntimatterFeature.class).stream().filter(IAntimatterFeature::enabled).forEach(IAntimatterFeature::init);
            WorldGenHelper.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
    }

    public static void register(Class<?> c, WorldGenBase<?> base) {
        IAntimatterFeature feature = AntimatterAPI.get(IAntimatterFeature.class, c.getName());
        if (feature != null) {
            base.getDims().forEach(d -> feature.getRegistry().computeIfAbsent((int) d, k -> new LinkedList<>()).add(base.build()));
        }
    }

    public static <T> List<T> all(Class<T> c, int dim) {
        IAntimatterFeature feat = AntimatterAPI.get(IAntimatterFeature.class, c.getName());
        return feat != null ? feat.getRegistry().getOrDefault(dim, new LinkedList<>()).stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }
}