package muramasa.antimatter.tool;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public class ToolUtils {
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        return AntimatterPlatformUtils.getToolProperties(group, repairable);
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state){
        return AntimatterPlatformUtils.isCorrectTierForDrops(tier, state);
    }
}
