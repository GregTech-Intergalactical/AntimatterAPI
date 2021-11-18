package muramasa.antimatter.tool.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

public class MatArmorMaterial implements IArmorMaterial {
    final AntimatterArmorType toolType;
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};

    public MatArmorMaterial(AntimatterArmorType toolType) {
        this.toolType = toolType;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * toolType.getDurabilityFactor();
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotIn) {
        return toolType.getBaseArmor();
    }

    @Override
    public int getEnchantmentValue() {
        return ArmorMaterial.IRON.getEnchantmentValue();
    }

    @Override
    public SoundEvent getEquipSound() {
        return toolType.getEvent();
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
        return toolType.getBaseToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return toolType.getBaseKnockback();
    }
}
