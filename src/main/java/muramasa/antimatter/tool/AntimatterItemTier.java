package muramasa.antimatter.tool;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class AntimatterItemTier implements IItemTier {

    private boolean hasSecondary = false;
    private AntimatterToolType type;
    private Material primary;
    @Nullable private Material secondary;

    public AntimatterItemTier(AntimatterToolType type, Material primary, Material secondary) {
        this.type = type;
        this.primary = primary;
        this.secondary = secondary;
        // Handling enchantments as a product of crafting recipe and not creative menu
    }

    @Override
    public int getMaxUses() {
        return primary.getToolDurability() + (hasSecondary ? secondary.getHandleDurability() : 0);
    }

    @Override
    public float getEfficiency() {
        return primary.getToolSpeed() + ( hasSecondary ? secondary.getHandleSpeed() : 0);
    }

    // Can't pass type.getBaseAttackDamage() since MaterialSword does that in the constructor
    @Override
    public float getAttackDamage() { return primary.getToolDamage(); }

    @Override
    public int getHarvestLevel() {
        return type.getBaseQuality()  + primary.getToolQuality();
    }

    @Override
    public int getEnchantability() {
        return (int) (getHarvestLevel() + getEfficiency());
    }

    @Override
    public Ingredient getRepairMaterial() {
        if (type.isPowered()) return null;
        if (primary.has(MaterialType.GEM)) {
            return Ingredient.fromTag(Utils.getForgeItemTag("gems/".concat(primary.getId())));
        }
        else if (primary.has(MaterialType.INGOT)) {
            return Ingredient.fromTag(Utils.getForgeItemTag("ingots/".concat(primary.getId())));
        }
        else if (primary.has(MaterialType.DUST)) {
            return Ingredient.fromTag(Utils.getForgeItemTag("dusts/".concat(primary.getId())));
        }
        else if (ItemTags.getCollection().get(new ResourceLocation("forge", "blocks/".concat(primary.getId()))) != null) {
            return Ingredient.fromTag(Utils.getForgeItemTag("blocks/".concat(primary.getId())));
        }
        return null;
    }

    public Material getPrimaryMaterial() {
        return primary;
    }

    public boolean hasSecondary() {
        return hasSecondary;
    }

    @Nullable public Material getSecondaryMaterial() {
        return secondary;
    }

}
