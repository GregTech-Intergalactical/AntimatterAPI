package muramasa.antimatter.util.fabric;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.BlockAccessor;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UtilsImpl {

    public static void popExperience(Block block, ServerLevel level, BlockPos pos, int exp){
        ((BlockAccessor)block).port_lib$popExperience(level, pos, exp);
    }

    public static void requestModelDataRefresh(BlockEntity tile){
    }

    public static boolean isCorrectToolForDrops(BlockState state, Player player){
        if (!state.requiresCorrectToolForDrops()){
            //TODO
            //return ForgeEventFactory.doPlayerHarvestCheck(player, state, true);
        }

        return player.hasCorrectToolForDrops(state);
    }

    public static int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer entityPlayer, BlockPos pos)
    {
        return PortingHooks.onBlockBreakEvent(level, gameType, entityPlayer, pos);
    }

    //TODO
    public static boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player){
        return isCorrectToolForDrops(state, player);
        //return state.canHarvestBlock(level, pos, player);
    }
}
