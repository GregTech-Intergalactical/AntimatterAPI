package muramasa.antimatter.util.fabric;

import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class UtilsImpl {
    public static ItemStack insertItem(IItemHandler to, ItemStack toInsert, boolean simulate){
        return ItemHandlerHelper.insertItem(to, toInsert, simulate);
    }

    public static void requestModelDataRefresh(BlockEntity tile){
        ModelDataManager.requestModelDataRefresh(tile);
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
    /*public static boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player){
        return state.canHarvestBlock(level, pos, player);
    }*/
}
