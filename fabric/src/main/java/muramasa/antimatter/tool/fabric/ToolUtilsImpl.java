package muramasa.antimatter.tool.fabric;

import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;
import muramasa.antimatter.tool.IAbstractToolMethods;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public class ToolUtilsImpl {
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        FabricItemSettings properties = new FabricItemSettings().group(group);
        properties.customDamage(IAbstractToolMethods::damageItemStatic);
        return properties;
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state){
        return TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }
}
