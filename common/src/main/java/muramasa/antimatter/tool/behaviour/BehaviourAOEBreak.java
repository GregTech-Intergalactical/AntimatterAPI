package muramasa.antimatter.tool.behaviour;

import lombok.Getter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IAddInformation;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.behaviour.IItemRightClick;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BehaviourAOEBreak implements IBlockDestroyed<IAntimatterTool>, IItemRightClick<IAntimatterTool>, IAddInformation<IAntimatterTool> {

    @Getter
    protected int column, row, depth;
    protected String tooltipKey;

    public BehaviourAOEBreak(int column, int row, int depth, String tooltipKey) {
        if (column == 0 && row == 0) Utils.onInvalidData("BehaviourAOEBreak was set to break empty rows and columns!");
        this.column = column;
        this.row = row;
        this.depth = depth;
        this.tooltipKey = tooltipKey;
    }

    @Override
    public String getId() {
        return "aoe_break";
    }

    @Override
    public void onAddInformation(IAntimatterTool instance, ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = instance.getDataTag(stack);
        if (tag != null){
            boolean enabled = tag.getBoolean(Ref.KEY_TOOL_BEHAVIOUR_AOE_BREAK);
            tooltip.add(Utils.translatable("antimatter.tooltip.behaviour.aoe_right_click", Utils.translatable("antimatter.behaviour." + tooltipKey)));
            String suffix = enabled ? "enabled" : "disabled";
            tooltip.add(Utils.translatable("antimatter.tooltip.behaviour.aoe_" + suffix, Utils.translatable("antimatter.behaviour." + tooltipKey)));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> onRightClick(IAntimatterTool instance, Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isShiftKeyDown() && !level.isClientSide){
            CompoundTag tag = instance.getDataTag(stack);
            if (tag != null){
                boolean enabled = tag.getBoolean(Ref.KEY_TOOL_BEHAVIOUR_AOE_BREAK);
                tag.putBoolean(Ref.KEY_TOOL_BEHAVIOUR_AOE_BREAK, !enabled);
                player.sendMessage(Utils.literal("Mode set to " + !enabled), player.getUUID());
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean onBlockDestroyed(IAntimatterTool instance, ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        //if(!super.onBlockDestroyed(stack, world, state, pos, entity)) return false;
        if (!(entity instanceof Player)) return true;
        CompoundTag tag = instance.getDataTag(stack);
        if (tag == null || !tag.getBoolean(Ref.KEY_TOOL_BEHAVIOUR_AOE_BREAK)) return true;
        Player player = (Player) entity;
        for (BlockPos blockPos : Utils.getHarvestableBlocksToBreak(world, player, instance, stack, column, row, depth)) {
            if (!instance.hasEnoughDurability(stack, instance.getAntimatterToolType().getUseDurability(), instance.getAntimatterToolType().isPowered()))
                return true;
            if (!Utils.breakBlock(world, player, stack, blockPos, instance.getAntimatterToolType().getUseDurability()))
                break;
        }
        return true;
    }
}
