package muramasa.gtu.api.materials;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface IMaterialTag {

    Set<Material> getMats();

    default void register(Class c, IGregTechObject o) {
        GregTechAPI.register(c, o);
        GregTechAPI.register(IMaterialTag.class, o);
    }

    default void add(Material... m) {
        for (int i = 0; i < m.length; i++) {
            if (!getMats().contains(m[i])) {
                getMats().add(m[i]);
                m[i].add(this);
            }
        }
    }

    default void remove(Material... m) {
        for (int i = 0; i < m.length; i++) {
            getMats().remove(m[i]);
            m[i].add(this);
        }
    }

    static Set<Material> getMats(IMaterialTag... tags) {
        Set<Material> materials = new HashSet<>();
        Arrays.stream(tags).forEach(t -> materials.addAll(t.getMats()));
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
