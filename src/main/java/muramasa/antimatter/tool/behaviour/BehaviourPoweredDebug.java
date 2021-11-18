package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;

// TODO: REPLACE WITH CAPABILITY
public class BehaviourPoweredDebug implements IItemUse<IAntimatterTool> {

    public static final BehaviourPoweredDebug INSTANCE = new BehaviourPoweredDebug();

    @Override
    public String getId() {
        return "powered_debug";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        if (instance.getAntimatterToolType().isPowered() && c.getLevel().getBlockState(c.getClickedPos()) == Blocks.REDSTONE_BLOCK.defaultBlockState() && c.getPlayer() != null) {
            ItemStack stack = c.getPlayer().getItemInHand(c.getHand());
            CompoundNBT tag = instance.getDataTag(stack);
            if (instance.getMaxEnergy(stack) - instance.getCurrentEnergy(stack) <= 50000)
                tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, instance.getMaxEnergy(stack));
            else tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, tag.getLong(Ref.KEY_TOOL_DATA_ENERGY) + 50000);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
