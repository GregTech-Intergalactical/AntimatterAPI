package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;

public class AntimatterBlockModelBuilder extends BlockModelBuilder {

    protected Map<String, JsonObject> customProperties = new HashMap<>();

    public AntimatterBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public AntimatterBlockModelBuilder property(String property, String key, String value) {
        JsonObject propertyObject = new JsonObject();
        propertyObject.addProperty(key, value);
        customProperties.put(property, propertyObject);
        return this;
    }

    public AntimatterBlockModelBuilder loader(String domain, String path) {
        property("loader", domain, path);
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        if (!customProperties.isEmpty()) {
            customProperties.forEach(root::add);
        }
        return root;
    }
}
