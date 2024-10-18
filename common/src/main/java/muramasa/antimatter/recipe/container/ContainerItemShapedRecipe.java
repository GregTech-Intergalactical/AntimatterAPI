package muramasa.antimatter.recipe.container;

import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.item.IContainerItem;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ContainerItemShapedRecipe extends ShapedRecipe {

    public static final Serializer INSTANCE = new Serializer();

    public static void init(){
        AntimatterAPI.register(RecipeSerializer.class, "container_shaped", Ref.ID, INSTANCE);
    }

    public ContainerItemShapedRecipe(ResourceLocation resourceLocation, String string, int i, int j, NonNullList<Ingredient> nonNullList, ItemStack itemStack) {
        super(resourceLocation, string, i, j, nonNullList, itemStack);
    }

    public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container) {
        if (AntimatterPlatformUtils.INSTANCE.isForge()) return super.getRemainingItems(container);
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = container.getItem(i);
            if (item.getItem() instanceof IContainerItem containerItem){
                if (containerItem.hasContainerItem(item)) {
                    nonnulllist.set(i, containerItem.getContainerItem(item));
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return INSTANCE;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        for(int i = 0; i <= inv.getWidth() - this.getWidth(); ++i) {
            for(int j = 0; j <= inv.getHeight() - this.getHeight(); ++j) {
                if (this.matches(inv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<ContainerItemShapedRecipe> {
        public Serializer() {
        }

        public ContainerItemShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String string = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] strings = shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            NonNullList<Ingredient> nonNullList = ShapedRecipe.dissolvePattern(strings, map, i, j);
            ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new ContainerItemShapedRecipe(recipeId, string, i, j, nonNullList, itemStack);
        }

        String[] shrink(String... toShrink) {
            int i = Integer.MAX_VALUE;
            int j = 0;
            int k = 0;
            int l = 0;

            for(int m = 0; m < toShrink.length; ++m) {
                String string = toShrink[m];
                i = Math.min(i, firstNonSpace(string));
                int n = lastNonSpace(string);
                j = Math.max(j, n);
                if (n < 0) {
                    if (k == m) {
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
                String[] strings = new String[toShrink.length - l - k];

                for(int o = 0; o < strings.length; ++o) {
                    strings[o] = toShrink[o + k].substring(i, j + 1);
                }

                return strings;
            }
        }

        private static int firstNonSpace(String entry) {
            int i;
            for(i = 0; i < entry.length() && entry.charAt(i) == ' '; ++i) {
            }

            return i;
        }

        private static int lastNonSpace(String entry) {
            int i;
            for(i = entry.length() - 1; i >= 0 && entry.charAt(i) == ' '; --i) {
            }

            return i;
        }

        public ContainerItemShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String string = buffer.readUtf();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i * j, Ingredient.EMPTY);

            nonNullList.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            ItemStack itemStack = buffer.readItem();
            return new ContainerItemShapedRecipe(recipeId, string, i, j, nonNullList, itemStack);
        }

        public void toNetwork(FriendlyByteBuf buffer, ContainerItemShapedRecipe recipe) {
            buffer.writeVarInt(recipe.getWidth());
            buffer.writeVarInt(recipe.getHeight());
            buffer.writeUtf(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
