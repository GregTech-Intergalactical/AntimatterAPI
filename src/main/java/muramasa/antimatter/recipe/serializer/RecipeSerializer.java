package muramasa.antimatter.recipe.serializer;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

public class RecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<Recipe> {

    public static final RecipeSerializer INSTANCE = new RecipeSerializer();

    static {
        INSTANCE.setRegistryName(new ResourceLocation(Ref.ID, "machine"));
    }

    @Override
    public Recipe read(ResourceLocation recipeId, JsonObject json) {
        try {
            ItemStack[] outputs = null;
            if (json.has("item_out")) {
                outputs = Streams.stream(json.getAsJsonArray("item_out")).map(t -> CraftingHelper.getItemStack(t.getAsJsonObject(), true)).toArray(ItemStack[]::new);
            }
            FluidStack[] fluidInputs = null;
            if (json.has("fluid_in")) {
                fluidInputs = Streams.stream(json.getAsJsonArray("fluid_in")).map(this::getStack).toArray(FluidStack[]::new);
            }
            FluidStack[] fluidOutputs = null;
            if (json.has("fluid_out")) {
                fluidOutputs = Streams.stream(json.getAsJsonArray("fluid_out")).map(this::getStack).toArray(FluidStack[]::new);
            }
            List<RecipeIngredient> list = new ObjectArrayList<>();
            if (json.has("item_in")) {
                JsonArray array = json.getAsJsonArray("item_in");
                for (JsonElement element : array) {
                    Ingredient i = CraftingHelper.getIngredient(element);
                    if (i instanceof AntimatterIngredient) {
                        list.add(new RecipeIngredient(() -> (AntimatterIngredient) i));
                    } else {{
                        list.add(AntimatterIngredient.of(i.getMatchingStacks()[0].getCount(),i.getMatchingStacks()));
                    }}
                }
            }
            long eut = json.get("euT").getAsLong();
            int duration = json.get("duration").getAsInt();
            int amps = json.has("amps") ? json.get("amps").getAsInt() : 1;
            Recipe r = new Recipe(list, outputs, fluidInputs, fluidOutputs, duration, eut, 0, amps);
            r.id = recipeId;
            return r;
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return null;
    }

    private FluidStack getStack(JsonElement element) {
        try {
            return FluidStack.loadFluidStackFromNBT(JsonToNBT.getTagFromJson(element.getAsString()));
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return FluidStack.EMPTY;
    }

    @Nullable
    @Override
    public Recipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, Recipe recipe) {

    }
}
