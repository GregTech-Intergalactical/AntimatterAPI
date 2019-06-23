package muramasa.gtu.api.materials;

import muramasa.gtu.api.GregTechAPI;
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
            MaterialType type = GregTechAPI.get(MaterialType.class, name);
            if (type != null) flags.add(type);
            else if (EnumUtils.isValidEnum(RecipeFlag.class, name.toUpperCase())) flags.add(RecipeFlag.valueOf(name.toUpperCase()));
        }
        return flags;
    }
}
