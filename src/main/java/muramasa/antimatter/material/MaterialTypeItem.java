package muramasa.antimatter.material;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.Utils;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;

public class MaterialTypeItem<T> extends MaterialType<T> {

    public interface ItemSupplier {
        void createItems(String domain, MaterialType<?> type, Material material);
    }

    private final ItemSupplier itemSupplier;

    public MaterialTypeItem(String id, int layers, boolean visible, int unitValue) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeItem.class, this);
        this.itemSupplier = (domain, type, material) -> new MaterialItem(domain, type, material);
    }

    public MaterialTypeItem(String id, int layers, boolean visible, int unitValue, ItemSupplier itemSupplier) {
        super(id, layers, visible, unitValue);
        AntimatterAPI.register(MaterialTypeItem.class, this);
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
        if (count < 1)
            Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return new ItemStack(get(material), count);
    }

    public RecipeIngredient getIngredient(Material material, int count) {
        if (count < 1)
            Utils.onInvalidData(String.join("", "GET ERROR - MAT STACK EMPTY: T(", id, ") M(", material.getId(), ")"));
        return RecipeIngredient.of(() -> new ItemStack(get(material), count), count);
    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        super.onRegistryBuild(registry);
        if (doRegister()) {
            for (Material material : this.materials) {
                if (!material.enabled) continue;
                getSupplier().createItems(material.materialDomain(), this, material);
            }
        }
    }
}
