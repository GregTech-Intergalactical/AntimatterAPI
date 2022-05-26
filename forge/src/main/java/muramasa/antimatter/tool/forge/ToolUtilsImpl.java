package muramasa.antimatter.tool.forge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ToolUtilsImpl {
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        Item.Properties properties = new Item.Properties().tab(group);
        if (!repairable) properties.setNoRepair();
        return properties;
    }
}
