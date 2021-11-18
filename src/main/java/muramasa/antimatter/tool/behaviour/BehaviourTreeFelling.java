package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviourTreeFelling implements IBlockDestroyed<IAntimatterTool> {

    public static final BehaviourTreeFelling INSTANCE = new BehaviourTreeFelling();

    @Override
    public String getId() {
        return "tree_felling";
    }

    @Override
    public boolean onBlockDestroyed(IAntimatterTool instance, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!AntimatterConfig.GAMEPLAY.AXE_TIMBER) return true;
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (Utils.isToolEffective(instance, state) && !player.isCrouching()) { // Only when player isn't shifting/crouching this ability activates
                if (state.getBlock().is(BlockTags.LOGS)) {
                    Utils.treeLogging(instance, stack, pos, player, world);
                }
            }
        }
        return true;
    }
}
