package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AntimatterBlockModelBuilder extends BlockModelBuilder {

    protected List<Consumer<JsonObject>> properties = new ArrayList<>();

    public AntimatterBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public AntimatterBlockModelBuilder property(String property, JsonElement element) {
        properties.add(o -> o.add(property, element));
        return this;
    }

    public AntimatterBlockModelBuilder property(String property, String value) {
        properties.add(o -> o.addProperty(property, value));
        return this;
    }

    public AntimatterBlockModelBuilder property(String property, String key, String value) {
        JsonObject propertyObject = new JsonObject();
        propertyObject.addProperty(key, value);
        return property(property, propertyObject);
    }

    public AntimatterBlockModelBuilder loader(AntimatterModelLoader loader) {
        return property("loader", loader.getLoc().toString());
    }

    public AntimatterBlockModelBuilder model(String model) {
        return property("model", "parent", model);
    }

    public AntimatterBlockModelBuilder model(Texture... textures) {
        return property("model", getModelObject(textures));
    }

    public AntimatterBlockModelBuilder config(int id, Texture... textures) {
        properties.add(o -> {
           if (!o.has("config")) o.add("config", new JsonArray());
            JsonObject configObject = new JsonObject();
            configObject.addProperty("id", id);
            configObject.add("model", getModelObject(textures));
           o.getAsJsonArray("config").add(configObject);
        });
        return this;
    }

    public JsonObject getModelObject(Texture... textures) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", Ref.ID + ":block/preset/simple");
        JsonObject texture = new JsonObject();
        if (textures.length == 1) {
            texture.addProperty("all", textures[0].toString());
        } else if (textures.length == Ref.DIRECTIONS.length) {
            for (int i = 0; i < Ref.DIRECTIONS.length; i++) {
                texture.addProperty(Ref.DIRECTIONS[i].toString(), textures[i].toString());
            }
        }
        model.add("textures", texture);
        return model;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        if (!properties.isEmpty()) properties.forEach(c -> c.accept(root));
        return root;
    }
}
