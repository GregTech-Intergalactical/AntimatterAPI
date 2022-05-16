package muramasa.antimatter.util.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Predicate;

public class UtilsImpl {
    public static ItemStack insertItem(IItemHandler to, ItemStack toInsert, boolean simulate){
        return ItemHandlerHelper.insertItem(to, toInsert, simulate);
    }

    public static void requestModelDataRefresh(BlockEntity tile){
        ModelDataManager.requestModelDataRefresh(tile);
    }

    public static boolean isCorrectToolForDrops(BlockState state, Player player){
        return ForgeHooks.isCorrectToolForDrops(state, player);
    }

    public static int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer player, BlockPos pos){
        return ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
    }

    public static boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player){
        return state.canHarvestBlock(level, pos, player);
    }
}
