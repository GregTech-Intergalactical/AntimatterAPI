package muramasa.antimatter.machine;

import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tool.MaterialTool;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static muramasa.antimatter.Data.HAMMER;

public class BlockMultiMachine extends BlockMachine {

    public BlockMultiMachine(Machine<?> type, Tier tier) {
        super(type, tier);
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            if (player.getHeldItem(hand).getItem() instanceof MaterialTool && ((MaterialTool) player.getHeldItem(hand).getItem()).getType() == HAMMER) {
                TileEntityMultiMachine machine = (TileEntityMultiMachine) world.getTileEntity(pos);
                if (!machine.isStructureValid()) {
                    machine.checkStructure();
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }
}
