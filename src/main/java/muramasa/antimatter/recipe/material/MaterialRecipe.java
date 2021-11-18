package muramasa.antimatter.recipe.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class MaterialRecipe extends ShapedRecipe {

    private final static Map<Provider, ResourceLocation> IDS = new Object2ObjectOpenHashMap<>();

    public interface Provider {
        ItemBuilder provide(String id);

        default String get(String identifier) {
            if (identifier.contains("/"))
                throw new RuntimeException("invalid input identifier to MaterialRecipe.Provider, contains /");
            return IDS.get(this) + "/" + identifier;
        }
    }

    public static Provider registerProvider(String loc, String domain, Provider obj) {
        if (loc.contains("/"))
            throw new RuntimeException("invalid input identifier to MaterialRecipe.Provider, contains /");
        AntimatterAPI.register(Provider.class, loc, domain, obj);
        IDS.put(obj, new ResourceLocation(domain, loc));
        return obj;
    }

    public interface ItemBuilder {
        ItemStack build(CraftingInventory inv, Result mats);

        Map<String, Object> getFromResult(@Nonnull ItemStack stack);
    }

    public final Map<String, Set<Integer>> materialSlots;
    public final ItemBuilder builder;
    private final int size;
    public final String builderId;
    public final NonNullList<ItemStack> outputs;

    public MaterialRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, NonNullList<ItemStack> recipeOutputIn, String builderId, Map<String, Set<Integer>> materialSlots) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn.get(0));
        this.materialSlots = ImmutableMap.copyOf(materialSlots);
        this.size = materialSlots.values().stream().mapToInt(Set::size).sum();
        String[] ids = builderId.split("/");
        ResourceLocation lookup = new ResourceLocation(ids[0]);
        this.builderId = builderId;
        this.builder = Objects.requireNonNull(AntimatterAPI.get(Provider.class, lookup.getPath(), lookup.getNamespace()), "Failed to get builder provider in MaterialRecipe" + builderId).provide(ids[1]);
        this.outputs = recipeOutputIn;
    }

    /*@Override
    public IRecipeType<?> getType() {
        return TYPE;
    }*/

    public List<ItemStack> stacksToLookup() {
        return outputs;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return build(inv, true) != null;
    }

    private Result build(CraftingInventory inv, boolean regularTest) {
        for (int i = 0; i <= inv.getWidth() - this.getWidth(); ++i) {
            for (int j = 0; j <= inv.getHeight() - this.getHeight(); ++j) {
                //Result m = this.build(inv, i, j,regularTest, true);
                //if (m != null) return m;
                Result m = this.build(inv, i, j, regularTest, false);
                if (m != null) return m;
            }
        }
        return null;
    }

    private Result build(CraftingInventory inv, int width, int height, boolean regularTest, boolean mirrored) {
        Int2ObjectMap<Object> result = new Int2ObjectOpenHashMap<>(size);
        Map<String, Object> ret = new Object2ObjectOpenHashMap<>(5, 0.25f);
        Map<Ingredient, ItemStack> whichStacks = new Object2ObjectOpenHashMap<>(5, 0.25f);
        for (int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient;
                if (k >= 0 && l >= 0 && k < getWidth() && l < getHeight()) {
                    int offset;
                    if (mirrored)
                        offset = this.getRecipeWidth() - k - 1 + l * this.getRecipeWidth();
                    else
                        offset = k + l * this.getRecipeWidth();
                    ingredient = this.getIngredients().get(offset);
                    ItemStack stack = inv.getItem(i + j * inv.getWidth());
                    if (ingredient instanceof PropertyIngredient) {
                        Object obj = getMat((PropertyIngredient) ingredient, stack);
                        if (obj == null) return null;
                        result.put(offset, obj);
                    }
                    if (regularTest && !ingredient.test(stack)) {
                        return null;
                    }
                    whichStacks.put(ingredient, stack);
                } else {
                    if (regularTest && !Ingredient.EMPTY.test(inv.getItem(i + j * inv.getWidth()))) {
                        return null;
                    }
                }
            }
        }
        for (Set<Integer> l : this.materialSlots.values()) {
            Object mat = null;
            for (int i : l) {
                Object innerMat = result.get(i);
                if (innerMat == null) {
                    return null;
                }
                if (mat == null) {
                    mat = innerMat;
                    continue;
                }
                if (!innerMat.equals(mat)) {
                    return null;
                }
            }
            ret.put(((PropertyIngredient) this.getIngredients().get(l.iterator().next())).getId(), mat);
        }
        for (Object value : ret.values()) {
            if (value == null) return null;
        }
        return new Result(ret, whichStacks);
    }

    @Nullable
    public static Object getMat(PropertyIngredient type, ItemStack stack) {
        return type.getMat(stack);
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        Result m = build(inv, false);

        return this.builder.build(inv, m);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return MaterialSerializer.INSTANCE;
    }

    public static final class Result {
        public final Map<String, Object> mats;
        public final Map<Ingredient, ItemStack> items;

        public Result(Map<String, Object> mats, Map<Ingredient, ItemStack> items) {
            this.mats = mats;
            this.items = items;
        }
    }

}
