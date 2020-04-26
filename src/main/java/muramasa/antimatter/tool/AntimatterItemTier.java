package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class AntimatterItemTier implements IItemTier {

    private Material primary;
    private Material secondary;

    public static final AntimatterItemTier NULL = new AntimatterItemTier(Data.NULL, Data.NULL);

    public static final Object2ObjectMap<Pair<Material, Material>, IItemTier> TIERS = new Object2ObjectOpenHashMap<>();

    static {
        TIERS.put(Pair.of(Data.NULL, Data.NULL), NULL);
    }

    protected AntimatterItemTier(@Nonnull Material primary, @Nonnull Material secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public static IItemTier getOrCreate(@Nonnull Material primary, @Nonnull Material secondary) {
        return TIERS.computeIfAbsent(Pair.of(primary, secondary), v -> new AntimatterItemTier(primary, secondary));
    }

    @Override
    public int getMaxUses() {
        return primary.getToolDurability() + secondary.getHandleDurability();
    }

    @Override
    public float getEfficiency() {
        return primary.getToolSpeed() + secondary.getHandleSpeed();
    }

    // Can't pass type.getBaseAttackDamage() since MaterialSword does that in the constructor
    @Override
    public float getAttackDamage() { return primary.getToolDamage(); }

    @Override
    public int getHarvestLevel() {
        return /* type.getBaseQuality()  +  */ primary.getToolQuality();
    }

    @Override
    public int getEnchantability() {
        return (int) (getHarvestLevel() + getEfficiency());
    }

    @Override
    public Ingredient getRepairMaterial() {
        // if (type.isPowered()) return null;
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

    public Material getSecondaryMaterial() {
        return secondary;
    }

}
