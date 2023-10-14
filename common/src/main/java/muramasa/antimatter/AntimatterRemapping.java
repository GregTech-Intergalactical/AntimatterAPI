package muramasa.antimatter;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AntimatterRemapping {
    private static final Map<String, Map<String, ResourceLocation>> REMAPPING_MAP = new Object2ObjectArrayMap<>();

    private static final Map<ResourceLocation, ResourceLocation> BE_REMAPPING_MAP = new Object2ObjectArrayMap<>();

    private static final List<Function<ResourceLocation, ResourceLocation>> BE_REMAPPING_LIST = new ArrayList<>();

    private static final Map<ResourceLocation, ResourceLocation> COVER_REMAPPING_MAP = new Object2ObjectArrayMap<>();

    static {
        BE_REMAPPING_LIST.add(r -> {
            if (BE_REMAPPING_MAP.containsKey(r)){
                return BE_REMAPPING_MAP.get(r);
            }
            if (r.getNamespace().equals(Ref.SHARED_ID)){
                if (r.getPath().startsWith("fluid_")){
                    return new ResourceLocation(r.getNamespace(), r.getPath().replace("fluid_", "fluid_pipe_"));
                }
                if (r.getPath().startsWith("item_")){
                    return new ResourceLocation(r.getNamespace(), r.getPath().replace("item_", "item_pipe_"));
                }
            }
            return null;
        });
    }

    public static void remapMachine(ResourceLocation old, Machine<?> machine){
        BE_REMAPPING_MAP.put(old, machine.getLoc());
        machine.getTiers().forEach(t -> {
            REMAPPING_MAP.computeIfAbsent(old.getNamespace(), o -> new Object2ObjectArrayMap<>())
                    .put(old.getPath() + "_" + t.getId(), new ResourceLocation(machine.getDomain(), machine.getId() + "_" + t.getId()));
        });
    }

    public static void remapMachine(String old, Machine<?> machine){
        BE_REMAPPING_MAP.put(new ResourceLocation(machine.getDomain(), old), machine.getLoc());
        machine.getTiers().forEach(t -> {
            REMAPPING_MAP.computeIfAbsent(machine.getDomain(), o -> new Object2ObjectArrayMap<>())
                    .put(old + "_" + t.getId(), new ResourceLocation(machine.getDomain(), machine.getId() + "_" + t.getId()));
        });
    }

    public static void remap(String modid, String oldId, String newId){
        REMAPPING_MAP.computeIfAbsent(modid, o -> new Object2ObjectArrayMap<>()).put(oldId, new ResourceLocation(modid, newId));
    }

    public static void remap(ResourceLocation oldId, ResourceLocation newId){
        REMAPPING_MAP.computeIfAbsent(oldId.getNamespace(), o -> new Object2ObjectArrayMap<>()).put(oldId.getPath(), newId);
    }

    public static void remapBlockEntity(ResourceLocation oldId, ResourceLocation newId){
        BE_REMAPPING_MAP.put(oldId, newId);
    }

    public static void remapCover(ResourceLocation oldId, ResourceLocation newId){
        COVER_REMAPPING_MAP.put(oldId, newId);
    }

    public static Map<ResourceLocation, ResourceLocation> getBeRemappingMap() {
        return BE_REMAPPING_MAP;
    }

    public static Map<ResourceLocation, ResourceLocation> getCoverRemappingMap() {
        return COVER_REMAPPING_MAP;
    }

    public static Map<String, Map<String, ResourceLocation>> getRemappingMap() {
        return REMAPPING_MAP;
    }

    public static List<Function<ResourceLocation, ResourceLocation>> getBeRemappingFunctionList() {
        return BE_REMAPPING_LIST;
    }
}
