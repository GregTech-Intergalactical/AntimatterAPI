package muramasa.antimatter.tool;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IBlockDestroyed;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IAntimatterTool extends IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider {

    @Nonnull AntimatterToolType getType();

    @Nonnull default Material getPrimaryMaterial(@Nonnull ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL));
    }

    @Nonnull default Material getSecondaryMaterial(@Nonnull ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
    }

    @Nonnull default Material[] getMaterials(@Nonnull ItemStack stack) {
        CompoundNBT nbt = getDataTag(stack);
        return new Material[] { Material.get(nbt.getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL)), Material.get(nbt.getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL)) };
    }

    default long getCurrentEnergy(ItemStack stack) {
        return getDataTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY);
    }

    default long getMaxEnergy(ItemStack stack) {
        return getDataTag(stack).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    Item asItem();

    @Nonnull ItemStack asItemStack(@Nonnull Material primary, @Nonnull Material secondary);

    @Nonnull default CompoundNBT getDataTag(ItemStack stack) {
        CompoundNBT dataTag = stack.getChildTag(Ref.TAG_TOOL_DATA);
        return dataTag != null ? dataTag : validateTag(stack, Data.NULL, Data.NULL, 0, 10000);
    }

    @Nonnull default IItemTier getTier(@Nonnull ItemStack stack) {
        CompoundNBT dataTag = getDataTag(stack);
        Optional<AntimatterItemTier> tier = AntimatterItemTier.get(dataTag.getInt(Ref.KEY_TOOL_DATA_TIER));
        return tier.isPresent() ? tier.get() : resolveTierTag(dataTag);
    }

    default ItemStack resolveStack(@Nonnull Material primary, @Nonnull Material secondary, long startingEnergy, long maxEnergy) {
        ItemStack stack = new ItemStack(asItem());
        validateTag(stack, primary, secondary, startingEnergy, maxEnergy);
        Map<Enchantment, Integer> mainEnchants = primary.getEnchantments(), handleEnchants = secondary.getEnchantments();
        if (!mainEnchants.isEmpty()) {
            mainEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack)).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
            return stack;
        }
        if (!handleEnchants.isEmpty()) handleEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack)).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
        // if (handleEnchants != null) handleEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack)).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
        return stack;
    }

    default CompoundNBT validateTag(ItemStack stack, Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        CompoundNBT dataTag = stack.getOrCreateChildTag(Ref.TAG_TOOL_DATA);
        // if (!dataTag.isEmpty()) return dataTag;
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

    default void onGenericAddInformation(ItemStack stack, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (flag.isAdvanced() && getType().isPowered()) {
            tooltip.add(new StringTextComponent("Energy: " + getCurrentEnergy(stack) + " / " + getMaxEnergy(stack)));
        }
        if (getType().getTooltip().size() != 0) {
            for (ITextComponent text : getType().getTooltip()) {
                tooltip.add(text);
            }
        }
    }

    default boolean onGenericHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker, float volume, float pitch) {
        if (getType().getUseSound() != null) target.getEntityWorld().playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(), getType().getUseSound(), SoundCategory.HOSTILE, volume, pitch);
        stack.damageItem(getType().getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    default boolean onGenericBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (getType().getUseSound() != null) player.playSound(getType().getUseSound(), SoundCategory.BLOCKS, 0.84F, 0.75F);
            boolean isToolEffective = Utils.isToolEffective(getType(), state);
            if (state.getBlockHardness(world, pos) != 0.0F) {
                stack.damageItem(isToolEffective ? getType().getUseDurability() : getType().getUseDurability() + 1, entity, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
            }
        }
        boolean returnValue = true;
        for (Map.Entry<String, IBehaviour<MaterialTool>> e : getType().getBehaviours().entrySet()) {
            IBehaviour b = e.getValue();
            if (!(b instanceof IBlockDestroyed)) continue;
            returnValue = ((IBlockDestroyed) b).onBlockDestroyed(this, stack, world, state, pos ,entity);
        }
        return returnValue;
    }

    default ActionResultType onGenericItemUse(ItemUseContext ctx) {
        ActionResultType result = ActionResultType.PASS;
        for (Map.Entry<String, IBehaviour<MaterialTool>> e : getType().getBehaviours().entrySet()) {
            IBehaviour b = e.getValue();
            if (!(b instanceof IItemUse)) continue;
            ActionResultType r = ((IItemUse) b).onItemUse(this, ctx);
            if (result != ActionResultType.SUCCESS) result = r;
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
        if (amount > 0) stack.setDamage(stack.getDamage() - amount);
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

    @Override default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? getPrimaryMaterial(stack).getRGB() : getSecondaryMaterial(stack).getRGB();
    }

    @Override default Texture[] getTextures() {
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

    @Override default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }

}
