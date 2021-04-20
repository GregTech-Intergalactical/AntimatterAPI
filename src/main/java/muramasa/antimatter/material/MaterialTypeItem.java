package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;

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
        return !OVERRIDES.containsKey(material) && allowGen(material) && !blockType;
    }

    public Item get(Material material) {
        Item replacement = AntimatterAPI.getReplacement(this, material);
        if (replacement == null) {
            if (!allowItemGen(material))
                Utils.onInvalidData(String.join("", "GET ERROR - DOES NOT GENERATE: T(", id, ") M(", material.getId(), ")"));
            else return AntimatterAPI.get(MaterialItem.class, id + "_" + material.getId());
        }
        return replacement;
    }

    public ItemSupplier getSupplier() {
        return itemSupplier;
    }

    public ItemStack get(Material material, int count) {
        if (count < 1) Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return new ItemStack(get(material), count);
    }

    public RecipeIngredient getIngredient(Material material, int count) {
        if (count < 1) Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return RecipeIngredient.of(new LazyValue<>(() -> new ItemStack(get(material), count)), count);
    }
}
