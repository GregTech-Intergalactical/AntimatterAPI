package muramasa.antimatter.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.*;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import tesseract.api.gt.IEnergyItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static muramasa.antimatter.material.Material.NULL;

public interface IBasicAntimatterTool extends IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider, IAbstractToolMethods {
    AntimatterToolType getAntimatterToolType();

    Tier getItemTier();

    default String getTextureDomain(){
        return getDomain();
    }

    default Item getItem() {
        return (Item) this;
    }

    default Object2ObjectMap<String, IBehaviour<IBasicAntimatterTool>> getBehaviours(){
        return getAntimatterToolType().getBehaviours();
    }

    default Set<TagKey<Block>> getActualTags() {
        return getAntimatterToolType().getToolTypes();
    }

    default CompoundTag getDataTag(ItemStack stack) {
        return stack.getTagElement(Ref.TAG_TOOL_DATA);
    }

    default CompoundTag getOrCreateDataTag(ItemStack stack) {
        return stack.getOrCreateTagElement(Ref.TAG_TOOL_DATA);
    }

    default Tier getTier(ItemStack stack) {
        return getItemTier();
    }

    default boolean genericIsCorrectToolForDrops(ItemStack stack, BlockState state) {
        AntimatterToolType type = this.getAntimatterToolType();
        boolean containsEffectiveBlock = false;
        if (type.getEffectiveMaterials().contains(state.getMaterial())) {
            containsEffectiveBlock = true;
        }
        if (type.getEffectiveBlocks().contains(state.getBlock())) {
            containsEffectiveBlock = true;
        }
        for (TagKey<Block> effectiveBlockTag : type.getEffectiveBlockTags()) {
            if (state.is(effectiveBlockTag)){
                containsEffectiveBlock = true;
                break;
            }
        }
        for (TagKey<Block> toolType : getAntimatterToolType().getToolTypes()) {
            if (state.is(toolType)){
                containsEffectiveBlock = true;
                break;
            }
        }
        return containsEffectiveBlock && ToolUtils.isCorrectTierForDrops(getTier(stack), state);
    }

    default float getDefaultMiningSpeed(ItemStack stack){
        return getTier(stack).getSpeed() * getAntimatterToolType().getMiningSpeedMultiplier();
    }

    default void onGenericAddInformation(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        if (getAntimatterToolType().getTooltip().size() != 0) tooltip.addAll(getAntimatterToolType().getTooltip());
        tooltip.add(Utils.translatable("antimatter.tooltip.mining_level", getTier(stack).getLevel()).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Utils.translatable("antimatter.tooltip.tool_speed", Utils.literal("" + getDefaultMiningSpeed(stack)).withStyle(ChatFormatting.LIGHT_PURPLE)));
        for (Map.Entry<String, IBehaviour<IBasicAntimatterTool>> e : getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IAddInformation addInformation)) continue;
            addInformation.onAddInformation(this, stack, tooltip, flag);
        }
    }

    default boolean onGenericHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker, float volume, float pitch) {
        if (getAntimatterToolType().getUseSound() != null)
            target.getCommandSenderWorld().playSound(null, target.getX(), target.getY(), target.getZ(), getAntimatterToolType().getUseSound(), SoundSource.HOSTILE, volume, pitch);
        Utils.damageStack(getAntimatterToolType().getAttackDurability(), stack, attacker);
        if (attacker instanceof Player player) refillTool(stack, player);
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default boolean onGenericBlockDestroyed(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (getAntimatterToolType().getUseSound() != null)
                player.playNotifySound(getAntimatterToolType().getUseSound(), SoundSource.BLOCKS, 0.84F, 0.75F);
            boolean isToolEffective = genericIsCorrectToolForDrops(stack, state);
            if (state.getDestroySpeed(world, pos) != 0.0F) {
                int damage = isToolEffective ? getAntimatterToolType().getUseDurability() : getAntimatterToolType().getUseDurability() + 1;
                Utils.damageStack(damage, stack, entity);
            }
        }
        boolean returnValue = true;
        for (Map.Entry<String, IBehaviour<IBasicAntimatterTool>> e : getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IBlockDestroyed)) continue;
            returnValue = ((IBlockDestroyed) b).onBlockDestroyed(this, stack, world, state, pos, entity);
        }
        if (entity instanceof Player player) refillTool(stack, player);
        return returnValue;
    }

    default void refillTool(ItemStack stack, Player player){}

    default InteractionResult genericInteractLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand){
        InteractionResult result = InteractionResult.PASS;
        for (Map.Entry<String, IBehaviour<IBasicAntimatterTool>> e : getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IInteractEntity interactEntity)) continue;
            InteractionResult r = interactEntity.interactLivingEntity(this, stack, player, interactionTarget, usedHand);
            if (result != InteractionResult.SUCCESS) result = r;
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default InteractionResult onGenericItemUse(UseOnContext ctx) {
        InteractionResult result = InteractionResult.PASS;
        for (Map.Entry<String, IBehaviour<IBasicAntimatterTool>> e : getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemUse itemUse)) continue;
            InteractionResult r = itemUse.onItemUse(this, ctx);
            if (result != InteractionResult.SUCCESS) result = r;
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    default InteractionResultHolder<ItemStack> onGenericRightclick(Level level, Player player, InteractionHand usedHand){
        for (Map.Entry<String, IBehaviour<IBasicAntimatterTool>> e : getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemRightClick rightClick)) continue;
            InteractionResultHolder<ItemStack> r = rightClick.onRightClick(this, level, player, usedHand);
            if (r.getResult().shouldAwardStats()) return r;
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @SuppressWarnings("rawtypes")
    default InteractionResult onGenericHighlight(Player player, LevelRenderer levelRenderer, Camera camera, HitResult target, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        InteractionResult result = InteractionResult.PASS;
        for (Map.Entry<String, IBehaviour<IBasicAntimatterTool>> e : getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemHighlight)) continue;
            InteractionResult type = ((IItemHighlight) b).onDrawHighlight(player, levelRenderer, camera, target, partialTicks, poseStack, multiBufferSource);
            if (type != InteractionResult.SUCCESS) {
                result = type;
            } else {
                return InteractionResult.FAIL;
            }
        }
        return result;
    }

    default boolean hasEnoughDurability(ItemStack stack, int damage, boolean energy) {
        return true;
    }
}
