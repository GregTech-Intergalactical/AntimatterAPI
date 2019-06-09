package muramasa.gtu.api.registration;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.loaders.InternalRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class GregTechRegistry {

    public static Set<Item> ITEMS = new LinkedHashSet<>();
    public static Set<Block> BLOCKS = new LinkedHashSet<>();
    public static Set<Class> TILES = new LinkedHashSet<>();
    public static HashMap<String, LinkedHashMap<String, IGregTechObject>> OBJECTS = new HashMap<>();

    public static void register(Object o) {
        if (o instanceof Item) ITEMS.add((Item) o);
        else if (o instanceof Block) BLOCKS.add((Block) o);
        else if (o instanceof Class) TILES.add((Class) o);
    }

    public static void register(Class c, IGregTechObject o) {
        if (!OBJECTS.containsKey(c.getName())) OBJECTS.put(c.getName(), new LinkedHashMap<>());
        OBJECTS.get(c.getName()).put(o.getId(), o);
        register(o);
    }

    public static <T> T get(Class<T> c, String name) {
        return (T) OBJECTS.get(c.getName()).get(name);
    }

    public static <T> List<T> all(Class<T> c) {
        return OBJECTS.get(c.getName()).values().stream().map(c::cast).collect(Collectors.toList());
    }

    /** Registrar Section **/
    private static final IGregTechRegistrar INTERNAL_REGISTRAR = new InternalRegistrar();

    public static final HashMap<String, IGregTechRegistrar> REGISTRARS = new HashMap<>();

    public static void addRegistrar(IGregTechRegistrar registrar) {
        if (registrar.isEnabled() || Ref.ENABLE_ALL_REGISTRARS) REGISTRARS.put(registrar.getId(), registrar);
    }

    public static void callRegistrationEvent(RegistrationEvent event) {
        INTERNAL_REGISTRAR.onRegistrationEvent(event);
        REGISTRARS.values().forEach(r -> r.onRegistrationEvent(event));
    }

    public static boolean isRegistrarEnabled(String id) {
        IGregTechRegistrar registrar = getRegistrar(id);
        return registrar != null && registrar.isEnabled();
    }

    @Nullable
    public static IGregTechRegistrar getRegistrar(String id) {
        return REGISTRARS.get(id);
    }

    public static BlockStone getStone(StoneType type) {
        return (BlockStone) getBlock(Ref.MODID, "stone_" + type.getId());
    }

    public static Item getItem(String domain, String path) {
        return Item.getByNameOrId(new ResourceLocation(domain, path).toString());
    }

    public static Block getBlock(String domain, String path) {
        return Block.getBlockFromName(new ResourceLocation(domain, path).toString());
    }
}
