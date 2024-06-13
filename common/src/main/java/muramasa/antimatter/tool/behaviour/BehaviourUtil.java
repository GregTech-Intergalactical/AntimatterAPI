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
    public static BlockState onToolUse(BlockState originalState, UseOnContext context, String action){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean onUseHoe(UseOnContext context){
        throw new AssertionError();
    }
}
