package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.config.AntimatterConfig;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourTreeFelling implements IBlockDestroyed<IAntimatterTool> {

    public static final BehaviourTreeFelling INSTANCE = new BehaviourTreeFelling();

    @Override
    public String getId() {
        return "tree_felling";
    }

    @Override
    public boolean onBlockDestroyed(IAntimatterTool instance, ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!AntimatterConfig.GAMEPLAY.AXE_TIMBER) return true;
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (Utils.isToolEffective(instance, state) && !player.isCrouching()) { // Only when player isn't shifting/crouching this ability activates
                if (state.is(BlockTags.LOGS)) {
                    Utils.treeLogging(instance, stack, pos, player, world);
                }
            }
        }
        return true;
    }
}
