package muramasa.antimatter.tool.armor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class MatArmorMaterial implements ArmorMaterial {
    final AntimatterArmorType type;
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public MatArmorMaterial(AntimatterArmorType type) {
        this.type = type;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * type.getDurabilityFactor();
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slotIn) {
        return type.getExtraArmor();
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
        return null;
    }

    @Override
    public String getName() {
        return "armor";
    }

    @Override
    public float getToughness() {
        return type.getExtraToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return type.getExtraKnockback();
    }
}
