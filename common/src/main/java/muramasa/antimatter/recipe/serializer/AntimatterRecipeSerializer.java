package muramasa.antimatter.recipe.serializer;

import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeTag;
import muramasa.antimatter.recipe.RecipeUtil;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AntimatterRecipeSerializer implements RecipeSerializer<Recipe> {

    public static final AntimatterRecipeSerializer INSTANCE = new AntimatterRecipeSerializer();

    public static void init() {
        AntimatterAPI.register(RecipeSerializer.class, "machine", Ref.ID, INSTANCE);
    }

    @Override
    public Recipe fromJson(ResourceLocation recipeId, JsonObject json) {
        try {
            String mapId = json.get("map").getAsString();
            RecipeMap<?> map = AntimatterAPI.get(RecipeMap.class, mapId);
            if (map == null) throw new IllegalStateException("Recipe map: " + mapId + " is unknown");
            if (map.getRecipeSerializer() != null){
                return map.getRecipeSerializer().fromJson(recipeId, json);
            }
            List<Ingredient> list = new ObjectArrayList<>();
            if (json.has("inputItems")) {
                JsonArray array = json.getAsJsonArray("inputItems");
                for (JsonElement element : array) {
                    list.add(RecipeIngredient.fromJson(element));
                }
            }
            ItemStack[] outputs = null;
            if (json.has("outputItems")) {
                outputs = Streams.stream(json.getAsJsonArray("outputItems")).map(t -> RecipeUtil.getItemStack(t.getAsJsonObject(), true)).toArray(ItemStack[]::new);
            }
            List<FluidIngredient> fluidInputs = new ObjectArrayList<>();
            if (json.has("inputFluids")) {
                JsonArray array = json.getAsJsonArray("inputFluids");
                for (JsonElement element : array) {
                    fluidInputs.add(getFluidIngredient(element));
                }
            }
            FluidHolder[] fluidOutputs = null;
            if (json.has("outputFluids")) {
                fluidOutputs = Streams.stream(json.getAsJsonArray("outputFluids")).map(AntimatterRecipeSerializer::getStack).toArray(FluidHolder[]::new);
            }
            long eut = json.get("eu").getAsLong();
            int duration = json.get("duration").getAsInt();
            int amps = json.has("amps") ? json.get("amps").getAsInt() : 1;
            int special = json.has("special") ? json.get("special").getAsInt() : 0;
            Recipe r = new Recipe(list, outputs, fluidInputs, fluidOutputs, duration, eut, special, amps);
            if (json.has("outputChances")) {
                List<Integer> chances = new ObjectArrayList<>();
                for (JsonElement el : json.getAsJsonArray("outputChances")) {
                    chances.add(el.getAsInt());
                }
                r.addOutputChances(chances.stream().mapToInt(i -> i).toArray());
            }
            if (json.has("inputChances")) {
                List<Integer> chances = new ObjectArrayList<>();
                for (JsonElement el : json.getAsJsonArray("inputChances")) {
                    chances.add(el.getAsInt());
                }
                r.addInputChances(chances.stream().mapToInt(i -> i).toArray());
            }
            r.setHidden(json.get("hidden").getAsBoolean());
            r.setFake(json.get("fake").getAsBoolean());
            if (json.has("tags")){
                JsonArray array = json.getAsJsonArray("tags");
                Set<RecipeTag> tags = Streams.stream(array).map(e -> {
                    String[] strings = e.getAsString().split(":", 1);
                    return AntimatterAPI.get(RecipeTag.class, strings[1], strings[0]);
                }).collect(Collectors.toSet());
                r.addTags(tags);
            }
            r.setIds(recipeId, mapId);
            return r;
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
            Antimatter.LOGGER.error(json.toString());
        }
        return null;
    }
    public static FluidHolder getStack(JsonElement element) {
        try {
            if (!(element.isJsonObject())) {
                return FluidHooks.emptyFluid();
            }
            JsonObject obj = (JsonObject) element;
            ResourceLocation fluidName = new ResourceLocation(obj.get("fluid").getAsString());
            Fluid fluid = AntimatterPlatformUtils.getFluidFromID(fluidName);
            if (fluid == null) {
                return FluidHooks.emptyFluid();
            }
            FluidHolder stack = FluidPlatformUtils.createFluidStack(fluid, obj.has("amount") ? obj.get("amount").getAsLong() : 1000 * TesseractGraphWrappers.dropletMultiplier);

            if (obj.has("tag")) {
                stack.setCompound(TagParser.parseTag(obj.get("tag").getAsString()));
            }
            return stack;
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return FluidHooks.emptyFluid();
    }

    public static FluidIngredient getFluidIngredient(JsonElement element) {
        try {
            if (!(element.isJsonObject())) {
                return FluidIngredient.EMPTY;
            }
            JsonObject obj = (JsonObject) element;
            if (obj.has("fluidTag")) {
                ResourceLocation tagType = new ResourceLocation(obj.get("tag").getAsString());
                long amount = obj.has("amount") ? obj.get("amount").getAsLong() : 1000 * TesseractGraphWrappers.dropletMultiplier;
                return FluidIngredient.of(tagType, amount);
            }
            return FluidIngredient.of(getStack(element));
        } catch (Exception ex) {
            Antimatter.LOGGER.error(ex);
        }
        return FluidIngredient.EMPTY;
    }

    @Nullable
    @Override
    public Recipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String mapId = buffer.readUtf();
        RecipeMap<?> map = AntimatterAPI.get(RecipeMap.class, mapId);
        if (map != null && map.getRecipeSerializer() != null){
            return map.getRecipeSerializer().fromNetwork(recipeId, buffer);
        }
        int size = buffer.readInt();
        List<Ingredient> ings = new ObjectArrayList<>(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                ings.add(RecipeUtil.fromNetwork(buffer));
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
        FluidHolder[] outf = new FluidHolder[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                outf[i] = FluidPlatformUtils.INSTANCE.readFromPacket(buffer);
            }
        }
        size = buffer.readInt();
        int[] outputChances = new int[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                outputChances[i] = buffer.readInt();
            }
        }
        size = buffer.readInt();
        int[] inputChances = new int[size];
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                inputChances[i] = buffer.readInt();
            }
        }
        long power = buffer.readLong();
        int dur = buffer.readInt();
        int special = buffer.readInt();
        int amps = buffer.readInt();
        boolean hidden = buffer.readBoolean();
        boolean fake = buffer.readBoolean();

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
        if (outputChances.length > 0)
            r.addOutputChances(outputChances);
        if (inputChances.length > 0){
            r.addInputChances(inputChances);
        }
        r.setIds(recipeId, mapId);
        r.setHidden(hidden);
        r.setFake(fake);
        return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, Recipe recipe) {
        buffer.writeUtf(recipe.mapId);
        RecipeMap<?> map = AntimatterAPI.get(RecipeMap.class, recipe.mapId);
        if (map != null && map.getRecipeSerializer() != null){
            map.getRecipeSerializer().toNetwork(buffer, recipe);
            return;
        }
        buffer.writeInt(!recipe.hasInputItems() ? 0 : recipe.getInputItems().size());
        if (recipe.hasInputItems()) {
            recipe.getInputItems().forEach(t -> RecipeUtil.write(buffer, t));
        }
        buffer.writeInt(!recipe.hasOutputItems() ? 0 : recipe.getOutputItems(false).length);
        if (recipe.hasOutputItems()) {
            Arrays.stream(recipe.getOutputItems(false)).forEach(buffer::writeItem);
        }
        buffer.writeInt(!recipe.hasInputFluids() ? 0 : recipe.getInputFluids().size());
        if (recipe.hasInputFluids()) {
            recipe.getInputFluids().stream().forEach(t -> t.write(buffer));
        }
        buffer.writeInt(!recipe.hasOutputFluids() ? 0 : recipe.getOutputFluids().length);
        if (recipe.hasOutputFluids()) {
            Arrays.stream(recipe.getOutputFluids()).forEach(stack -> FluidPlatformUtils.INSTANCE.writeToPacket(buffer, stack));
        }
        buffer.writeInt(recipe.hasOutputChances() ? recipe.getOutputChances().length : 0);
        if (recipe.hasOutputChances()) {
            Arrays.stream(recipe.getOutputChances()).forEach(buffer::writeInt);
        }
        buffer.writeInt(recipe.hasInputChances() ? recipe.getInputChances().length : 0);
        if (recipe.hasInputChances()) {
            Arrays.stream(recipe.getInputChances()).forEach(buffer::writeInt);
        }
        buffer.writeLong(recipe.getPower());
        buffer.writeInt(recipe.getDuration());
        buffer.writeInt(recipe.getSpecialValue());
        buffer.writeInt(recipe.getAmps());
        buffer.writeBoolean(recipe.isHidden());
        buffer.writeBoolean(recipe.isFake());
    }
}
