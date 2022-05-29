package muramasa.antimatter.tool.forge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;

public class ToolUtilsImpl {
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        Item.Properties properties = new Item.Properties().tab(group);
        if (!repairable) properties.setNoRepair();
        return properties;
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state){
        return TierSortingRegistry.isCorrectTierForDrops(tier, state);
    }
}
