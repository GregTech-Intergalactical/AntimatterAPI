package muramasa.gtu.loaders;

import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.items.MaterialTool;
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader {

    public static void init() {
        //Register materialItem entries (prefixMaterialName)
        String[] prefix, mat;
        for (MaterialItem item : MaterialItem.getAll()) {
            prefix = item.getPrefix().getName().split("_");
            for (int i = 1; i < prefix.length; i++) {
                prefix[i] = prefix[i].substring(0, 1).toUpperCase() + prefix[i].substring(1);
            }
            mat = item.getMaterial().getName().split("_");
            for (int i = 0; i < mat.length; i++) {
                mat[i] = mat[i].substring(0, 1).toUpperCase() + mat[i].substring(1);
            }
            OreDictionary.registerOre(String.join("", prefix) + String.join("", mat), item);
        }

        //Register craftingTool entries (craftingToolType)
        MaterialTool tool;
        for (ToolType type : ToolType.values()) {
            tool = GregTechRegistry.getMaterialTool(type);
            OreDictionary.registerOre(type.getOreDict(), new ItemStack(tool, 1, OreDictionary.WILDCARD_VALUE));
        }
    }
}
