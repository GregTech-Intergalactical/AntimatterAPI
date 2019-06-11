package muramasa.gtu.api.tools;

import com.google.common.collect.Sets;
import muramasa.gtu.api.items.MaterialTool;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.int3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Set;

public class ToolJackhammer extends MaterialTool {

    public ToolJackhammer() {
        super(ToolType.JACKHAMMER);
    }

    @Override
    public Set<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        if (!player.isSneaking()) return Sets.newHashSet();
        RayTraceResult result = rayTrace(player.world, player, false);
        if (result.sideHit == null) return Sets.newHashSet();
        return Utils.getCubicPosArea(new int3(1, 1, 0), result.sideHit, origin, player, true);
    }
}
