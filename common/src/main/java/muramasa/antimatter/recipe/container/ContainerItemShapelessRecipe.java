package muramasa.antimatter.recipe.container;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class ContainerItemShapelessRecipe extends ShapelessRecipe {
    public static final Serializer INSTANCE = new Serializer();

    public static void init(){
        AntimatterAPI.register(RecipeSerializer.class, "container_shapeless", Ref.ID, INSTANCE);
    }
    public ContainerItemShapelessRecipe(ResourceLocation resourceLocation, String string, ItemStack itemStack, NonNullList<Ingredient> nonNullList) {
        super(resourceLocation, string, itemStack, nonNullList);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return INSTANCE;
    }

    public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer container) {
        if (AntimatterPlatformUtils.isForge()) return super.getRemainingItems(container);
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

    public static class Serializer implements RecipeSerializer<ContainerItemShapelessRecipe> {
        public Serializer() {
        }

        public ContainerItemShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String string = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> nonNullList = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (nonNullList.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonNullList.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                ItemStack itemStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                return new ContainerItemShapelessRecipe(recipeId, string, itemStack, nonNullList);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonNullList = NonNullList.create();

            for(int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonNullList.add(ingredient);
                }
            }

            return nonNullList;
        }

        public ContainerItemShapelessRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String string = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonNullList.size(); ++j) {
                nonNullList.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack itemStack = buffer.readItem();
            return new ContainerItemShapelessRecipe(recipeId, string, itemStack, nonNullList);
        }

        public void toNetwork(FriendlyByteBuf buffer, ContainerItemShapelessRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.getResultItem());
        }
    }
}
