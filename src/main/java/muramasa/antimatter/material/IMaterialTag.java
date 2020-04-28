package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;

import java.util.Arrays;
import java.util.Set;

public interface IMaterialTag extends IAntimatterObject {

    //TODO use a static id -> material set map, instead of each IMaterialTag class having it's own collection
    Set<Material> all();

    default void register(Class<?> c, String id) {
        AntimatterAPI.register(c, id, this);
        AntimatterAPI.register(IMaterialTag.class, id, this);
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
        Set<Material> materials = new ObjectOpenHashSet<>();
        Arrays.stream(tags).forEach(t -> materials.addAll(t.all()));
        return materials;
    }
}
