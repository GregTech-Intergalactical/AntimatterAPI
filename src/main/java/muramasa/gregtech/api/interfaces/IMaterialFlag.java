package muramasa.gregtech.api.interfaces;

import muramasa.gregtech.api.materials.Material;

import java.util.ArrayList;

public interface IMaterialFlag { //TODO rename to IMaterialFlag, rename MaterialFlag to ItemFlag

    String getName();

    void add(Material... mats);

    long getBit();

    ArrayList<String> getMatNames();

    Material[] getMats();

    static ArrayList<String> getNamesFor(IMaterialFlag... flags) {
        ArrayList<String> names = new ArrayList<>();
        for (IMaterialFlag flag : flags) {
            for (String name : flag.getMatNames()) {
                if (!names.contains(name)) {
                    names.add(name);
                }
            }
        }
        return names;
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
