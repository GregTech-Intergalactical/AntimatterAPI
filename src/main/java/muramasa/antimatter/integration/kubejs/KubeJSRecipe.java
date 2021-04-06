package muramasa.antimatter.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.NBTUtilsJS;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.serializer.RecipeSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
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

    //TODO!
    @Override
    public void create(ListJS listJS) {
        //Object itemIn = listJS.get(0);
        //Object itemOut = listJS.get(1);
        //Object fluidIn = listJS.get(2);
        //Object fluidOut = listJS.get(3);
    }

    @Override
    public void deserialize() {
        for (JsonElement e : JSONUtils.getJsonArray(json, "item_in", new JsonArray()))
        {
            this.inputItems.add(RecipeIngredientJS.of(e));
        }
        for (JsonElement e : JSONUtils.getJsonArray(json, "item_out", new JsonArray()))
        {
            this.outputItems.add(ItemStackJS.of(e));
        }
        for (JsonElement e : JSONUtils.getJsonArray(json, "fluid_in", new JsonArray()))
        {
            this.fluidInput.add(RecipeSerializer.getStack(e));
        }
        for (JsonElement e : JSONUtils.getJsonArray(json, "fluid_out", new JsonArray()))
        {
            this.fluidOutput.add(RecipeSerializer.getStack(e));
        }
        this.duration = JSONUtils.getInt(json, "duration");
        this.special = JSONUtils.getInt(json, "special", 0);
        this.power = JSONUtils.getInt(json, "eu");
        this.amps = JSONUtils.getInt(json, "amps",1);
        this.map = JSONUtils.getString(json, "map");

        for (JsonElement e : JSONUtils.getJsonArray(json, "chances", new JsonArray()))
        {
            this.chances.add(e.getAsInt());
        }
    }

    public static JsonElement serializeStack(FluidStack stack) {
        JsonObject obj = new JsonObject();
        obj.addProperty("fluid", stack.getFluid().getRegistryName().toString());
        obj.addProperty("amount", stack.getAmount());
        if (stack.hasTag()) {
            obj.add("tag",NBTDynamicOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.getTag()));
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
