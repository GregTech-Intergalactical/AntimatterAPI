package muramasa.antimatter.tool.behaviour.fabric;

import muramasa.antimatter.tool.behaviour.BehaviourUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourUtilImpl {
    //TODO make this use fabric events, if they exist
    public static BlockState onToolUse(BlockState originalState, Level world, BlockPos pos, Player player, ItemStack stack, String action){
        return originalState;
    }

    public static boolean onUseHoe(UseOnContext context){
        return false;
    }
}
