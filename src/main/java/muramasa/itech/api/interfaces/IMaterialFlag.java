package muramasa.itech.api.interfaces;

import muramasa.itech.api.materials.Materials;

public interface IMaterialFlag { //TODO rename to IMaterialFlag, rename MaterialFlag to ItemFlag

    void add(Materials... mats);

    int getMask();

    Materials[] getMats();
}
