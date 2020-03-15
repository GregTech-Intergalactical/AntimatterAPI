package muramasa.antimatter.tools.behaviour;

import muramasa.antimatter.Configs;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.tools.base.AntimatterToolType;
import muramasa.antimatter.tools.base.MaterialTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static muramasa.antimatter.Data.AXE;
import static muramasa.antimatter.Data.CHAINSAW;

public class BehaviourTreeFelling implements IBlockDestroyed<MaterialTool> {

    @Override
    public String getId() {
        return "tree_felling";
    }

    @Override
    public boolean onBlockDestroyed(MaterialTool instance, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            AntimatterToolType type = instance.getType();
            boolean isToolEffective = Utils.isToolEffective(type, state);
            if (isToolEffective && !player.isCrouching()) { // Only when player isn't shifting/crouching this ability activates
                if (type == CHAINSAW || instance.getTier().getHarvestLevel() > 1 && (type == AXE || type.getToolTypes().contains("axe"))) {
                    if (!Configs.GAMEPLAY.AXE_TIMBER) return true;
                    if (state.getBlock().isIn(BlockTags.LOGS)) {
                        Utils.treeLogging(instance, stack, pos, player, world);
                    }
                }
            }
        }
        return true;
    }
}
