package muramasa.antimatter.cover;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class CoverReplacements {
    private static final Map<Item, CoverFactory> REPLACEMENTS = new Object2ObjectOpenHashMap<>();

    public static void addReplacement(Item item, CoverFactory factory) {
        REPLACEMENTS.put(item, factory);
    }

    public static boolean hasReplacement(Item item) {
        return REPLACEMENTS.containsKey(item);
    }

    public static CoverFactory getReplacement(Item item) {
        return REPLACEMENTS.get(item);
    }
}
