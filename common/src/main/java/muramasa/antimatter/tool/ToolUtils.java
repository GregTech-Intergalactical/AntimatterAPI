package muramasa.antimatter.tool;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public class ToolUtils {
    @ExpectPlatform
    public static Item.Properties getToolProperties(CreativeModeTab group, boolean repairable){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCorrectTierForDrops(Tier tier, BlockState state){
        throw new AssertionError();
    }
}
