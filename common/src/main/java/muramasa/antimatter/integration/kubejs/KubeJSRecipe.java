package muramasa.antimatter.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.List;

public class KubeJSRecipe extends RecipeJS {

    public final List<FluidIngredient> fluidInput = new ObjectArrayList<>();
    public final List<FluidStack> fluidOutput = new ObjectArrayList<>();

    private int duration;
    private int special;
    private long power;
    private int amps;
    private final List<Integer> chances = new ObjectArrayList<>();
    private String map;

    @Override
    public void create(ListJS listJS) {
        this.map = (String) listJS.get(0);
        if (listJS.get(1) != null) for (Object inputItem : ListJS.orSelf(listJS.get(1))) {
            this.inputItems.add(IngredientJS.of(inputItem));
            /*if (inputItem instanceof ItemStackJS i) {
                this.inputItems.add(IngredientStackJS.stackOf(i));
            } else if (inputItem instanceof MapJS map) {
                this.inputItems.add(IngredientJS.of(map));
            } else if (inputItem instanceof JsonElement) {
                this.inputItems.add(RecipeIngredientJS.of((JsonElement) inputItem));
            } else if (inputItem instanceof IngredientJS) {
                this.inputItems.add(RecipeIngredientJS.of((IngredientJS) inputItem));
            }*/
        }
        if (listJS.get(2) != null) for (Object outputItem : ListJS.orSelf(listJS.get(2))) {
            this.outputItems.add(ItemStackJS.of(outputItem));
        }
        if (listJS.get(3) != null) for (Object inputFluid : ListJS.orSelf(listJS.get(3))) {
            MapJS map = (MapJS) inputFluid;
            this.fluidInput.add(AntimatterRecipeSerializer.getFluidIngredient(map.toJson()));
        }
        if (listJS.get(4) != null) for (Object outputFluid : ListJS.orSelf(listJS.get(4))) {
            MapJS map = (MapJS) outputFluid;
            this.fluidOutput.add(AntimatterRecipeSerializer.getStack(map.toJson()));
        }
        duration = ((Number) listJS.get(5)).intValue();
        power = ((Number) listJS.get(6)).longValue();

        if (listJS.size() > 7) {
            amps = ((Number) listJS.get(7)).intValue();
            special = ((Number) listJS.get(8)).intValue();
            if (listJS.size() > 9) {
                for (Object chance : ListJS.orSelf(listJS.get(9))) {
                    this.chances.add(((Number) chance).intValue());
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
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "item_in", new JsonArray())) {
            this.inputItems.add(IngredientJS.of(e));
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "item_out", new JsonArray())) {
            this.outputItems.add(ItemStackJS.of(e));
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "fluid_in", new JsonArray())) {
            this.fluidInput.add(AntimatterRecipeSerializer.getFluidIngredient(e));
        }
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "fluid_out", new JsonArray())) {
            this.fluidOutput.add(AntimatterRecipeSerializer.getStack(e));
        }
        this.duration = GsonHelper.getAsInt(json, "duration");
        this.special = GsonHelper.getAsInt(json, "special", 0);
        this.power = GsonHelper.getAsInt(json, "eu");
        this.amps = GsonHelper.getAsInt(json, "amps", 1);
        this.map = GsonHelper.getAsString(json, "map");

        for (JsonElement e : GsonHelper.getAsJsonArray(json, "chances", new JsonArray())) {
            this.chances.add(e.getAsInt());
        }
    }

    public static JsonElement serializeStack(FluidStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("fluid", FluidPlatformUtils.getFluidId(stack.getFluid()).toString());
        obj.addProperty("amount", stack.getAmount());
        if (stack.hasTag()) {
            obj.add("tag", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.getTag()));
        }
        return obj;
    }

    public static JsonElement serializeFluid(FluidIngredient stack) {
        JsonArray obj = new JsonArray();
        for (FluidStack fluidStack : stack.getStacks()) {
            obj.add(serializeStack(fluidStack));
        }
        return obj;
    }

    @Override
    public void serialize() {
        if (inputItems.size() > 0) {
            JsonArray arr = new JsonArray();
            inputItems.forEach(t -> arr.add(t.toJson()));
            this.json.add("item_in", arr);
        }
        if (outputItems.size() > 0) {
            JsonArray arr = new JsonArray();
            outputItems.forEach(t -> arr.add(t.toResultJson()));
            this.json.add("item_out", arr);
        }
        if (fluidInput.size() > 0) {
            JsonArray arr = new JsonArray();
            fluidInput.forEach(t -> arr.add(serializeFluid(t)));
            this.json.add("fluid_in", arr);
        }
        if (fluidOutput.size() > 0) {
            JsonArray arr = new JsonArray();
            fluidOutput.forEach(t -> arr.add(serializeStack(t)));
            this.json.add("fluid_out", arr);
        }
        if (chances.size() > 0) {
            JsonArray arr = new JsonArray();
            chances.forEach(arr::add);
            this.json.add("chances", arr);
        }
        this.json.addProperty("eu", this.power);
        this.json.addProperty("duration", this.duration);
        this.json.addProperty("amps", this.amps);
        this.json.addProperty("special", this.special);
        this.json.addProperty("map", this.map);
    }
}
