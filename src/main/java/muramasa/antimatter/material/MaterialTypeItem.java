package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.LazyValue;

import java.util.Arrays;
import java.util.function.Supplier;

public class MaterialTypeItem<T> extends MaterialType<T> {

    public interface ItemSupplier {
        MaterialItem supply(String domain, MaterialType<?> type, Material material);
    }

    private final ItemSupplier itemSupplier;

    public MaterialTypeItem(String id, int layers, boolean visible, int unitValue) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeItem.class, id, this);
        this.itemSupplier = MaterialItem::new;
    }

    public MaterialTypeItem(String id, int layers, boolean visible, int unitValue, ItemSupplier itemSupplier) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeItem.class, id, this);
        this.itemSupplier = itemSupplier;
    }

    public boolean allowItemGen(Material material) {
        return !OVERRIDES.contains(material) && allowGen(material) && !blockType && AntimatterAPI.getReplacement(this, material) == null;
    }

    public Item get(Material material) {
        LazyValue<AntimatterIngredient> replacement = AntimatterAPI.getReplacement(this, material);
        if (replacement == null) {
            if (!allowItemGen(material))
                Utils.onInvalidData(String.join("", "GET ERROR - DOES NOT GENERATE: T(", id, ") M(", material.getId(), ")"));
            else return AntimatterAPI.get(MaterialItem.class, id + "_" + material.getId());
        }
        return null;
    }

    public ItemSupplier getSupplier() {
        return itemSupplier;
    }


    //TODO THIS DOESNT WORK
    /**
     * Forces these tags to not generate, assuming they have a replacement.
     * @param tags
     */
    public void forceOverride(Material... tags) {
        OVERRIDES.addAll(Arrays.asList(tags));
    }

    public ItemStack get(Material material, int count) {
        if (count < 1) Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return new ItemStack(get(material), count);
    }

    public LazyValue<AntimatterIngredient> getIngredient(Material material, int count) {
        if (count < 1) Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return AntimatterIngredient.fromStack(new LazyValue<>(() -> new ItemStack(get(material), count)));
    }

    public ITag.INamedTag<Item> getMaterialTag(Material m) {
        return Utils.getForgeItemTag(String.join("", Utils.getConventionalMaterialType(this), "/", m.getId()));
    }

    public LazyValue<AntimatterIngredient> getMaterialIngredient(Material m, int count) {
        return AntimatterIngredient.of(Utils.getForgeItemTag(String.join("", Utils.getConventionalMaterialType(this), "/", m.getId())),count);
    }

    public LazyValue<AntimatterIngredient> getMaterialIngredient(Material m) {
        return getMaterialIngredient(m,1);
    }
}
