package muramasa.antimatter.tools;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class AntimatterItemTier implements IItemTier {

    private Material material;
    private ImmutableMap<Enchantment, Integer> nativeEnchantments;

    // Take both Material and AntimatterToolType and do some min-max?
    public AntimatterItemTier(Material material) {
        this.material = material;
        this.nativeEnchantments = material.getEnchantments();
        // AntimatterAPI.register(AntimatterItemTier.class, this);
    }

    @Override
    public int getMaxUses() {
        return material.getToolDurability();
    }

    @Override
    public float getEfficiency() {
        return material.getToolSpeed();
    }

    @Override
    public float getAttackDamage() {
        return material.getToolDamage();
    }

    @Override
    public int getHarvestLevel() {
        return material.getToolQuality();
    }

    @Override
    public int getEnchantability() {
        return (int)(getHarvestLevel() + getEfficiency());
    }

    @Override
    public Ingredient getRepairMaterial() {
        if (material.has(MaterialType.GEM)) {
            return Ingredient.fromTag(Utils.getForgeItemTag("gems/".concat(material.getId())));
        }
        else if (material.has(MaterialType.INGOT)) {
            return Ingredient.fromTag(Utils.getForgeItemTag("ingots/".concat(material.getId())));
        }
        else if (material.has(MaterialType.DUST)) {
            return Ingredient.fromTag(Utils.getForgeItemTag("dusts/".concat(material.getId())));
        }
        else if (ItemTags.getCollection().get(new ResourceLocation("forge", "blocks/".concat(material.getId()))) != null) {
            return Ingredient.fromTag(Utils.getForgeItemTag("blocks/".concat(material.getId())));
        }
        return null;
    }

    public Material getMaterial() {
        return material;
    }

    public ImmutableMap<Enchantment, Integer> getNativeEnchantments() {
        return nativeEnchantments;
    }

}
