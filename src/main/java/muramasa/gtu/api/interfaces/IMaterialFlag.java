package muramasa.gtu.api.interfaces;

import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.RecipeFlag;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;

public interface IMaterialFlag {

    String getName();

    void add(Material... mats);

    void remove(Material... mats);

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

    static ArrayList<IMaterialFlag> getFlagsFor(String... flagNames) {
        ArrayList<IMaterialFlag> flags = new ArrayList<>();
        for (String name : flagNames) {
            if (EnumUtils.isValidEnum(GenerationFlag.class, name.toUpperCase())) flags.add(GenerationFlag.valueOf(name.toUpperCase()));
            else if (EnumUtils.isValidEnum(RecipeFlag.class, name.toUpperCase())) flags.add(RecipeFlag.valueOf(name.toUpperCase()));
        }
        return flags;
    }
}
