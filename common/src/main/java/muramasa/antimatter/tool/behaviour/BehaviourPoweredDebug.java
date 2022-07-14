package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

// TODO: REPLACE WITH CAPABILITY
public class BehaviourPoweredDebug implements IItemUse<IAntimatterTool> {

    public static final BehaviourPoweredDebug INSTANCE = new BehaviourPoweredDebug();

    @Override
    public String getId() {
        return "powered_debug";
    }

    @Override
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        if (instance.getAntimatterToolType().isPowered() && c.getLevel().getBlockState(c.getClickedPos()) == Blocks.REDSTONE_BLOCK.defaultBlockState() && c.getPlayer() != null) {
            ItemStack stack = c.getPlayer().getItemInHand(c.getHand());
            CompoundTag tag = instance.getDataTag(stack);
            if (instance.getMaxEnergy(stack) - instance.getCurrentEnergy(stack) <= 50000)
                tag.putLong(Ref.KEY_ITEM_ENERGY, instance.getMaxEnergy(stack));
            else tag.putLong(Ref.KEY_ITEM_ENERGY, tag.getLong(Ref.KEY_ITEM_ENERGY) + 50000);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
