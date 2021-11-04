package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
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
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static muramasa.antimatter.Data.NULL;

public interface IAntimatterTool extends IAntimatterObject, IColorHandler, ITextureProvider, IModelProvider, IForgeItem {

    AntimatterToolType getAntimatterToolType();

    default Material getPrimaryMaterial(ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL));
    }

    default Material getSecondaryMaterial(ItemStack stack) {
        return Material.get(getDataTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
    }

    default Material[] getMaterials(ItemStack stack) {
        CompoundNBT tag = getDataTag(stack);
        return new Material[]{Material.get(tag.getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL)), Material.get(tag.getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL))};
    }

    default Set<ToolType> getToolTypes() {
        return getAntimatterToolType().getActualToolTypes();
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
        return dataTag != null ? dataTag : validateTag(stack, NULL, NULL, 0, 10000);
    }

    default IItemTier getTier(ItemStack stack) {
        CompoundNBT dataTag = getDataTag(stack);
        Optional<AntimatterItemTier> tier = AntimatterItemTier.get(dataTag.getInt(Ref.KEY_TOOL_DATA_TIER));
        return tier.orElseGet(() -> resolveTierTag(dataTag));
    }

    default ItemStack resolveStack(Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        ItemStack stack = new ItemStack(getItem());
        validateTag(stack, primary, secondary, startingEnergy, maxEnergy);
        Map<Enchantment, Integer> mainEnchants = primary.getToolEnchantments(), handleEnchants = secondary.getHandleEnchantments();
        if (!mainEnchants.isEmpty()) {
            mainEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack)).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
            //return stack;
        }
        if (!handleEnchants.isEmpty())
            handleEnchants.entrySet().stream().filter(e -> e.getKey().canApply(stack) && !mainEnchants.containsKey(e.getKey())).forEach(e -> stack.addEnchantment(e.getKey(), e.getValue()));
        return stack;
    }

    default CompoundNBT validateTag(ItemStack stack, Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        CompoundNBT dataTag = stack.getOrCreateChildTag(Ref.TAG_TOOL_DATA);
        dataTag.putString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL, primary.getId());
        dataTag.putString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL, secondary.getId());
        if (!getAntimatterToolType().isPowered()) return dataTag;
        dataTag.putLong(Ref.KEY_TOOL_DATA_ENERGY, startingEnergy);
        dataTag.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, maxEnergy);
        IEnergyHandler h = stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(t -> t).orElse(null);
        if (h instanceof ToolEnergyHandler) {
            ((ToolEnergyHandler) h).setEnergy(startingEnergy);
            ((ToolEnergyHandler) h).setMaxEnergy(maxEnergy);
        }
        return dataTag;
    }

    default AntimatterItemTier resolveTierTag(CompoundNBT dataTag) {
        AntimatterItemTier tier = AntimatterItemTier.getOrCreate(dataTag.getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL), dataTag.getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
        dataTag.putInt(Ref.KEY_TOOL_DATA_TIER, tier.hashCode());
        return tier;
    }

    default void onGenericFillItemGroup(ItemGroup group, NonNullList<ItemStack> list, long maxEnergy) {
        if (group != Ref.TAB_TOOLS) return;
        if (getAntimatterToolType().isPowered()) {
            ItemStack stack = asItemStack(NULL, NULL);
            getDataTag(stack).putLong(Ref.KEY_TOOL_DATA_ENERGY, maxEnergy);
            list.add(stack);
        } else list.add(asItemStack(NULL, NULL));
    }

    default void onGenericAddInformation(ItemStack stack, List<ITextComponent> tooltip, ITooltipFlag flag) {
        Material primary = getPrimaryMaterial(stack);
        Material secondary = getSecondaryMaterial(stack);
        tooltip.add(new StringTextComponent("Primary Material: " + primary.getDisplayName().getString()));
        if (secondary != NULL)
            tooltip.add(new StringTextComponent("Secondary Material: " + secondary.getDisplayName().getString()));
        if (flag.isAdvanced() && getAntimatterToolType().isPowered())
            tooltip.add(new StringTextComponent("Energy: " + getCurrentEnergy(stack) + " / " + getMaxEnergy(stack)));
        if (getAntimatterToolType().getTooltip().size() != 0) tooltip.addAll(getAntimatterToolType().getTooltip());
    }

    default boolean onGenericHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker, float volume, float pitch) {
        if (getAntimatterToolType().getUseSound() != null)
            target.getEntityWorld().playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(), getAntimatterToolType().getUseSound(), SoundCategory.HOSTILE, volume, pitch);
        Utils.damageStack(getAntimatterToolType().getAttackDurability(), stack, attacker);
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default boolean onGenericBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (getAntimatterToolType().getUseSound() != null)
                player.playSound(getAntimatterToolType().getUseSound(), SoundCategory.BLOCKS, 0.84F, 0.75F);
            boolean isToolEffective = Utils.isToolEffective(getAntimatterToolType(), getToolTypes(), state);
            if (state.getBlockHardness(world, pos) != 0.0F) {
                Utils.damageStack(isToolEffective ? getAntimatterToolType().getUseDurability() : getAntimatterToolType().getUseDurability() + 1, stack, entity);
            }
        }
        boolean returnValue = true;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IBlockDestroyed)) continue;
            returnValue = ((IBlockDestroyed) b).onBlockDestroyed(this, stack, world, state, pos, entity);
        }
        return returnValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default ActionResultType onGenericItemUse(ItemUseContext ctx) {
        ActionResultType result = ActionResultType.PASS;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemUse)) continue;
            ActionResultType r = ((IItemUse) b).onItemUse(this, ctx);
            if (result != ActionResultType.SUCCESS) result = r;
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    default ActionResultType onGenericHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        ActionResultType result = ActionResultType.PASS;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemHighlight)) continue;
            ActionResultType type = ((IItemHighlight) b).onDrawHighlight(player, ev);
            if (type != ActionResultType.SUCCESS) {
                result = type;
            } else {
                ev.setCanceled(true);
            }
        }
        return result;
    }

    default ItemStack getGenericContainerItem(final ItemStack oldStack) {
        ItemStack stack = oldStack.copy();
        int amount = damage(stack, getAntimatterToolType().getCraftingDurability());
        if (!getAntimatterToolType().isPowered()) { // Powered items can't enchant with Unbreaking
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), j = 0;
            for (int k = 0; level > 0 && k < amount; k++) {
                if (UnbreakingEnchantment.negateDamage(stack, level, Ref.RNG)) j++;
            }
            amount -= j;
        }
        boolean empty = false;
        if (amount > 0) {
            int l = stack.getDamage() + amount;
            stack.setDamage(l);
            empty = l >= stack.getMaxDamage();
        }
        if (empty) {
            if (!getAntimatterToolType().getBrokenItems().containsKey(this.getId())) {
                return ItemStack.EMPTY;
            }
            ItemStack item = getAntimatterToolType().getBrokenItems().get(this.getId()).apply(oldStack);
            return item;
        }
        return stack;
    }

    default int damage(ItemStack stack, int amount) {
        if (!getAntimatterToolType().isPowered()) return amount;
        IEnergyHandler h = stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).map(t -> t).orElse(null);
        if (!(h instanceof ItemEnergyHandler)) {
            return amount;
        }
        long currentEnergy = h.getEnergy();
        int multipliedDamage = amount * 100;
        if (Ref.RNG.nextInt(20) == 0) return amount; // 1/20 chance of taking durability off the tool
        else if (currentEnergy >= multipliedDamage) {
            ((ItemEnergyHandler) h).extractInternal(multipliedDamage, false, true);
            //tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, currentEnergy - multipliedDamage); // Otherwise take energy off of tool if energy is larger than multiplied damage
            return 0; // Nothing is taken away from main durability
        } else { // Lastly, set energy to 0 and take leftovers off of tool durability itself
            int leftOver = (int) (multipliedDamage - currentEnergy);
            ((ItemEnergyHandler) h).extractInternal(currentEnergy, false, true);
            //tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            return Math.max(1, leftOver / 100);
        }
    }

    default boolean hasEnoughDurability(ItemStack stack, int damage, boolean energy) {
        if (energy && getCurrentEnergy(stack) >= damage * 100) return true;
        return stack.getDamage() >= damage;
    }

    default void onItemBreak(ItemStack stack, PlayerEntity entity) {
        String name = this.getId();
        AntimatterToolType type = getAntimatterToolType();
        if (!type.getBrokenItems().containsKey(name)) {
            return;
        }
        ItemStack item = type.getBrokenItems().get(name).apply(stack);
        if (!item.isEmpty() && !entity.addItemStackToInventory(item)) {
            entity.dropItem(item, true);
        }
    }

    @Override
    default int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? getPrimaryMaterial(stack).getRGB() : getSubColour(stack) == 0 ? getSecondaryMaterial(stack).getRGB() : getSubColour(stack);
    }

    @Override
    default Texture[] getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        int layers = getAntimatterToolType().getOverlayLayers();
        textures.add(new Texture(getDomain(), "item/tool/".concat(getAntimatterToolType().getId())));
        if (layers == 1)
            textures.add(new Texture(getDomain(), "item/tool/overlay/".concat(getAntimatterToolType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getDomain(), String.join("", "item/tool/overlay/", getAntimatterToolType().getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override
    default void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }

}
