package muramasa.antimatter.tool.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.tool.AntimatterItemTier;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

import static muramasa.antimatter.Data.GEM;
import static muramasa.antimatter.Data.HELMET;
import static muramasa.antimatter.Data.INGOT;

public class MaterialArmor extends ArmorItem implements IAntimatterTool {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    protected String domain;
    protected AntimatterToolType type;
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public MaterialArmor(String domain, AntimatterToolType type, IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
        this.domain = domain;
        this.type = type;
        AntimatterAPI.register(IAntimatterTool.class, getId(), this);
    }

    @Override
    public String getId() {
        return type.getId();
    }

    @Override
    public AntimatterToolType getType() {
        return type;
    }

    @Override
    public ItemStack asItemStack(Material primary, Material secondary) {
        return resolveStack(primary, secondary, 0, 0);
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return getTier(toRepair).getRepairMaterial().test(repair);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (getPrimaryMaterial(stack) == null) return super.getMaxDamage(stack);
        return MAX_DAMAGE_ARRAY[slot.getIndex()] * getPrimaryMaterial(stack).getArmorDurabilityFactor();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType, ItemStack stack) {
        if (getPrimaryMaterial(stack) == null || slotType != this.slot) return super.getAttributeModifiers(slotType, stack);
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        modifiers.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", material.getDamageReductionAmount(slot) + getPrimaryMaterial(stack).getArmor(), AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", material.getToughness() + getPrimaryMaterial(stack).getToughness(), AttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    @Nullable
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        return null;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return Ref.ID + ":textures/models/armor_layer_" + (slot == EquipmentSlotType.LEGS ? 2 : 1) + ".png";
    }
}
