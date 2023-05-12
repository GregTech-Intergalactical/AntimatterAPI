package muramasa.antimatter.integration.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.item.ingredient.TagIngredientJS;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static muramasa.antimatter.recipe.ingredient.RecipeIngredient.valuesFromJson;

public class RecipeIngredientJS extends IngredientStackJS {
    protected boolean nonConsume = false;
    protected boolean ignoreNbt = false;

    public RecipeIngredientJS(IngredientJS i, int c) {
        super(i, c);
    }

    public RecipeIngredientJS setNoConsume(boolean noConsume) {
        this.nonConsume = noConsume;
        return this;
    }

    public boolean ignoreConsume() {
        return nonConsume;
    }

    public RecipeIngredientJS setIgnoreNbt(boolean ignoreNbt) {
        this.ignoreNbt = ignoreNbt;
        return this;
    }

    public boolean ignoreNbt() {
        return ignoreNbt;
    }

    private static IngredientJS fromValue(Ingredient.Value value){
        if (value instanceof RecipeIngredient.RecipeValue v){
            if (v.getTag() != null){
                IngredientJS in = TagIngredientJS.createTag(v.getTag().location().toString());
                if (v.getCount() > 1){
                    in = new IngredientStackJS(in, v.getCount());
                }
                return in;
            } else {
                return IngredientJS.of(v.getItems());
            }
        } else if (value instanceof RecipeIngredient.MultiValue v){
            List<IngredientJS> js = new ArrayList<>();
            for (Ingredient.Value v2 : v.getValues()) {
                js.add(fromValue(v2));
            }
            return IngredientJS.of(js);
        }
        return null;
    }

    public static RecipeIngredientJS fromJson(@Nullable JsonElement json) {
        List<Ingredient.Value> values = valuesFromJson(json).toList();
        List<IngredientJS> js = new ArrayList<>();
        for (Ingredient.Value v2 : values) {
            js.add(fromValue(v2));
        }
        int count = 1;
        boolean ignoreNBt = false, noConsume = false;
        if (json instanceof JsonObject object){
            if (object.has("count")){
                count = object.get("count").getAsInt();
            }
            if (object.has("nbt") && object.get("nbt").getAsBoolean()){
                ignoreNBt = true;
            }
            if (object.has("noconsume") && object.get("noconsume").getAsBoolean()){
                noConsume = true;
            }
        }
        return new RecipeIngredientJS(IngredientJS.of(js.size() == 1 ? js.get(0) : js), count).setIgnoreNbt(ignoreNBt).setNoConsume(noConsume);
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        JsonElement element = super.toJson();
        if (element instanceof JsonObject o){
            object = o;
        } else if (element instanceof JsonArray){
            object.add("values", element);
        }
        object.addProperty("nbt", ignoreNbt);
        object.addProperty("noconsume", nonConsume);
        return object;
    }
}
