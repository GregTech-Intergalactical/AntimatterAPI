package muramasa.gregtech.api.interfaces;

import muramasa.gregtech.api.materials.Material;

import java.util.ArrayList;

public interface IMaterialFlag { //TODO rename to IMaterialFlag, rename MaterialFlag to ItemFlag

    void add(Material... mats);

    long getBit();

    ArrayList<Integer> getIds();

    Material[] getMats();

    static ArrayList<Integer> getIdsFor(IMaterialFlag... flags) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (IMaterialFlag flag : flags) {
            for (Integer i : flag.getIds()) {
                if (!ids.contains(i)) {
                    ids.add(i);
                }
            }
        }
        return ids;
    }

    static ArrayList<Material> getMatsFor(IMaterialFlag... flags) {
        ArrayList<Material> materials = new ArrayList<>();
        for (IMaterialFlag flag : flags) {
            for (Material mat : flag.getMats()) {
                if (!materials.contains(mat)) {
                    materials.add(mat);
                }
            }
        }
        return materials;
    }
}
