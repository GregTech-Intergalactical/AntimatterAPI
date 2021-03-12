package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

import javax.annotation.Nullable;
import java.util.Collection;

public class BehaviourBlockRotate implements IItemUse<IAntimatterTool> {

    public static final BehaviourBlockRotate INSTANCE = new BehaviourBlockRotate();

    @Override
    public String getId() {
        return "block_rotate";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        BlockState state = c.getWorld().getBlockState(c.getPos());
        if (getValidRotations(state) != null && c.getPlayer() != null) {
            state.rotate(c.getWorld(), c.getPos(), c.getPlayer().isCrouching() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
            c.getItem().damageItem(instance.getType().getUseDurability(), c.getPlayer(), (p) -> p.sendBreakAnimation(c.getHand()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
    //Copie from 1.15. Might not work
    @Nullable
    Direction[] getValidRotations(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if ((prop.getName().equals("facing") || prop.getName().equals("rotation")) && prop.getValueClass() == Direction.class)
            {
                @SuppressWarnings("unchecked")
                Collection<Direction> values = ((Collection<Direction>)prop.getAllowedValues());
                return values.toArray(new Direction[values.size()]);
            }
        }
        return null;
    }
}
