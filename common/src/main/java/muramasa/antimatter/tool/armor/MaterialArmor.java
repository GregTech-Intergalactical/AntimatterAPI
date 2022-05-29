package muramasa.antimatter.tool.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.tool.IAbstractToolMethods;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static muramasa.antimatter.Data.NULL;

public class MaterialArmor extends ArmorItem implements IAntimatterArmor, DyeableLeatherItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    protected String domain;
    protected AntimatterArmorType type;
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public MaterialArmor(String domain, AntimatterArmorType type, ArmorMaterial materialIn, EquipmentSlot slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.domain = domain;
        this.type = type;
        AntimatterAPI.register(IAntimatterArmor.class, this);
        if (type.getSlot() == EquipmentSlot.HEAD && AntimatterPlatformUtils.isClient()) {
            RenderHelper.registerProbePropertyOverrides(this);
        }
    }

    @Override
    public String getId() {
        return type.getId();
    }

    @Override
    public AntimatterArmorType getAntimatterArmorType() {
        return type;
    }

    @Override
    public ItemStack asItemStack(Material primary) {
        return resolveStack(primary);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return getRepairMaterial(toRepair).test(repair);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (getMaterial(stack) == null) return super.getMaxDamage();
        return MAX_DAMAGE_ARRAY[slot.getIndex()] * getMaterial(stack).getArmorDurabilityFactor();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slotType, ItemStack stack) {
        Material mat = getMaterial(stack);
        if (mat == null || mat == NULL || slotType != this.slot) return super.getDefaultAttributeModifiers(slotType);
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", material.getDefenseForSlot(slot) + mat.getArmor()[slot.getIndex()], AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", material.getToughness() + mat.getToughness(), AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", mat.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        onGenericAddInformation(stack, tooltip, flag);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String extra = "";
        if (slot == EquipmentSlot.HEAD && type != null) {
            CompoundTag nbt = stack.getTag();
            if (nbt != null && nbt.contains("theoneprobe") && nbt.getBoolean("theoneprobe")) extra = "_probe";
        }
        return Ref.ID + ":textures/models/armor_layer_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + (type == null ? "" : "_" + type + extra) + ".png";
    }

    @Override
    public int getColor(ItemStack stack) {
        return getItemColor(stack, null, 0);
    }

    @Override
    public boolean hasCustomColor(ItemStack stack) {
        Material mat = getMaterial(stack);
        return mat != null;
    }

    @Override
    public void setColor(ItemStack stack, int color) {

    }

    @Override
    public void clearColor(ItemStack stack) {

    }
}
