package muramasa.antimatter.tool.fabric;

import muramasa.antimatter.tool.IAbstractToolMethods;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ToolUtilsImpl {
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        FabricItemSettings properties = new FabricItemSettings().group(group);
        properties.customDamage(IAbstractToolMethods::damageItemStatic);
        return properties;
    }
}
