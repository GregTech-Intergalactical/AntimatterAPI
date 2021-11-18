package muramasa.antimatter.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.recipe.serializer.RecipeSerializer;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class KubeJSRecipe extends RecipeJS {

    public final List<FluidStack> fluidInput = new ObjectArrayList<>();
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
            if (inputItem instanceof ItemStackJS) {
                this.inputItems.add(RecipeIngredientJS.of(((ItemStackJS) inputItem).toResultJson()));
            } else if (inputItem instanceof MapJS) {
                MapJS map = (MapJS) inputItem;
                this.inputItems.add(RecipeIngredientJS.of(map.toJson()));
            } else if (inputItem instanceof JsonElement) {
                this.inputItems.add(RecipeIngredientJS.of((JsonElement) inputItem));
            } else if (inputItem instanceof IngredientJS) {
                this.inputItems.add(RecipeIngredientJS.of((IngredientJS) inputItem));
            }
        }
        if (listJS.get(2) != null) for (Object outputItem : ListJS.orSelf(listJS.get(2))) {
            this.outputItems.add(ItemStackJS.of(outputItem));
        }
        if (listJS.get(3) != null) for (Object inputFluid : ListJS.orSelf(listJS.get(3))) {
            MapJS map = (MapJS) inputFluid;
            this.fluidInput.add(RecipeSerializer.getStack(map.toJson()));
        }
        if (listJS.get(4) != null) for (Object outputFluid : ListJS.orSelf(listJS.get(4))) {
            MapJS map = (MapJS) outputFluid;
            this.fluidOutput.add(RecipeSerializer.getStack(map.toJson()));
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
        for (JsonElement e : JSONUtils.getAsJsonArray(json, "item_in", new JsonArray())) {
            this.inputItems.add(RecipeIngredientJS.of(e));
        }
        for (JsonElement e : JSONUtils.getAsJsonArray(json, "item_out", new JsonArray())) {
            this.outputItems.add(ItemStackJS.of(e));
        }
        for (JsonElement e : JSONUtils.getAsJsonArray(json, "fluid_in", new JsonArray())) {
            this.fluidInput.add(RecipeSerializer.getStack(e));
        }
        for (JsonElement e : JSONUtils.getAsJsonArray(json, "fluid_out", new JsonArray())) {
            this.fluidOutput.add(RecipeSerializer.getStack(e));
        }
        this.duration = JSONUtils.getAsInt(json, "duration");
        this.special = JSONUtils.getAsInt(json, "special", 0);
        this.power = JSONUtils.getAsInt(json, "eu");
        this.amps = JSONUtils.getAsInt(json, "amps", 1);
        this.map = JSONUtils.getAsString(json, "map");

        for (JsonElement e : JSONUtils.getAsJsonArray(json, "chances", new JsonArray())) {
            this.chances.add(e.getAsInt());
        }
    }

    public static JsonElement serializeStack(FluidStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("fluid", stack.getFluid().getRegistryName().toString());
        obj.addProperty("amount", stack.getAmount());
        if (stack.hasTag()) {
            obj.add("tag", NBTDynamicOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.getTag()));
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
            fluidInput.forEach(t -> arr.add(serializeStack(t)));
            this.json.add("fluid_in", arr);
        }
        if (fluidOutput.size() > 0) {
            JsonArray arr = new JsonArray();
            fluidInput.forEach(t -> arr.add(serializeStack(t)));
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
