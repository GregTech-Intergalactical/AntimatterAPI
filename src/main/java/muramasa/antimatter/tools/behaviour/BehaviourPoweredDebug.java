package muramasa.antimatter.tools.behaviour;

import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tools.base.MaterialTool;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;

public class BehaviourPoweredDebug implements IItemUse<MaterialTool> {

    @Override
    public String getId() {
        return "powered_debug";
    }

    @Override
    public ActionResultType onItemUse(MaterialTool instance, ItemUseContext c) {
        if (instance.getType().isPowered() && c.getWorld().getBlockState(c.getPos()) == Blocks.REDSTONE_BLOCK.getDefaultState() && c.getPlayer() != null) {
            ItemStack stack = c.getPlayer().getHeldItem(c.getHand());
            CompoundNBT nbt = instance.getTag(stack);
            if (instance.getMaxEnergy(stack) - instance.getEnergy(stack) <= 50000) nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, instance.getMaxEnergy(stack));
            else nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, nbt.getLong(Ref.KEY_TOOL_DATA_ENERGY) + 50000);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
