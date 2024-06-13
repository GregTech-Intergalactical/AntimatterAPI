package muramasa.antimatter.tool.behaviour.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class BehaviourUtilImpl {
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, String action){
        return ForgeEventFactory.onToolUse(originalState, context, ToolAction.get(action), false);
    }

    public static boolean onUseHoe(UseOnContext context){
        return MinecraftForge.EVENT_BUS.post(new UseHoeEvent(context));
    }
}
