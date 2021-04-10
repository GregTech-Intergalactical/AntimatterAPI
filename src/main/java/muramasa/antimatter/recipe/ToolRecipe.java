package muramasa.antimatter.recipe;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.recipe.ingredient.MaterialIngredient;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static muramasa.antimatter.Data.INGOT;
import static muramasa.antimatter.Data.PLATE;

public class ToolRecipe extends ShapedRecipe {

    private final Map<MaterialTypeItem, Set<Integer>> materialSlots;
    private final String toolType;
    private final boolean isArmor;

    public ToolRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, String toolType, boolean isArmor, Map<MaterialTypeItem, Set<Integer>> materialSlots) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        this.materialSlots = materialSlots;
        this.toolType = toolType;
        this.isArmor = isArmor;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
       if (build(inv) == null) return false;
        return super.matches(inv, worldIn);
    }

    private Map<MaterialTypeItem, Material> build(CraftingInventory inv) {
        for(int i = 0; i <= inv.getWidth() - this.getWidth(); ++i) {
            for(int j = 0; j <= inv.getHeight() - this.getHeight(); ++j) {
                Map<MaterialTypeItem, Material> m = this.build(inv, i, j, true);
                if (m != null) return m;
                m = this.build(inv, i, j, false);
                if (m != null) return m;
            }
        }
        return null;
    }

    private Map<MaterialTypeItem, Material> build(CraftingInventory inv, int width, int height, boolean someboolean) {
        Int2ObjectMap<Material> result = new Int2ObjectOpenHashMap<>();
        Map<MaterialTypeItem, Material> ret = new Object2ObjectOpenHashMap<>();
        for(int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < getWidth() && l < getHeight()) {
                    int offset;
                    if (someboolean)
                        offset = this.getRecipeWidth() - k - 1 + l * this.getRecipeWidth();
                    else
                        offset = k + l * this.getRecipeWidth();
                    ingredient = this.getIngredients().get(offset);
                    if (ingredient instanceof MaterialIngredient) {
                        result.put(offset, getMat(inv.getStackInSlot(i + j * inv.getWidth())));
                    }
                }
            }
        }
        for (Set<Integer> l : this.materialSlots.values()) {
            Material mat = null;
            for (int i : l) {
                Material innerMat = result.get(i);
                if (mat == null) {
                    mat = innerMat;
                    continue;
                }
                if (innerMat != mat) {
                    return null;
                }
            }
            ret.put(((MaterialIngredient)this.getIngredients().get(l.iterator().next())).getType(), mat);
        }
        return ret;
    }

    @Nullable
    private Material getMat(ItemStack stack) {
        if (!(stack.getItem() instanceof MaterialItem)) return null;
        return ((MaterialItem)stack.getItem()).getMaterial();
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        Map<MaterialTypeItem, Material> m = build(inv);
        if (isArmor) {
            return AntimatterAPI.get(AntimatterArmorType.class, toolType).getToolStack(m.get(PLATE));
        }
        return AntimatterAPI.get(AntimatterToolType.class, toolType).getToolStack(m.remove(INGOT), m.values().iterator().next());
    }

    public static class ToolRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ToolRecipe> {

        public static ToolRecipeSerializer INSTANCE = new ToolRecipeSerializer();
        static {
            INSTANCE.setRegistryName(new ResourceLocation(Ref.ID, "tool"));
        }
        static int MAX_WIDTH = 3;
        static int MAX_HEIGHT = 3;

        @Override
        public ToolRecipe read(ResourceLocation recipeId, JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] astring = shrink(patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = deserializeIngredients(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            boolean isArmor = AntimatterAPI.has(AntimatterArmorType.class, json.get("tool").getAsString());
            return new ToolRecipe(recipeId, s, i, j, nonnulllist, itemstack, json.get("tool").getAsString(), isArmor, buildMaterialInput(nonnulllist));
        }

        private static Map<MaterialTypeItem, Set<Integer>> buildMaterialInput(NonNullList<Ingredient> ingredients) {
            Map<MaterialTypeItem, Set<Integer>> ret = new Object2ObjectOpenHashMap<>();
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ing = ingredients.get(i);
                if (ing instanceof MaterialIngredient) {
                    int finalI1 = i;
                    ret.compute(((MaterialIngredient) ing).getType(), (s, v) -> {
                        if (v == null) v = new ObjectArraySet<>();
                        v.add(finalI1);
                        return v;
                    });
                }
            }
            return ret;
        }

        private static int firstNonSpace(String str) {
            int i;
            for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
            }

            return i;
        }

        private static int lastNonSpace(String str) {
            int i;
            for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
            }

            return i;
        }

        static String[] shrink(String... toShrink) {
            int i = Integer.MAX_VALUE;
            int j = 0;
            int k = 0;
            int l = 0;

            for (int i1 = 0; i1 < toShrink.length; ++i1) {
                String s = toShrink[i1];
                i = Math.min(i, firstNonSpace(s));
                int j1 = lastNonSpace(s);
                j = Math.max(j, j1);
                if (j1 < 0) {
                    if (k == i1) {
                        ++k;
                    }

                    ++l;
                } else {
                    l = 0;
                }
            }

            if (toShrink.length == l) {
                return new String[0];
            } else {
                String[] astring = new String[toShrink.length - l - k];

                for (int k1 = 0; k1 < astring.length; ++k1) {
                    astring[k1] = toShrink[k1 + k].substring(i, j + 1);
                }

                return astring;
            }
        }

        private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
            Set<String> set = Sets.newHashSet(keys.keySet());
            set.remove(" ");

            for (int i = 0; i < pattern.length; ++i) {
                for (int j = 0; j < pattern[i].length(); ++j) {
                    String s = pattern[i].substring(j, j + 1);
                    Ingredient ingredient = keys.get(s);
                    if (ingredient == null) {
                        throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                    }

                    set.remove(s);
                    nonnulllist.set(j + patternWidth * i, ingredient);
                }
            }

            if (!set.isEmpty()) {
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
            } else {
                return nonnulllist;
            }
        }

        private static Map<String, Ingredient> deserializeKey(JsonObject json) {
            Map<String, Ingredient> map = Maps.newHashMap();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                }

                if (" ".equals(entry.getKey())) {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }

                map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
            }

            map.put(" ", Ingredient.EMPTY);
            return map;
        }

        private static String[] patternFromJson(JsonArray jsonArr) {
            String[] astring = new String[jsonArr.size()];
            if (astring.length > MAX_HEIGHT) {
                throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
            } else if (astring.length == 0) {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            } else {
                for (int i = 0; i < astring.length; ++i) {
                    String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");
                    if (s.length() > MAX_WIDTH) {
                        throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                    }

                    if (i > 0 && astring[0].length() != s.length()) {
                        throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                    }

                    astring[i] = s;
                }

                return astring;
            }
        }

        @Nullable
        @Override
        public ToolRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String s = buffer.readString(32767);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, Ingredient.read(buffer));
            }

            ItemStack itemstack = buffer.readItemStack();
            String tool = buffer.readString();
            boolean armor = buffer.readBoolean();
            return new ToolRecipe(recipeId, s, i, j, nonnulllist, itemstack, tool, armor, buildMaterialInput(nonnulllist));
        }

        @Override
        public void write(PacketBuffer buffer, ToolRecipe recipe) {
            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeString(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());
            buffer.writeString(recipe.toolType);
            buffer.writeBoolean(recipe.isArmor);
        }
    }
}
