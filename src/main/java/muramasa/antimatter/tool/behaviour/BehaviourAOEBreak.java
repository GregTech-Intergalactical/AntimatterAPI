package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.tool.MaterialTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviourAOEBreak implements IBlockDestroyed<MaterialTool> {

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
    public boolean onBlockDestroyed(MaterialTool instance, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        //if(!super.onBlockDestroyed(stack, world, state, pos, entity)) return false;
        if (!(entity instanceof PlayerEntity)) return true;
        PlayerEntity player = (PlayerEntity) entity;
        for (BlockPos blockPos : Utils.getHarvestableBlocksToBreak(world, player, instance, column, row, depth)) {
            if (!instance.enoughDurability(stack, instance.getType().getUseDurability(), instance.getType().isPowered())) return true;
            if (!Utils.breakBlock(world, player, stack, blockPos, instance.getType().getUseDurability())) break;
        }
        return true;
    }
}
