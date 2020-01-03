package muramasa.antimatter.materials;

import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.registration.IGregTechObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IMaterialTag {

    Set<Material> all();

    default void register(Class c, IGregTechObject o) {
        GregTechAPI.register(c, o);
        GregTechAPI.register(IMaterialTag.class, o);
    }

    default void add(Material... m) {
        for (int i = 0; i < m.length; i++) {
            all().add(m[i]);
        }
    }

    default void remove(Material... m) {
        for (int i = 0; i < m.length; i++) {
            all().remove(m[i]);
        }
    }

    static Set<Material> all(IMaterialTag... tags) {
        Set<Material> materials = new HashSet<>();
        Arrays.stream(tags).forEach(t -> materials.addAll(t.all()));
        return materials;
    }

    static Set<IMaterialTag> getTags(String... ids) {
        Set<IMaterialTag> tags = new HashSet<>();
        for (String id : ids) {
            IMaterialTag t = GregTechAPI.get(IMaterialTag.class, id);
            if (t != null) tags.add(t);
        }
        return tags;
    }
}
