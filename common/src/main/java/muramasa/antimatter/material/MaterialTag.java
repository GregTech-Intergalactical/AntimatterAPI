package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MaterialTag implements IMaterialTag {

    //TODO get alloy flag for adding mixer and dust crafting recipes automatically

    private final String id;
    private final Set<Material> materials = new ObjectLinkedOpenHashSet<>();
    private final Map<SubTag, Set<Material>> TAG_MAP = new Object2ObjectOpenHashMap<>();

    public MaterialTag(String id) {
        this.id = id + "_tag";
        register(MaterialTag.class, id + "_tag");
    }

    public MaterialTag subTag(SubTag tag, Material... mats) {
        Set<Material> set = TAG_MAP.computeIfAbsent(tag, k -> new ObjectOpenHashSet<>());
        set.addAll(Arrays.asList(mats));
        return this;
    }

    public Set<Material> allSub(SubTag sub) {
        return TAG_MAP.getOrDefault(sub, Collections.emptySet());
    }

    public boolean has(SubTag tag, Material mat) {
        return TAG_MAP.getOrDefault(tag, Collections.emptySet()).contains(mat);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<Material> all() {
        return materials;
    }
}
