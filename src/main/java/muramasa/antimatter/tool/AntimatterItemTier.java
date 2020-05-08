package muramasa.antimatter.tool;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class AntimatterItemTier implements IItemTier {

    private final Material primary;
    private final Material secondary;

    public static final AntimatterItemTier NULL = new AntimatterItemTier(Data.NULL, Data.NULL);

    private static final Int2ObjectMap<AntimatterItemTier> TIERS_LOOKUP = new Int2ObjectOpenHashMap<>();

    static {
        TIERS_LOOKUP.put(NULL.hashCode(), NULL);
    }

    AntimatterItemTier(@Nonnull Material primary, @Nonnull Material secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public static Optional<AntimatterItemTier> get(int key) {
        return Optional.ofNullable(TIERS_LOOKUP.get(key));
    }

    public static AntimatterItemTier getOrCreate(String primaryName, String secondaryName) {
        return TIERS_LOOKUP.computeIfAbsent(Objects.hash(primaryName, secondaryName), m -> new AntimatterItemTier(Material.get(primaryName), Material.get(secondaryName)));
    }

    public static AntimatterItemTier getOrCreate(Material primary, Material secondary) {
        return TIERS_LOOKUP.computeIfAbsent(Objects.hash(primary.getHash(), secondary.getHash()), m -> new AntimatterItemTier(primary, secondary));
    }

    @Override
    public int getMaxUses() {
        return primary.getToolDurability() + secondary.getHandleDurability();
    }

    @Override
    public float getEfficiency() {
        return primary.getToolSpeed() + secondary.getHandleSpeed();
    }

    @Override
    public float getAttackDamage() { return primary.getToolDamage(); }

    @Override
    public int getHarvestLevel() {
        return primary.getToolQuality();
    }

    @Override
    public int getEnchantability() {
        return (int) (getHarvestLevel() + getEfficiency());
    }

    @Override
    public Ingredient getRepairMaterial() {
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
        return Ingredient.EMPTY;
        // return null;
    }

    public Material getPrimary() {
        return primary;
    }

    public Material getSecondary() {
        return secondary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        AntimatterItemTier tier = (AntimatterItemTier) obj;
        if (primary == tier.getPrimary() && secondary == tier.getSecondary()) return true;
        return false;
    }

    @Override
    public int hashCode() {
       return Objects.hash(primary.getHash(), secondary.getHash());
    }

}
