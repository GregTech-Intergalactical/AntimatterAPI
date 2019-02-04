package muramasa.gregtech.api.interfaces;

import muramasa.gregtech.api.materials.Material;

public interface IMaterialFlag { //TODO rename to IMaterialFlag, rename MaterialFlag to ItemFlag

    void add(Material... mats);

    long getBit();

    Material[] getMats();
}
