package muramasa.antimatter.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MaterialTool extends ToolItem implements IAntimatterTool {

    protected final String domain;
    protected final AntimatterToolType type;

    protected final int energyTier;
    protected final long maxEnergy;

    public MaterialTool(String domain, AntimatterToolType type, Properties properties) {
        super(type.getBaseAttackDamage(), type.getBaseAttackSpeed(), AntimatterItemTier.NULL, type.getEffectiveBlocks(), properties);
        this.domain = domain;
        this.type = type;
        this.energyTier = -1;
        this.maxEnergy = -1;
        AntimatterAPI.register(IAntimatterTool.class, getId(), this);
    }

    public MaterialTool(String domain, AntimatterToolType type, Properties properties, int energyTier) {
        super(type.getBaseAttackDamage(), type.getBaseAttackSpeed(), AntimatterItemTier.NULL, type.getEffectiveBlocks(), properties);
        this.domain = domain;
        this.type = type;
        this.energyTier = energyTier;
        this.maxEnergy = type.getBaseMaxEnergy() * energyTier;
        AntimatterAPI.register(IAntimatterTool.class, getId(), this);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return type.isPowered() ? String.join("_", type.getId(), Ref.VN[energyTier].toLowerCase(Locale.ENGLISH)) : type.getId();
    }

    @Nonnull
    @Override
    public AntimatterToolType getType() { return type; }

    @Nonnull
    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return getToolTypes();
    }

    /** Returns -1 if its not a powered tool **/
    public int getEnergyTier() {
        return energyTier;
    }

    @Nonnull
    @Override
    public ItemStack asItemStack(@Nonnull Material primary, @Nonnull Material secondary) {
        return resolveStack(primary, secondary, 0, maxEnergy);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list) {
        onGenericFillItemGroup(group, list, maxEnergy);
    }

    /*
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return getPrimaryMaterial(stack).getDisplayName().appendSibling(new StringTextComponent(type.getId()));
    }
     */

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        onGenericAddInformation(stack, tooltip, flag);
        super.addInformation(stack, world, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return type.getUseAction();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return type.getUseAction() == UseAction.NONE ? super.getUseDuration(stack) : 72000;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return Utils.isToolEffective(this, state) && getTier(stack).getHarvestLevel() >= state.getHarvestLevel();
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return getTier(stack).getHarvestLevel();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getTier(stack).getMaxUses();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return onGenericHitEntity(stack, target, attacker, 0.75F, 0.75F);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return Utils.isToolEffective(this, state) ? getTier(stack).getEfficiency() : 1.0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        return onGenericBlockDestroyed(stack, world, state, pos, entity);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        return onGenericItemUse(ctx);
    }

    public void handleRenderHighlight(PlayerEntity entity, DrawHighlightEvent ev) {
        onGenericHighlight(entity,ev);
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return type.getBlockBreakability();
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return type.getToolTypes().contains("axe");
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        if (slotType == EquipmentSlotType.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", getTier(stack).getAttackDamage(), AttributeModifier.Operation.ADDITION));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

//    @Override
//    public ActionResultType onItemUse(ItemUseContext ctx) {
//        return onGenericItemUse(ctx);

        //TODO functionality moved to BlockMachine.onBlockActivated
        //TODO determine if other mods need smart interaction on
        //TODO blocks that *don't* extend BlockMachine
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile == null) return EnumActionResult.PASS;
//        EnumActionResult result = EnumActionResult.PASS;
//        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, facing)) {
//            Direction targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
//            IInteractHandler interactHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
//            if (interactHandler != null) {
//                if (type != null && interactHandler.onInteract(player, hand, targetSide, type)) {
//                    damage(stack, type.getDamageCrafting(), player, true);
//                    result = EnumActionResult.SUCCESS;
//                }
//            }
//        }
//        if (tile.hasCapability(GTCapabilities.COVERABLE, facing)) {
//            Direction targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
//            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
//            if (coverHandler != null) {
//                if (type != null && coverHandler.onInteract(player, hand, targetSide, type)) {
//                    damage(stack, type.getDamageCrafting(), player, true);
//                    result = EnumActionResult.SUCCESS;
//                }
//            }
//        }
//        return result;
//    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) ? 0 : damage(stack, amount);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return getTier(stack).getEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !type.isPowered();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return type.isPowered() ? enchantment != Enchantments.UNBREAKING : super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack oldStack) {
        return getGenericContainerItem(oldStack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        // return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
        if (type.isPowered()) return getCurrentEnergy(stack) > 0 ? 0x00BFFF : super.getRGBDurabilityForDisplay(stack);
        return super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (!type.isPowered()) return super.getDurabilityForDisplay(stack);
        long currentEnergy = getCurrentEnergy(stack);
        if (currentEnergy > 0) {
            double maxAmount = getMaxEnergy(stack), difference = maxAmount - currentEnergy;
            return difference / maxAmount;
        }
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (type.isPowered()) return true;
        return super.showDurabilityBar(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (type.isPowered()) {
            ItemEnergyHandler.initNBT(nbt);
            //TODO: not lv
            return new ItemEnergyHandler(stack, ItemEnergyHandler.getEnergyFromStack(stack), type.getBaseMaxEnergy(), 32,32,1,1);
        }
        return null;
    }
}