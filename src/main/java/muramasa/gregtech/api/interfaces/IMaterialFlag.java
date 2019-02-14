package muramasa.gregtech.api.interfaces;

import muramasa.gregtech.api.materials.Material;

import java.util.ArrayList;

public interface IMaterialFlag { //TODO rename to IMaterialFlag, rename MaterialFlag to ItemFlag

    String getName();

    void add(Material... mats);

    long getBit();

    ArrayList<Material> getMats();

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
