package muramasa.antimatter.mixin;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public class AxeItemMixin extends DiggerItem {

    public AxeItemMixin(float attackDamageModifier, float attackSpeedModifier, Tier tier, TagKey<Block> blocks, Properties properties) {
        super(attackDamageModifier, attackSpeedModifier, tier, blocks, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!AntimatterConfig.AXE_TIMBER.get()) return true;
        if (miningEntity instanceof Player player && !level.isClientSide) {
            if (this.isCorrectToolForDrops(state) && !player.isCrouching()) { // Only when player isn't shifting/crouching this ability activates
                if (state.is(BlockTags.LOGS)) {
                    Utils.treeLogging(AntimatterDefaultTools.AXE, stack, pos, player, level);
                }
            }
        }
        return true;
    }
}
