package muramasa.antimatter.recipe.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.recipe.ingredient.MaterialIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;


public class MaterialRecipe extends ShapedRecipe {

    public abstract static class ItemBuilder implements IAntimatterObject {

        @Override
        public String getDomain() {
            return Ref.ID;
        }

        public ItemBuilder() {
            AntimatterAPI.register(ItemBuilder.class, this);
        }
        public abstract ItemStack build(CraftingInventory inv, Map<String, Material> mats);

    }
    public final Map<String, Set<Integer>> materialSlots;
    private final ItemBuilder builder;
    public final ResourceLocation builderId;

    public MaterialRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, String builderId, Map<String, Set<Integer>> materialSlots) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        this.materialSlots = ImmutableMap.copyOf(materialSlots);
        this.builderId = new ResourceLocation(builderId);
        this.builder = AntimatterAPI.get(ItemBuilder.class, this.builderId.getPath());
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
       return build(inv, true) != null;
    }

    private Map<String, Material> build(CraftingInventory inv, boolean regularTest) {
        for(int i = 0; i <= inv.getWidth() - this.getWidth(); ++i) {
            for(int j = 0; j <= inv.getHeight() - this.getHeight(); ++j) {
                Map<String, Material> m = this.build(inv, i, j,regularTest, true);
                if (m != null) return m;
                m = this.build(inv, i, j,regularTest, false);
                if (m != null) return m;
            }
        }
        return null;
    }

    private Map<String, Material> build(CraftingInventory inv, int width, int height, boolean regularTest, boolean someBooleanIDunno) {
        Int2ObjectMap<Material> result = new Int2ObjectOpenHashMap<>();
        Map<String, Material> ret = new Object2ObjectOpenHashMap<>();
        for(int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient;
                if (k >= 0 && l >= 0 && k < getWidth() && l < getHeight()) {
                    int offset;
                    if (someBooleanIDunno)
                        offset = this.getRecipeWidth() - k - 1 + l * this.getRecipeWidth();
                    else
                        offset = k + l * this.getRecipeWidth();
                    ingredient = this.getIngredients().get(offset);
                    if (ingredient instanceof MaterialIngredient) {
                        result.put(offset, getMat(((MaterialIngredient)ingredient).getType(), inv.getStackInSlot(i + j * inv.getWidth())));
                    }
                    if (regularTest && !ingredient.test(inv.getStackInSlot(i + j * inv.getWidth()))) {
                        return null;
                    }
                }
            }
        }
        for (Set<Integer> l : this.materialSlots.values()) {
            Material mat = null;
            for (int i : l) {
                Material innerMat = result.get(i);
                if (innerMat == null) {
                    return null;
                }
                if (mat == null) {
                    mat = innerMat;
                    continue;
                }
                if (innerMat != mat) {
                    return null;
                }
            }
            ret.put(((MaterialIngredient)this.getIngredients().get(l.iterator().next())).getId(), mat);
        }
        for (Material value : ret.values()) {
            if (value == null) return null;
        }
        return ret;
    }

    @Nullable
    private Material getMat(MaterialType type, ItemStack stack) {
        return type.tryMaterialFromItem(stack);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        Map<String, Material> m = build(inv, false);

        return this.builder.build(inv, m);
    }

}
