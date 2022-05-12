package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourAOEBreak implements IBlockDestroyed<IAntimatterTool> {

    protected int column, row, depth;

    public BehaviourAOEBreak(int column, int row, int depth) {
        if (column == 0 && row == 0) Utils.onInvalidData("BehaviourAOEBreak was set to break empty rows and columns!");
        this.column = column;
        this.row = row;
        this.depth = depth;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String getId() {
        return "aoe_break";
    }

    @Override
    public boolean onBlockDestroyed(IAntimatterTool instance, ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        //if(!super.onBlockDestroyed(stack, world, state, pos, entity)) return false;
        if (!(entity instanceof Player)) return true;
        Player player = (Player) entity;
        for (BlockPos blockPos : Utils.getHarvestableBlocksToBreak(world, player, instance, column, row, depth)) {
            if (!instance.hasEnoughDurability(stack, instance.getAntimatterToolType().getUseDurability(), instance.getAntimatterToolType().isPowered()))
                return true;
            if (!Utils.breakBlock(world, player, stack, blockPos, instance.getAntimatterToolType().getUseDurability()))
                break;
        }
        return true;
    }
}
