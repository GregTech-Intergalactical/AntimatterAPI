package muramasa.gtu.api.materials;

import muramasa.gtu.api.GregTechAPI;

import java.util.ArrayList;
import java.util.Set;

public interface IMaterialFlag {

    long getBit();

    Set<Material> getMats();

    default void add(Material... m) {
        for (int i = 0; i < m.length; i++) getMats().add(m[i]);
    }

    default void remove(Material... m) {
        for (int i = 0; i < m.length; i++) getMats().remove(m[i]);
    }

    static ArrayList<Material> getMatsFor(IMaterialFlag... flags) {
        ArrayList<Material> materials = new ArrayList<>();
        for (IMaterialFlag flag : flags) {
            for (Material m : flag.getMats()) {
                if (!materials.contains(m)) {
                    materials.add(m);
                }
            }
        }
        return materials;
    }

    static ArrayList<IMaterialFlag> getFlagsFor(String... flagNames) {
        ArrayList<IMaterialFlag> flags = new ArrayList<>();
        for (String name : flagNames) {
            MaterialType type = GregTechAPI.get(MaterialType.class, name);
            if (type != null) flags.add(type);
            MaterialTag tag = GregTechAPI.get(MaterialTag.class, name);
            if (tag != null) flags.add(tag);
        }
        return flags;
    }
}
