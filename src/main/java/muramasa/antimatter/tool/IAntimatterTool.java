package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface IAntimatterTool extends IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider, IForgeItem {

    AntimatterToolType getType();

    default Material getPrimaryMaterial(ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL));
    }

    default Material getSecondaryMaterial(ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
    }

    default Material[] getMaterials(ItemStack stack) {
        CompoundNBT tag = getDataTag(stack);
        return new Material[] { Material.get(tag.getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL)), Material.get(tag.getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL)) };
    }

    default Set<ToolType> getToolTypes() {
        return getType().getToolTypes().stream().map(ToolType::get).collect(Collectors.toSet());
    }

    default int getSubColour(ItemStack stack) {
        return getDataTag(stack).getInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR);
    }

    default long getCurrentEnergy(ItemStack stack) {
        return getDataTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY);
    }

    default long getMaxEnergy(ItemStack stack) {
        return getDataTag(stack).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    ItemStack asItemStack(Material primary, Material secondary);

    default CompoundNBT getDataTag(ItemStack stack) {
        CompoundNBT dataTag = stack.getChildTag(Ref.TAG_TOOL_DATA);
        return dataTag != null ? dataTag : validateTag(stack, Data.NULL, Data.NULL, 0, 10000);
    }

    default IItemTier getTier(ItemStack stack) {
        CompoundNBT dataTag = getDataTag(stack);
        Optional<AntimatterItemTier> tier = AntimatterItemTier.get(dataTag.getInt(Ref.KEY_TOOL_DATA_TIER));
        return tier.orElseGet(() -> resolveTierTag(dataTag));
    }

    default ItemStack resolveStack(Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        ItemStack stack = new ItemStack(getItem());
        validateTag(stack, primary, secondary, startingEnergy, maxEnergy);
        Map<Enchantment, Integer> mainEnchants = primary.getEnchantments(), handleEnchants = secondary.getEnchantments();
        if (!mainEnchants.isEmpty()) {
            mainEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack)).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
            return stack;
        }
        if (!handleEnchants.isEmpty()) handleEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack)).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
        return stack;
    }

    default CompoundNBT validateTag(ItemStack stack, Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        CompoundNBT dataTag = stack.getOrCreateChildTag(Ref.TAG_TOOL_DATA);
        dataTag.putString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL, primary.getId());
        dataTag.putString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL, secondary.getId());
        if (!getType().isPowered()) return dataTag;
        dataTag.putLong(Ref.KEY_TOOL_DATA_ENERGY, startingEnergy);
        dataTag.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, maxEnergy);
        return dataTag;
    }

    default AntimatterItemTier resolveTierTag(CompoundNBT dataTag) {
        AntimatterItemTier tier = AntimatterItemTier.getOrCreate(dataTag.getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL), dataTag.getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
        dataTag.putInt(Ref.KEY_TOOL_DATA_TIER, tier.hashCode());
        return tier;
    }

    default void onGenericFillItemGroup(ItemGroup group, NonNullList<ItemStack> list, long maxEnergy) {
        if (group != Ref.TAB_TOOLS) return;
        if (getType().isPowered()) {
            ItemStack stack = asItemStack(Data.NULL, Data.NULL);
            getDataTag(stack).putLong(Ref.KEY_TOOL_DATA_ENERGY, maxEnergy);
            list.add(stack);
        }
        else list.add(asItemStack(Data.NULL, Data.NULL));
    }

    default void onGenericAddInformation(ItemStack stack, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (flag.isAdvanced() && getType().isPowered()) tooltip.add(new StringTextComponent("Energy: " + getCurrentEnergy(stack) + " / " + getMaxEnergy(stack)));
        if (getType().getTooltip().size() != 0) tooltip.addAll(getType().getTooltip());
    }

    default boolean onGenericHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker, float volume, float pitch) {
        if (getType().getUseSound() != null) target.getEntityWorld().playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(), getType().getUseSound(), SoundCategory.HOSTILE, volume, pitch);
        stack.damageItem(getType().getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default boolean onGenericBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (getType().getUseSound() != null) player.playSound(getType().getUseSound(), SoundCategory.BLOCKS, 0.84F, 0.75F);
            boolean isToolEffective = Utils.isToolEffective(getType(), getToolTypes(), state);
            if (state.getBlockHardness(world, pos) != 0.0F) {
                stack.damageItem(isToolEffective ? getType().getUseDurability() : getType().getUseDurability() + 1, entity, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
            }
        }
        boolean returnValue = true;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IBlockDestroyed)) continue;
            returnValue = ((IBlockDestroyed) b).onBlockDestroyed(this, stack, world, state, pos ,entity);
        }
        return returnValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default ActionResultType onGenericItemUse(ItemUseContext ctx) {
        ActionResultType result = ActionResultType.PASS;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemUse)) continue;
            ActionResultType r = ((IItemUse) b).onItemUse(this, ctx);
            if (result != ActionResultType.SUCCESS) result = r;
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default ActionResultType onGenericHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        ActionResultType result = ActionResultType.PASS;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemHighlight)) continue;
            ActionResultType type = ((IItemHighlight) b).onDrawHighlight(player,ev);
            if (result != ActionResultType.SUCCESS) result = type;
        }
        return result;
    }

    default ItemStack getGenericContainerItem(final ItemStack oldStack) {
        ItemStack stack = oldStack.copy();
        int amount = damage(stack, getType().getCraftingDurability());
        if (!getType().isPowered()) { // Powered items can't enchant with Unbreaking
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), j = 0;
            for (int k = 0; level > 0 && k < amount; k++) {
                if (UnbreakingEnchantment.negateDamage(stack, level, Ref.RNG)) j++;
            }
            amount -= j;
        }
        if (amount > 0) stack.setDamage(stack.getDamage()+amount);
        return stack;
    }

    default int damage(ItemStack stack, int amount) {
        if (!getType().isPowered()) return amount;
        CompoundNBT tag = getDataTag(stack);
        long currentEnergy = tag.getLong(Ref.KEY_TOOL_DATA_ENERGY);
        int multipliedDamage = amount * 100;
        if (Ref.RNG.nextInt(20) == 0) return amount; // 1/20 chance of taking durability off the tool
        else if (currentEnergy >= multipliedDamage) {
            tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, currentEnergy - multipliedDamage); // Otherwise take energy off of tool if energy is larger than multiplied damage
            return 0; // Nothing is taken away from main durability
        }
        else { // Lastly, set energy to 0 and take leftovers off of tool durability itself
            int leftOver = (int) (multipliedDamage - currentEnergy);
            tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            return Math.max(1, leftOver / 100);
        }
    }

    default boolean hasEnoughDurability(ItemStack stack, int damage, boolean energy) {
        if (energy && getCurrentEnergy(stack) >= damage * 100) return true;
        return stack.getDamage() >= damage;
    }

    @Override
    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? getPrimaryMaterial(stack).getRGB() : getSubColour(stack) == 0 ? getSecondaryMaterial(stack).getRGB() : getSubColour(stack);
    }

    @Override
    default Texture[] getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        int layers = getType().getOverlayLayers();
        textures.add(new Texture(getDomain(), "item/tool/".concat(getType().getId())));
        if (layers == 1) textures.add(new Texture(getDomain(), "item/tool/overlay/".concat(getType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/tool/overlay/", getType().getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override
    default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }

}
