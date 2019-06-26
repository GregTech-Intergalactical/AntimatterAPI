package muramasa.gtu.loaders;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.items.MaterialTool;
import muramasa.gtu.api.materials.MaterialType;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader {

    public static void init() {
        //Register materialItem entries (materialTypeMaterialName)
        GregTechAPI.all(MaterialItem.class).forEach(i -> {
            OreDictionary.registerOre(i.getType().oreName(i.getMaterial()), i);
            /*if (i.getType() == MaterialType.ROD) {
                OreDictionary.registerOre(i.getType().oreName(i.getMaterial()), i);
            }*/
        });

        //Register craftingTool entries (craftingToolType)
        GregTechAPI.all(MaterialTool.class).forEach(t -> OreDictionary.registerOre(t.getType().getOreDict(), t));
    }
}
