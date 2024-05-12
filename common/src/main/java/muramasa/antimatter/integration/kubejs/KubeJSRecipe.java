package muramasa.antimatter.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.integration.rei.REIUtils;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;

import java.util.List;

public class KubeJSRecipe extends RecipeJS {

    /*public final List<FluidIngredient> fluidInput = new ObjectArrayList<>();
    public final List<FluidHolder> fluidOutput = new ObjectArrayList<>();

    private int duration;
    private int special;
    private long power;
    private int amps;
    private boolean hidden, fake;
    private final List<Integer> outputChances = new ObjectArrayList<>(), inputChances = new ObjectArrayList<>();
    private String map;

    @Override
    public void create(ListJS listJS) {
        this.map = (String) listJS.get(0);
        RecipeMap<?> rMap = AntimatterAPI.get(RecipeMap.class, this.map);
        if (rMap == null){
            throw new IllegalArgumentException("Unknown recipe map");
        }
        if (listJS.get(1) != null) for (Object inputItem : ListJS.orSelf(listJS.get(1))) {
            if (inputItem instanceof MapJS map){
                this.inputItems.add(RecipeIngredientJS.fromJson(map.toJson()));
            } else {
                this.inputItems.add(IngredientJS.of(inputItem));
            }
        }
        if (listJS.get(2) != null) for (Object outputItem : ListJS.orSelf(listJS.get(2))) {
            this.outputItems.add(ItemStackJS.of(outputItem));
        }
        if (listJS.get(3) != null) for (Object inputFluid : ListJS.orSelf(listJS.get(3))) {
            if (inputFluid instanceof FluidStackJS fluidStack){
                this.fluidInput.add(FluidIngredient.of(REIUtils.fromREIFluidStack(fluidStack.getFluidStack())));
            } else if (inputFluid instanceof MapJS map){
                this.fluidInput.add(AntimatterRecipeSerializer.getFluidIngredient(map.toJson()));
            } else {
                throw new IllegalArgumentException("Invalid entry type in fluid output");
            }

        }
        if (listJS.get(4) != null) for (Object outputFluid : ListJS.orSelf(listJS.get(4))) {
            if (outputFluid instanceof FluidStackJS fluidStack){
                this.fluidOutput.add(REIUtils.fromREIFluidStack(fluidStack.getFluidStack()));
            } else if (outputFluid instanceof MapJS map){
                this.fluidOutput.add(AntimatterRecipeSerializer.getStack(map.toJson()));
            } else {
                throw new IllegalArgumentException("Invalid entry type in fluid output");
            }
        }
        duration = ((Number) listJS.get(5)).intValue();
        power = ((Number) listJS.get(6)).longValue();
        hidden = false;
        fake = false;
        if (listJS.size() > 7) {
            amps = ((Number) listJS.get(7)).intValue();
            special = ((Number) listJS.get(8)).intValue();
            if (listJS.size() > 9) {
                for (Object chance : ListJS.orSelf(listJS.get(9))) {
                    this.outputChances.add(((Number) chance).intValue());
                }
            }
            if (listJS.size() > 10){
                for (Object chance : ListJS.orSelf(listJS.get(9))) {
                    this.inputChances.add(((Number) chance).intValue());
                }
            }
        } else {
            amps = 1;
            special = 0;
        }
        if (inputItems.size() == 0 && fluidInput.size() == 0) {
            throw new IllegalStateException("No input in recipe");
        }
    }

    @Override
    public void deserialize() {
        this.map = GsonHelper.getAsString(json, "map");
        RecipeMap<?> map = AntimatterAPI.get(RecipeMap.class, this.map);
        if (map != null && map.getRecipeSerializer() != null){
            //return map.getRecipeSerializer().fromJson(id, json);
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "inputItems", new JsonArray())) {
            this.inputItems.add(RecipeIngredientJS.fromJson(e));
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "outputItems", new JsonArray())) {
            this.outputItems.add(ItemStackJS.of(e));
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "inputFluids", new JsonArray())) {
            this.fluidInput.add(AntimatterRecipeSerializer.getFluidIngredient(e));
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "outputFluids", new JsonArray())) {
            this.fluidOutput.add(AntimatterRecipeSerializer.getStack(e));
        }
        this.duration = GsonHelper.getAsInt(json, "duration");
        this.special = GsonHelper.getAsInt(json, "special", 0);
        this.power = GsonHelper.getAsInt(json, "eu");
        this.amps = GsonHelper.getAsInt(json, "amps", 1);
        this.hidden = GsonHelper.getAsBoolean(json, "hidden");
        this.fake = GsonHelper.getAsBoolean(json, "fake");

        for (JsonElement e : GsonHelper.getAsJsonArray(json, "outputChances", new JsonArray())) {
            this.outputChances.add(e.getAsInt());
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "inputChances", new JsonArray())) {
            this.inputChances.add(e.getAsInt());
        }
    }

    public static JsonElement serializeStack(FluidHolder stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("fluid", FluidPlatformUtils.INSTANCE.getFluidId(stack.getFluid()).toString());
        obj.addProperty("amount", stack.getFluidAmount());
        if (stack.getCompound() != null) {
            obj.add("tag", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.getCompound()));
        }
        return obj;
    }

    @Override
    public @Nullable JsonElement serializeIngredientStack(IngredientStackJS in) {
        JsonElement element;
        if (in.ingredient instanceof MatchAnyIngredientJS js){
            var object = new JsonObject();
            JsonArray array = new JsonArray();
            js.ingredients.forEach(i -> array.add(i.toJson()));
            object.add("values", array);
            element = object;
        } else {
            element = in.ingredient.toJson();
        }
        if (element instanceof JsonObject object && in.getCount() > 1){
            object.addProperty(in.countKey, in.getCount());
        }
        return element;
    }

    public static JsonElement serializeFluid(FluidIngredient stack) {
        return stack.toJson();
    }

    @Override
    public void serialize() {
        if (inputItems.size() > 0) {
            JsonArray arr = new JsonArray();
            inputItems.forEach(t -> arr.add(t.toJson()));
            this.json.add("inputItems", arr);
        }
        if (outputItems.size() > 0) {
            JsonArray arr = new JsonArray();
            outputItems.forEach(t -> arr.add(t.toResultJson()));
            this.json.add("outputItems", arr);
        }
        if (fluidInput.size() > 0) {
            JsonArray arr = new JsonArray();
            fluidInput.forEach(t -> arr.add(serializeFluid(t)));
            this.json.add("inputFluids", arr);
        }
        if (fluidOutput.size() > 0) {
            JsonArray arr = new JsonArray();
            fluidOutput.forEach(t -> arr.add(serializeStack(t)));
            this.json.add("outputFluids", arr);
        }
        if (outputChances.size() > 0) {
            JsonArray arr = new JsonArray();
            outputChances.forEach(arr::add);
            this.json.add("outputChances", arr);
        }
        if (inputChances.size() > 0) {
            JsonArray arr = new JsonArray();
            inputChances.forEach(arr::add);
            this.json.add("inputChances", arr);
        }
        this.json.addProperty("eu", this.power);
        this.json.addProperty("duration", this.duration);
        this.json.addProperty("amps", this.amps);
        this.json.addProperty("special", this.special);
        this.json.addProperty("hidden", this.hidden);
        this.json.addProperty("map", this.map);
        this.json.addProperty("fake", this.fake);
    }*/
}
