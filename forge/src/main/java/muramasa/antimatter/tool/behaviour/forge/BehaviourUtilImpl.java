package muramasa.antimatter.tool.behaviour.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class BehaviourUtilImpl {
    public static BlockState onToolUse(BlockState originalState, Level world, BlockPos pos, Player player, ItemStack stack, BehaviourToolAction action){
        return null;
    }

    public static boolean onUseHoe(UseOnContext context){
        UseHoeEvent hoeEvent = new UseHoeEvent(context);
        return MinecraftForge.EVENT_BUS.post(hoeEvent);
    }

    public static ToolAction getToolAction(BehaviourToolAction action){
        switch (action){
            case AXE_STRIP -> {
                return ToolActions.AXE_STRIP;
            }
            case HOE_DIG -> {
                return ToolActions.HOE_DIG;
            }
            case SHOVEL_DIG -> {
                return ToolActions.SHOVEL_DIG;
            }
            case SHOVEL_FLATTEN -> {
                return ToolActions.SHOVEL_FLATTEN;
            }
        }
    }
}
