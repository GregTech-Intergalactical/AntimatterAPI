package muramasa.antimatter.tool.behaviour;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourUtil {
    @ExpectPlatform
    public static BlockState onToolUse(BlockState originalState, Level world, BlockPos pos, Player player, ItemStack stack, BehaviourToolAction action){
        return null;
    }

    @ExpectPlatform
    public static boolean onUseHoe(UseOnContext context){
        return false;
    }

    public enum BehaviourToolAction {
        HOE_DIG,
        AXE_STRIP,
        SHOVEL_FLATTEN,
        SHOVEL_DIG
    }
}