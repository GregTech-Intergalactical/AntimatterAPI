package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;

public class BehaviourVanillaShovel implements IItemUse<IAntimatterTool> {

    public static final BehaviourVanillaShovel INSTANCE = new BehaviourVanillaShovel();

    @Override
    public String getId() {
        return "vanilla_shovel";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        if (c.getFace() == Direction.DOWN) return ActionResultType.PASS;
        BlockState state = c.getWorld().getBlockState(c.getPos());
        BlockState changedState = null;
        if (state.getBlock() == Blocks.GRASS_BLOCK && c.getWorld().isAirBlock(c.getPos().up())) {
            SoundEvent soundEvent = instance.getAntimatterToolType().getUseSound() == null ? SoundEvents.ITEM_SHOVEL_FLATTEN : instance.getAntimatterToolType().getUseSound();
            c.getWorld().playSound(c.getPlayer(), c.getPos(), soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            changedState = Blocks.GRASS_PATH.getDefaultState();
        }
        else if (state.getBlock() instanceof CampfireBlock && state.get(CampfireBlock.LIT)) {
            c.getWorld().playEvent(c.getPlayer(), 1009, c.getPos(), 0);
            changedState = state.with(CampfireBlock.LIT, false);
        }
        if (changedState != null) {
            c.getWorld().setBlockState(c.getPos(), changedState, 11);
            c.getItem().damageItem(instance.getAntimatterToolType().getUseDurability(), c.getPlayer(), (p) -> p.sendBreakAnimation(c.getHand()));
            return ActionResultType.SUCCESS;
        }
        else return ActionResultType.PASS;
    }
}
