package muramasa.antimatter.tool.armor;

import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

import static muramasa.antimatter.Data.INGOT;

public class MatArmorMaterial implements IArmorMaterial {
    final AntimatterToolType toolType;
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    public MatArmorMaterial(AntimatterToolType toolType){
        this.toolType = toolType;
    }

    @Override
    public int getDurability(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * 40;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
        return (int)toolType.getBaseAttackDamage();
    }

    @Override
    public int getEnchantability() {
        return ArmorMaterial.IRON.getEnchantability();
    }

    @Override
    public SoundEvent getSoundEvent() {
        return ArmorMaterial.IRON.getSoundEvent();
    }

    @Override
    public Ingredient getRepairMaterial() {
        return null;
    }

    @Override
    public String getName() {
        return "armor";
    }

    @Override
    public float getToughness() {
        return toolType.getBaseAttackSpeed();
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
