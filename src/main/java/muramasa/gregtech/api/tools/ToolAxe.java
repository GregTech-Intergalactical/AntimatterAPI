package muramasa.gregtech.api.tools;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.enums.ToolType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class ToolAxe extends MaterialTool {

    public ToolAxe() {
        super(ToolType.AXE);
    }

    @Override
    public Set<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        if (Ref.AXE_TIMBER) {
            Set<BlockPos> set = new HashSet<>();
            BlockPos tempPos;
            IBlockState state;
            for (int y = origin.getY() + 1; y < origin.getY() + Ref.MAX_AXE_TIMBER; y++) {
                tempPos = new BlockPos(origin.getX(), y, origin.getZ());
                state = world.getBlockState(tempPos);
                if (world.getBlockState(tempPos).getBlock().isAir(state, world, tempPos)) break;
                set.add(tempPos);
            }
            return set;
//            return Utils.getCubicPosArea(new int3(0, Ref.MAX_AXE_TIMBER / 2, 0), null, origin, player, true);
        }
        return super.getAOEBlocks(stack, world, player, origin);
    }
}
