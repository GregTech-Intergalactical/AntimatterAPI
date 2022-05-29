package muramasa.antimatter.recipe.condition;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface IConditionSerializer<T extends ICondition> {
    void write(JsonObject jsonObject, T iCondition);

    T read(JsonObject jsonObject);

    ResourceLocation getID();

    default JsonObject getJson(T value) {
        JsonObject json = new JsonObject();
        this.write(json, value);
        json.addProperty("type", value.getID().toString());
        return json;
    }
}
