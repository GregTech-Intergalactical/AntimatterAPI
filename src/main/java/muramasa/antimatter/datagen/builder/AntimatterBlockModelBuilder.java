package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AntimatterBlockModelBuilder extends BlockModelBuilder {

    protected static String SIMPLE = Ref.ID + ":block/preset/simple";
    protected static String LAYERED = Ref.ID + ":block/preset/layered";

    protected ResourceLocation loader = null;
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

    public AntimatterBlockModelBuilder loader() {
        return loader(AntimatterModelManager.LOADER);
    }

    public AntimatterBlockModelBuilder loader(AntimatterModelLoader loader) {
        this.loader = loader.getLoc();
        return this;
    }

    public AntimatterBlockModelBuilder model(String parent, String... models) {
        loader();
        return property("model", getModelObject(parent, models));
    }

    public AntimatterBlockModelBuilder model(String parent, Texture... textures) {
        loader();
        return property("model", getModelObject(parent, textures));
    }

    public AntimatterBlockModelBuilder config(int id, String parent, String... textures) {
        loader();
        properties.add(o -> {
           if (!o.has("config")) o.add("config", new JsonArray());
            JsonObject configObject = new JsonObject();
            configObject.addProperty("id", id);
            configObject.add("model", getModelObject(parent, textures));
           o.getAsJsonArray("config").add(configObject);
        });
        return this;
    }

    public AntimatterBlockModelBuilder config(int id, String parent, Texture... textures) {
        String[] strings = new String[textures.length];
        Arrays.setAll(strings, i -> textures[i].toString());
        return config(id, parent, strings);
    }

    public JsonObject getModelObject(String parent, Texture... textures) {
        return getModelObject(parent, Arrays.stream(textures).map(ResourceLocation::toString).toArray(String[]::new));
    }

    public JsonObject getModelObject(String parent, String... textures) {
        JsonObject model = new JsonObject();
        if (!parent.contains(":")) parent = parent.replace("simple", SIMPLE).replace("layered", LAYERED);
        model.addProperty("parent", parent);
        JsonObject texture = new JsonObject();
        if (textures.length == 1) {
            texture.addProperty("all", textures[0]);
        } else if (textures.length == Ref.DIRECTIONS.length) {
            for (int i = 0; i < Ref.DIRECTIONS.length; i++) {
                texture.addProperty(Ref.DIRECTIONS[i].toString(), textures[i]);
            }
        }
        model.add("textures", texture);
        return model;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        if (loader != null) root.addProperty("loader", loader.toString());
        if (!properties.isEmpty()) properties.forEach(c -> c.accept(root));
        return root;
    }

    public AntimatterBlockModelBuilder basicConfig(ITextureProvider textureProvider, Texture[] tex) {
        if (tex.length < 13) return this;
         model(SIMPLE, textureProvider.getTextures());

        //Single (1)
         config(1, SIMPLE, tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]);
         config(2, SIMPLE, tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]);
         config(4, SIMPLE, tex[1], tex[1], tex[0], tex[12], tex[0], tex[0]);
         config(8, SIMPLE, tex[1], tex[1], tex[12], tex[0], tex[0], tex[0]);
         config(16, SIMPLE, tex[0], tex[0], tex[0], tex[0], tex[0], tex[12]);
         config(32, SIMPLE, tex[0], tex[0], tex[0], tex[0], tex[12], tex[0]);

        //Lines (2)
         config(3, SIMPLE, tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]);
         config(12, SIMPLE, tex[1], tex[1], tex[12], tex[12], tex[0], tex[0]);
         config(48, SIMPLE, tex[0], tex[0], tex[0], tex[0], tex[12], tex[12]);

        //Elbows (2)
         config(6, SIMPLE, tex[1], tex[12], tex[0], tex[1], tex[10], tex[11]);
         config(5, SIMPLE, tex[12], tex[1], tex[12], tex[1], tex[9], tex[8]);
         config(9, SIMPLE, tex[12], tex[1], tex[1], tex[12], tex[8], tex[9]);
         config(10, SIMPLE, tex[1], tex[12], tex[1], tex[12], tex[11], tex[10]);
         config(17, SIMPLE, tex[12], tex[0], tex[8], tex[9], tex[12], tex[1]);
         config(18, SIMPLE, tex[0], tex[12], tex[11], tex[10], tex[12], tex[1]);
         config(33, SIMPLE, tex[12], tex[0], tex[9], tex[8], tex[1], tex[12]);
         config(34, SIMPLE, tex[0], tex[12], tex[10], tex[11], tex[1], tex[10]);
         config(20, SIMPLE, tex[10], tex[10], tex[0], tex[0], tex[0], tex[0]);
         config(24, SIMPLE, tex[9], tex[9], tex[0], tex[0], tex[0], tex[0]);
         config(36, SIMPLE, tex[11], tex[11], tex[0], tex[0], tex[0], tex[0]);
         config(40, SIMPLE, tex[8], tex[8], tex[0], tex[0], tex[0], tex[0]);

        //Side (3)
         config(7, SIMPLE, tex[12], tex[12], tex[12], tex[1], tex[4], tex[2]);
         config(11, SIMPLE, tex[12], tex[12], tex[1], tex[12], tex[2], tex[4]);
         config(13, SIMPLE, tex[12], tex[1], tex[12], tex[12], tex[3], tex[3]);
         config(14, SIMPLE, tex[1], tex[12], tex[12], tex[12], tex[5], tex[5]);
         config(19, SIMPLE, tex[12], tex[12], tex[2], tex[4], tex[12], tex[1]);
         config(28, SIMPLE, tex[4], tex[4], tex[12], tex[12], tex[12], tex[0]);
         config(35, SIMPLE, tex[12], tex[12], tex[4], tex[2], tex[1], tex[12]);
         config(44, SIMPLE, tex[2], tex[2], tex[12], tex[12], tex[0], tex[12]);
         config(49, SIMPLE, tex[12], tex[0], tex[3], tex[3], tex[12], tex[12]);
         config(50, SIMPLE, tex[0], tex[12], tex[5], tex[5], tex[12], tex[12]);
         config(52, SIMPLE, tex[3], tex[5], tex[12], tex[0], tex[12], tex[12]);
         config(56, SIMPLE, tex[5], tex[3], tex[0], tex[12], tex[12], tex[12]);

        //Corner (3)
         config(21, SIMPLE, tex[10], tex[10], tex[0], tex[9], tex[0], tex[8]);
         config(22, SIMPLE, tex[10], tex[10], tex[0], tex[10], tex[0], tex[11]);
         config(25, SIMPLE, tex[9], tex[9], tex[8], tex[0], tex[0], tex[9]);
         config(26, SIMPLE, tex[9], tex[9], tex[11], tex[0], tex[0], tex[10]);
         config(37, SIMPLE, tex[11], tex[11], tex[0], tex[8], tex[9], tex[0]);
         config(38, SIMPLE, tex[11], tex[11], tex[0], tex[11], tex[10], tex[0]);
         config(41, SIMPLE, tex[8], tex[8], tex[9], tex[0], tex[8], tex[0]);
         config(42, SIMPLE, tex[8], tex[8], tex[10], tex[0], tex[11], tex[0]);

        //Arrow (4)
         config(23, SIMPLE, tex[12], tex[12], tex[12], tex[4], tex[12], tex[2]);
         config(27, SIMPLE, tex[12], tex[12], tex[2], tex[12], tex[12], tex[4]);
         config(29, SIMPLE, tex[12], tex[4], tex[12], tex[12], tex[12], tex[3]);
         config(30, SIMPLE, tex[4], tex[12], tex[12], tex[12], tex[12], tex[5]);
         config(39, SIMPLE, tex[12], tex[12], tex[12], tex[2], tex[4], tex[12]);
         config(43, SIMPLE, tex[12], tex[12], tex[4], tex[12], tex[2], tex[12]);
         config(45, SIMPLE, tex[12], tex[2], tex[12], tex[12], tex[3], tex[12]);
         config(46, SIMPLE, tex[2], tex[12], tex[12], tex[12], tex[5], tex[12]);
         config(53, SIMPLE, tex[12], tex[5], tex[12], tex[3], tex[12], tex[12]);
         config(54, SIMPLE, tex[3], tex[12], tex[12], tex[5], tex[12], tex[12]);
         config(57, SIMPLE, tex[12], tex[3], tex[3], tex[12], tex[12], tex[12]);
         config(58, SIMPLE, tex[5], tex[12], tex[5], tex[12], tex[12], tex[12]);

        //Cross (4)
         config(15, SIMPLE, tex[12], tex[12], tex[12], tex[12], tex[6], tex[6]);
         config(51, SIMPLE, tex[12], tex[12], tex[6], tex[6], tex[12], tex[12]);
         config(60, SIMPLE, tex[6], tex[6], tex[12], tex[12], tex[12], tex[12]);

        //Five (5)
         config(31, SIMPLE, tex[12], tex[12], tex[12], tex[12], tex[12], tex[6]);
         config(47, SIMPLE, tex[12], tex[12], tex[12], tex[12], tex[6], tex[12]);
         config(55, SIMPLE, tex[12], tex[12], tex[12], tex[6], tex[12], tex[12]);
         config(59, SIMPLE, tex[12], tex[12], tex[6], tex[12], tex[12], tex[12]);
         config(61, SIMPLE, tex[12], tex[6], tex[12], tex[12], tex[12], tex[12]);
         config(62, SIMPLE, tex[6], tex[12], tex[12], tex[12], tex[12], tex[12]);

        //All (6)
         config(63, SIMPLE, tex[12], tex[12], tex[12], tex[12], tex[12], tex[12]);

        return this;
    }
}
