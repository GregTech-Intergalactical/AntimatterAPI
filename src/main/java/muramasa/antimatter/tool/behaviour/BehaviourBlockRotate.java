package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;

public class BehaviourBlockRotate implements IItemUse<IAntimatterTool> {

    public static final BehaviourBlockRotate INSTANCE = new BehaviourBlockRotate();

    @Override
    public String getId() {
        return "block_rotate";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        BlockState state = c.getWorld().getBlockState(c.getPos());
        if (state.getBlock().getValidRotations(state, c.getWorld(), c.getPos()) != null && c.getPlayer() != null) {
            state.rotate(c.getWorld(), c.getPos(), c.getPlayer().isCrouching() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
            c.getItem().damageItem(instance.getType().getUseDurability(), c.getPlayer(), (p) -> p.sendBreakAnimation(c.getHand()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
