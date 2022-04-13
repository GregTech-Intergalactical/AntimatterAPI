package muramasa.antimatter.recipe.serializer;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AntimatterRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<Recipe> {

    public static final AntimatterRecipeSerializer INSTANCE = new AntimatterRecipeSerializer();

    static {
        INSTANCE.setRegistryName(new ResourceLocation(Ref.ID, "machine"));
    }

    @Override
    public Recipe fromJson(ResourceLocation recipeId, JsonObject json) {
        try {
            List<Ingredient> list = new ObjectArrayList<>();
            if (json.has("item_in")) {
                JsonArray array = json.getAsJsonArray("item_in");
                for (JsonElement element : array) {
                    list.add(Ingredient.fromJson(element));
                }
            }
            ItemStack[] outputs = null;
            if (json.has("item_out")) {
                outputs = Streams.stream(json.getAsJsonArray("item_out")).map(t -> CraftingHelper.getItemStack(t.getAsJsonObject(), true)).toArray(ItemStack[]::new);
            }
            FluidStack[] fluidInputs = null;
            if (json.has("fluid_in")) {
                fluidInputs = Streams.stream(json.getAsJsonArray("fluid_in")).map(AntimatterRecipeSerializer::getStack).toArray(FluidStack[]::new);
            }
            FluidStack[] fluidOutputs = null;
            if (json.has("fluid_out")) {
                fluidOutputs = Streams.stream(json.getAsJsonArray("fluid_out")).map(AntimatterRecipeSerializer::getStack).toArray(FluidStack[]::new);
            }
            long eut = json.get("eu").getAsLong();
            int duration = json.get("duration").getAsInt();
            int amps = json.has("amps") ? json.get("amps").getAsInt() : 1;
            int special = json.has("special") ? json.get("special").getAsInt() : 0;
            Recipe r = new Recipe(list, outputs, fluidInputs == null ? Collections.emptyList() : Arrays.stream(fluidInputs).map(FluidIngredient::of).toList(), fluidOutputs, duration, eut, special, amps);
            if (json.has("chances")) {
                List<Integer> chances = new ObjectArrayList<>();
                for (JsonElement el : json.getAsJsonArray("chances")) {
                    chances.add(el.getAsInt());
                }
                r.addChances(chances.stream().mapToInt(i -> i).toArray());
            }
            r.setIds(recipeId, json.get("map").getAsString());
            return r;
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return null;
    }
    public static FluidStack getStack(JsonElement element) {
        try {
            if (!(element.isJsonObject())) {
                return FluidStack.EMPTY;
            }
            JsonObject obj = (JsonObject) element;
            ResourceLocation fluidName = new ResourceLocation(obj.get("fluid").getAsString());
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
            if (fluid == null) {
                return FluidStack.EMPTY;
            }
            FluidStack stack = new FluidStack(fluid, obj.has("amount") ? obj.get("amount").getAsInt() : 1000);

            if (obj.has("tag")) {
                stack.setTag(TagParser.parseTag(obj.get("tag").getAsString()));
            }
            return stack;
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return FluidStack.EMPTY;
    }

    public static FluidIngredient getFluidIngredient(JsonElement element) {
        try {
            if (!(element.isJsonObject())) {
                return FluidIngredient.EMPTY;
            }
            JsonObject obj = (JsonObject) element;
            if (obj.has("fluidTag")) {
                ResourceLocation tagType = new ResourceLocation(obj.get("tag").getAsString());
                int amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1000;
                return FluidIngredient.of(tagType, amount);
            }
            ResourceLocation fluidName = new ResourceLocation(obj.get("fluid").getAsString());
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
            if (fluid == null) {
                return FluidIngredient.EMPTY;
            }
            FluidStack stack = new FluidStack(fluid, obj.has("amount") ? obj.get("amount").getAsInt() : 1000);

            if (obj.has("tag")) {
                stack.setTag(TagParser.parseTag(obj.get("tag").getAsString()));
            }
            return FluidIngredient.of(stack);
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return FluidIngredient.EMPTY;
    }

    @Nullable
    @Override
    public Recipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<Ingredient> ings = new ObjectArrayList<>(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                ings.add(Ingredient.fromNetwork(buffer));
            }
        }
        size = buffer.readInt();
        ItemStack[] out = new ItemStack[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                out[i] = buffer.readItem();
            }
        }
        size = buffer.readInt();
        List<FluidIngredient> in = new ObjectArrayList<>(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                in.add(FluidIngredient.of(buffer));
            }
        }
        size = buffer.readInt();
        FluidStack[] outf = new FluidStack[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                outf[i] = FluidStack.readFromPacket(buffer);
            }
        }
        size = buffer.readInt();
        int[] chances = new int[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                chances[i] = buffer.readInt();
            }
        }
        long power = buffer.readLong();
        int dur = buffer.readInt();
        int special = buffer.readInt();
        int amps = buffer.readInt();
        String map = buffer.readUtf();
        ResourceLocation id = buffer.readResourceLocation();

        Recipe r = new Recipe(
                ings,
                out.length == 0 ? null : out,
                in,
                outf.length == 0 ? null : outf,
                dur,
                power,
                special,
                amps
        );
        if (chances.length > 0)
            r.addChances(chances);
        r.setIds(id, map);
        return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, Recipe recipe) {
        buffer.writeInt(!recipe.hasInputItems() ? 0 : recipe.getInputItems().size());
        if (recipe.hasInputItems()) {
            recipe.getInputItems().forEach(t -> CraftingHelper.write(buffer, t));
        }
        buffer.writeInt(!recipe.hasOutputItems() ? 0 : recipe.getOutputItems().length);
        if (recipe.hasOutputItems()) {
            Arrays.stream(recipe.getOutputItems()).forEach(buffer::writeItem);
        }
        buffer.writeInt(!recipe.hasInputFluids() ? 0 : recipe.getInputFluids().size());
        if (recipe.hasInputFluids()) {
            recipe.getInputFluids().stream().forEach(t -> t.write(buffer));
        }
        buffer.writeInt(!recipe.hasOutputFluids() ? 0 : recipe.getOutputFluids().length);
        if (recipe.hasOutputFluids()) {
            Arrays.stream(recipe.getOutputFluids()).forEach(buffer::writeFluidStack);
        }
        buffer.writeInt(recipe.hasChances() ? recipe.getChances().length : 0);
        if (recipe.hasChances()) {
            Arrays.stream(recipe.getChances()).forEach(buffer::writeInt);
        }
        buffer.writeLong(recipe.getPower());
        buffer.writeInt(recipe.getDuration());
        buffer.writeInt(recipe.getSpecialValue());
        buffer.writeInt(recipe.getAmps());
        buffer.writeUtf(recipe.mapId);
        buffer.writeResourceLocation(recipe.id);
    }
}
