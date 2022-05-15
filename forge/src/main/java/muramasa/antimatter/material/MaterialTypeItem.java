package muramasa.antimatter.material;

import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.util.Utils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MaterialTypeItem<T> extends MaterialType<T> {

    public interface ItemSupplier {
        void createItems(String domain, MaterialType<?> type, Material material);
    }

    private final ItemSupplier itemSupplier;

    public MaterialTypeItem(String id, int layers, boolean visible, int unitValue) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeItem.class, this);
        this.itemSupplier = MaterialItem::new;
    }

    public MaterialTypeItem(String id, int layers, boolean visible, int unitValue, ItemSupplier itemSupplier) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeItem.class, this);
        this.itemSupplier = itemSupplier;
    }

    public boolean allowItemGen(Material material) {
        return !replacements.containsKey(material) && allowGen(material) && !blockType;
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
        if (count < 1)
            Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return new ItemStack(get(material), count);
    }

    public RecipeIngredient getIngredient(Material material, int count) {
        if (count < 1)
            Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return RecipeIngredient.of(getMaterialTag(material), count);
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        super.onRegistryBuild(registry);
        if (doRegister()) {
            for (Material material : this.materials) {
                if (!material.enabled) continue;
                if (allowItemGen(material)) getSupplier().createItems(material.materialDomain(), this, material);
            }
        }
    }
}
