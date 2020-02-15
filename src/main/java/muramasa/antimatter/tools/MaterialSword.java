package muramasa.antimatter.tools;

import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MaterialSword extends SwordItem implements IAntimatterTool {

    protected String domain;
    protected IItemTier tier;
    protected AntimatterToolType type;
    protected Material primary;
    protected Material secondary;

    public MaterialSword(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, Material secondary) {
        super(tier, (int) type.getBaseAttackDamage(), type.getBaseAttackSpeed(), properties);
        this.domain = domain;
        this.type = type;
        this.tier = tier;
        this.primary = primary;
        this.secondary = secondary;
        setRegistryName(domain, getId());
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public AntimatterToolType getType() {
        return type;
    }

    @Override
    public Material getPrimaryMaterial() {
        return primary;
    }

    @Nullable
    @Override
    public Material getSecondaryMaterial() {
        return secondary;
    }

    @Override
    public Item asItem() { return this; }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            if (tier instanceof AntimatterItemTier && (!primary.getEnchantments().entrySet().isEmpty())) {
                ((AntimatterItemTier) tier).getNativeEnchantments().entrySet().forEach(e -> {
                    if (stack.canApplyAtEnchantingTable(e.getKey())) stack.addEnchantment(e.getKey(), e.getValue());
                });
            }
            items.add(stack);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(type.getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
            stack.damageItem(type.getMiningDurability(), entityLiving, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slotType);
        if (slotType == EquipmentSlotType.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", tier.getAttackDamage() + type.getBaseAttackDamage(), AttributeModifier.Operation.ADDITION));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

}
