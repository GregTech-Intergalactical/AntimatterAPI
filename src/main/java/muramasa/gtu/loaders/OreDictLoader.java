package muramasa.gtu.loaders;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.items.MaterialItem;
import muramasa.gtu.api.items.MaterialTool;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader {

    public static void init() {
        //Register materialItem entries (prefixMaterialName)
        GregTechAPI.all(MaterialItem.class).forEach(i -> OreDictionary.registerOre(i.getPrefix().oreName(i.getMaterial()), i));

        //Register craftingTool entries (craftingToolType)
        GregTechAPI.all(MaterialTool.class).forEach(t -> OreDictionary.registerOre(t.getType().getOreDict(), new ItemStack(t, 1, OreDictionary.WILDCARD_VALUE)));
    }
}
