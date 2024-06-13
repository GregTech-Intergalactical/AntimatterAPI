package muramasa.antimatter.tool.behaviour.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourUtilImpl {
    //TODO make this use fabric events, if they exist
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, String action){
        return originalState;
    }

    public static boolean onUseHoe(UseOnContext context){
        return false;
    }
}
