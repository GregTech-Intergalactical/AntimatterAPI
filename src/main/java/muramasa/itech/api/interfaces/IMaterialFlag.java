package muramasa.itech.api.interfaces;

import muramasa.itech.api.materials.Material;

public interface IMaterialFlag { //TODO rename to IMaterialFlag, rename MaterialFlag to ItemFlag

    void add(Material... mats);

    int getMask();

    Material[] getMats();
}
