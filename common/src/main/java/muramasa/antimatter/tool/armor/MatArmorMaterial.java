package muramasa.antimatter.tool.armor;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class MatArmorMaterial implements ArmorMaterial {
    final AntimatterArmorType type;
    final Material material;
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public MatArmorMaterial(AntimatterArmorType type, Material material) {
        this.type = type;
        this.material = material;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * MaterialTags.ARMOR.get(material).armorDurabilityFactor();
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slotIn) {
        return type.getExtraArmor() + MaterialTags.ARMOR.get(material).armor()[slotIn.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return ArmorMaterials.IRON.getEnchantmentValue();
    }

    @Override
    public SoundEvent getEquipSound() {
        return type.getEvent();
    }

    @Override
    public Ingredient getRepairIngredient() {
        if (material == null) {
            return Ingredient.EMPTY;
        }
        if (material.has(AntimatterMaterialTypes.GEM)) {
            return Ingredient.of(TagUtils.getForgelikeItemTag("gems/".concat(material.getId())));
        } else if (material.has(AntimatterMaterialTypes.INGOT)) {
            return Ingredient.of(TagUtils.getForgelikeItemTag("ingots/".concat(material.getId())));
        } else if (material.has(AntimatterMaterialTypes.DUST)) {
            return Ingredient.of(TagUtils.getForgelikeItemTag("dusts/".concat(material.getId())));
        }
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return material.getId() + "_" + type.getId();
    }

    @Override
    public float getToughness() {
        return type.getExtraToughness() + MaterialTags.ARMOR.get(material).toughness();
    }

    @Override
    public float getKnockbackResistance() {
        return type.getExtraKnockback() + MaterialTags.ARMOR.get(material).knockbackResistance();
    }
}
