package muramasa.antimatter.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.*;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import tesseract.TesseractCapUtils;
import tesseract.api.context.TesseractItemContext;
import tesseract.api.gt.IEnergyHandlerItem;
import tesseract.api.gt.IEnergyItem;

import javax.annotation.Nullable;
import java.util.*;

import static muramasa.antimatter.material.Material.NULL;

public interface IAntimatterTool extends ISharedAntimatterObject, IColorHandler, ITextureProvider, IModelProvider, IAbstractToolMethods, IEnergyItem {

    AntimatterToolType getAntimatterToolType();

    AntimatterItemTier getAntimatterItemTier();

    default String getTextureDomain(){
        return Ref.ID;
    }

    default Material getPrimaryMaterial(ItemStack stack) {
        if (getAntimatterToolType().isSimple()) return getAntimatterItemTier().getPrimary();
        return Material.get(getOrCreateDataTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL));
    }

    default Material getSecondaryMaterial(ItemStack stack) {
        if (getAntimatterToolType().isSimple()) return getAntimatterItemTier().getSecondary();
        return Material.get(getOrCreateDataTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
    }

    default DyeColor getDyeColor(ItemStack stack){
        CompoundTag data = getOrCreateDataTag(stack);
        if (data.contains(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR)){
            Optional<DyeColor> color = Arrays.stream(DyeColor.values()).filter(t -> t.getMaterialColor().col == data.getInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR)).findFirst();
            return color.orElse(DyeColor.WHITE);
        }
        return null;
    }

    default Material[] getMaterials(ItemStack stack) {
        return new Material[]{getPrimaryMaterial(stack), getSecondaryMaterial(stack)};
    }

    default Item getItem() {
        return (Item) this;
    }

    default Set<TagKey<Block>> getActualTags() {
        return getAntimatterToolType().getActualTags();
    }

    default int getSubColour(ItemStack stack) {
        return getDyeColor(stack) == null ? 0 : getDyeColor(stack).getMaterialColor().col;
    }

    default long getCurrentEnergy(ItemStack stack) {
        return getEnergyTag(stack).getLong(Ref.KEY_ITEM_ENERGY);
    }

    default long getMaxEnergy(ItemStack stack) {
        return getEnergyTag(stack).getLong(Ref.KEY_ITEM_MAX_ENERGY);
    }

    @Override
    default boolean canCreate(TesseractItemContext context) {
        return getAntimatterToolType().isPowered();
    }

    ItemStack asItemStack(Material primary, Material secondary);

    default CompoundTag getEnergyTag(ItemStack stack){
        CompoundTag dataTag = stack.getTagElement(Ref.TAG_ITEM_ENERGY_DATA);
        return dataTag != null ? dataTag : validateEnergyTag(stack, 0, 10000);
    }

    default CompoundTag getDataTag(ItemStack stack) {
        return stack.getTagElement(Ref.TAG_TOOL_DATA);
    }

    default CompoundTag getOrCreateDataTag(ItemStack stack) {
        CompoundTag dataTag = stack.getTagElement(Ref.TAG_TOOL_DATA);
        return dataTag != null ? dataTag : validateTag(stack, getAntimatterItemTier().getPrimary(), getAntimatterItemTier().getSecondary(), 0, 10000);
    }

    default Tier getTier(ItemStack stack) {
        if (getAntimatterToolType().isSimple()) return getAntimatterItemTier();
        CompoundTag dataTag = getOrCreateDataTag(stack);
        Optional<AntimatterItemTier> tier = AntimatterItemTier.get(dataTag.getInt(Ref.KEY_TOOL_DATA_TIER));
        return tier.orElseGet(() -> resolveTierTag(dataTag));
    }

    default ItemStack resolveStack(Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        Item item = getItem();
        ItemStack stack = new ItemStack(item);
        if (!getAntimatterToolType().isSimple() || getAntimatterToolType().isPowered()) validateTag(stack, primary, secondary, startingEnergy, maxEnergy);
        if (!primary.has(MaterialTags.TOOLS)){
            return stack;
        }
        Map<Enchantment, Integer> mainEnchants = MaterialTags.TOOLS.get(primary).toolEnchantment();
        if (!mainEnchants.isEmpty()) {
            mainEnchants.entrySet().stream().filter(e -> e.getKey().canEnchant(stack)).forEach(e -> stack.enchant(e.getKey(), e.getValue()));
            //return stack;
        }
        /*if (!handleEnchants.isEmpty()) {
            handleEnchants.entrySet().stream().filter(e -> e.getKey().canEnchant(stack) && !mainEnchants.containsKey(e.getKey())).forEach(e -> stack.enchant(e.getKey(), e.getValue()));
        }*/
        return stack;
    }

    default CompoundTag validateTag(ItemStack stack, Material primary, Material secondary, long startingEnergy, long maxEnergy) {
        CompoundTag dataTag = stack.getOrCreateTagElement(Ref.TAG_TOOL_DATA);
        if (!getAntimatterToolType().isSimple()){
            dataTag.putString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL, primary.getId());
            dataTag.putString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL, secondary.getId());
        }
        if (!getAntimatterToolType().isPowered()) return dataTag;
        validateEnergyTag(stack, startingEnergy, maxEnergy);
        return dataTag;
    }

    default CompoundTag validateEnergyTag(ItemStack stack, long startingEnergy, long maxEnergy){
        IEnergyHandlerItem h = TesseractCapUtils.getEnergyHandlerItem(stack).orElse(null);
        if (h != null){
            h.setEnergy(startingEnergy);
            h.setCapacity(maxEnergy);
            stack.setTag(h.getContainer().getTag());
        }
        return stack.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA);
    }

    default AntimatterItemTier resolveTierTag(CompoundTag dataTag) {
        AntimatterItemTier tier = AntimatterItemTier.getOrCreate(dataTag.getString(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL), dataTag.getString(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL));
        dataTag.putInt(Ref.KEY_TOOL_DATA_TIER, tier.hashCode());
        return tier;
    }

    default void onGenericFillItemGroup(CreativeModeTab group, NonNullList<ItemStack> list, long maxEnergy) {
        if (group != Ref.TAB_TOOLS) return;
        if (getAntimatterToolType().isPowered()) {
            ItemStack stack = asItemStack(NULL, NULL);
            IEnergyHandlerItem h = TesseractCapUtils.getEnergyHandlerItem(stack).orElse(null);
            if (h != null){
                list.add(stack.copy());
                h.setCapacity(maxEnergy);
                h.setEnergy(maxEnergy);
                stack.setTag(h.getContainer().getTag());
                list.add(stack);
            }
        } else list.add(asItemStack(getAntimatterItemTier().getPrimary(), getAntimatterItemTier().getSecondary()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void onGenericAddInformation(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        //TODO change this to object %s system for other lang compat
        Material primary = getPrimaryMaterial(stack);
        Material secondary = getSecondaryMaterial(stack);
        if (!getAntimatterToolType().isSimple())
            tooltip.add(new TranslatableComponent("antimatter.tooltip.material_primary", primary.getDisplayName().getString()));
        if (secondary != NULL)
            tooltip.add(new TranslatableComponent("antimatter.tooltip.material_secondary", secondary.getDisplayName().getString()));
        DyeColor color = getDyeColor(stack);
        if (color != null){
            tooltip.add(new TranslatableComponent("antimatter.tooltip.dye_color", color.getName()));
        }
        if (flag.isAdvanced() && getAntimatterToolType().isPowered())
            tooltip.add(new TranslatableComponent("antimatter.tooltip.energy").append(": " + getCurrentEnergy(stack) + " / " + getMaxEnergy(stack)));
        if (getAntimatterToolType().getTooltip().size() != 0) tooltip.addAll(getAntimatterToolType().getTooltip());
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IAddInformation addInformation)) continue;
            addInformation.onAddInformation(this, stack, tooltip, flag);
        }
    }

    default boolean onGenericHitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker, float volume, float pitch) {
        if (getAntimatterToolType().getUseSound() != null)
            target.getCommandSenderWorld().playSound(null, target.getX(), target.getY(), target.getZ(), getAntimatterToolType().getUseSound(), SoundSource.HOSTILE, volume, pitch);
        Utils.damageStack(getAntimatterToolType().getAttackDurability(), stack, attacker);
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default boolean onGenericBlockDestroyed(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (getAntimatterToolType().getUseSound() != null)
                player.playNotifySound(getAntimatterToolType().getUseSound(), SoundSource.BLOCKS, 0.84F, 0.75F);
            boolean isToolEffective = Utils.isToolEffective(getAntimatterToolType(), getActualTags(), state);
            if (state.getDestroySpeed(world, pos) != 0.0F) {
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
    default InteractionResult onGenericItemUse(UseOnContext ctx) {
        InteractionResult result = InteractionResult.PASS;
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
            IBehaviour<?> b = e.getValue();
            if (!(b instanceof IItemUse itemUse)) continue;
            InteractionResult r = itemUse.onItemUse(this, ctx);
            if (result != InteractionResult.SUCCESS) result = r;
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    default InteractionResultHolder<ItemStack> onGenericRightclick(Level level, Player player, InteractionHand usedHand){
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
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
        for (Map.Entry<String, IBehaviour<IAntimatterTool>> e : getAntimatterToolType().getBehaviours().entrySet()) {
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

    default ItemStack getGenericContainerItem(final ItemStack oldStack) {
        ItemStack stack = oldStack.copy();
        int amount = damage(stack, getAntimatterToolType().getCraftingDurability());
        if (!getAntimatterToolType().isPowered()) { // Powered items can't enchant with Unbreaking
            int level = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack), j = 0;
            for (int k = 0; level > 0 && k < amount; k++) {
                if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(stack, level, Ref.RNG)) j++;
            }
            amount -= j;
        }
        boolean empty = false;
        if (amount > 0) {
            int l = stack.getDamageValue() + amount;
            stack.setDamageValue(l);
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
        IEnergyHandlerItem h = TesseractCapUtils.getEnergyHandlerItem(stack).orElse(null);
        if (!(h instanceof ItemEnergyHandler)) {
            return amount;
        }
        long currentEnergy = h.getEnergy();
        int multipliedDamage = amount * 100;
        if (Ref.RNG.nextInt(20) == 0) return amount; // 1/20 chance of taking durability off the tool
        else if (currentEnergy >= multipliedDamage) {
            h.extractEu(multipliedDamage, false);
            stack.setTag(h.getContainer().getTag());
            //tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, currentEnergy - multipliedDamage); // Otherwise take energy off of tool if energy is larger than multiplied damage
            return 0; // Nothing is taken away from main durability
        } else { // Lastly, set energy to 0 and take leftovers off of tool durability itself
            int leftOver = (int) (multipliedDamage - currentEnergy);
            h.extractEu(currentEnergy, false);
            stack.setTag(h.getContainer().getTag());
            //tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            return Math.max(1, leftOver / 100);
        }
    }

    default boolean hasEnoughDurability(ItemStack stack, int damage, boolean energy) {
        if (energy && getCurrentEnergy(stack) >= damage * 100) return true;
        return stack.getDamageValue() >= damage;
    }

    default void onItemBreak(ItemStack stack, Player entity) {
        String name = this.getId();
        AntimatterToolType type = getAntimatterToolType();
        if (!type.getBrokenItems().containsKey(name)) {
            return;
        }
        ItemStack item = type.getBrokenItems().get(name).apply(stack);
        if (!item.isEmpty() && !entity.addItem(item)) {
            entity.drop(item, true);
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
        textures.add(new Texture(getTextureDomain(), "item/tool/".concat(getAntimatterToolType().getId())));
        if (layers == 1)
            textures.add(new Texture(getTextureDomain(), "item/tool/overlay/".concat(getAntimatterToolType().getId())));
        if (layers > 1) {
            for (int i = 1; i <= layers; i++) {
                textures.add(new Texture(getTextureDomain(), String.join("", "item/tool/overlay/", getAntimatterToolType().getId(), "_", Integer.toString(i))));
            }
        }
        return textures.toArray(new Texture[textures.size()]);
    }

    @Override
    default void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        prov.tex(item, "minecraft:item/handheld", getTextures());
    }


    // abstraction shit
    boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player);

    boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker);

    int getItemEnchantability(ItemStack stack);
}
