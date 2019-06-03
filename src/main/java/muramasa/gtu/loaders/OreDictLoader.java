package muramasa.gtu.loaders;

import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.tools.MaterialTool;
import muramasa.gtu.api.tools.ToolType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictLoader {

    public static void init() {
        //Register craftingTool entries
        MaterialTool tool;
        for (ToolType type : ToolType.values()) {
            tool = GregTechRegistry.getMaterialTool(type);
            OreDictionary.registerOre(type.getOreDict(), new ItemStack(tool, 1, OreDictionary.WILDCARD_VALUE));
        }
    }
}
