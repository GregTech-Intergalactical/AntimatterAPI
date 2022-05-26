package muramasa.antimatter.tool.fabric;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ToolUtilsImpl {
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        FabricItemSettings properties = new FabricItemSettings().group(group);
        if (!repairable) properties.setNoRepair();
        return properties;
    }
}
