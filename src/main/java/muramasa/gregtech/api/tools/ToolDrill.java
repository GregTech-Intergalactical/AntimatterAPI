package muramasa.gregtech.api.tools;

import com.google.common.collect.Sets;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.api.util.int3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Set;

public class ToolDrill extends MaterialTool {

    public ToolDrill() {
        super(ToolType.DRILL);
    }

    @Override
    public Set<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        if (!player.isSneaking()) return Sets.newHashSet();
        RayTraceResult result = rayTrace(player.world, player, false);
        if (result == null || result.sideHit == null) return Sets.newHashSet();
        return Utils.getCubicPosArea(new int3(1, 1, 0), result.sideHit, origin, player, true);
    }
}
