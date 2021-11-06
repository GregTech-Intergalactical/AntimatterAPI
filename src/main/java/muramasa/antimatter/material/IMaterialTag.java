package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.ISharedAntimatterObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public interface IMaterialTag extends ISharedAntimatterObject {

    //TODO use a static id -> material set map, instead of each IMaterialTag class having it's own collection
    Set<Material> all();

    default void register(Class<?> c, String id) {
        AntimatterAPI.register(c, this);
        AntimatterAPI.register(IMaterialTag.class, this);
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

    default Set<IMaterialTag> dependents() {
        return Collections.emptySet();
    }
}
