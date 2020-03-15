package muramasa.antimatter.datagen.builder;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public AntimatterBlockModelBuilder model(String parent, ImmutableMap<String, Texture> textures) {
        loader();
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        textures.forEach((k, v) -> builder.put(k, v.toString()));
        return property("model", getModelObject(parent, builder.build()));
    }

    public AntimatterBlockModelBuilder config(int id, String model, Function<DynamicConfigBuilder, DynamicConfigBuilder> configFunction) {
        DynamicConfigBuilder builder = configFunction.apply(new DynamicConfigBuilder(this).model(id, model));
        loader();
        properties.add(o -> {
            if (!o.has("config")) o.add("config", new JsonArray());
            JsonObject configObject = new JsonObject();
            configObject.addProperty("id", builder.id);
            if (builder.rotations != null && builder.rotations.length > 0) {
                configObject.add("rotation", getRotationObject(builder.rotations));
            }
            configObject.add("model", getModelObject(builder.parent, builder.textures));
            o.getAsJsonArray("config").add(configObject);
        });
        return this;
    }

    public JsonArray getRotationObject(Direction[] rotations) {
        JsonArray rotationArray = new JsonArray();
        Arrays.stream(rotations).forEach(r -> rotationArray.add(r.toString()));
        return rotationArray;
    }

    public JsonObject getModelObject(String parent, Texture... textures) {
        return getModelObject(parent, Arrays.stream(textures).map(ResourceLocation::toString).toArray(String[]::new));
    }

    public JsonObject getModelObject(String parent, String... textures) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        if (textures.length == 1) {
            builder.put("all", textures[0]);
        } else if (textures.length == Ref.DIRECTIONS.length) {
            for (int i = 0; i < Ref.DIRECTIONS.length; i++) {
                builder.put(Ref.DIRECTIONS[i].toString(), textures[i]);
            }
        }
        return getModelObject(parent, builder.build());
    }


    public JsonObject getModelObject(String parent, ImmutableMap<String, String> textures) {
        JsonObject model = new JsonObject();
        if (!parent.contains(":")) parent = parent.replace("simple", SIMPLE).replace("layered", LAYERED);
        model.addProperty("parent", parent);
        JsonObject texture = new JsonObject();
        textures.forEach((k, v) -> texture.addProperty(k, v.replaceAll("mc:", "minecraft:")));
        model.add("textures", texture);
        model.add("uvlock", new JsonPrimitive(true));
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
         config(1, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
         config(2, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
         config(4, SIMPLE, c -> c.tex(tex[1], tex[1], tex[0], tex[12], tex[0], tex[0]));
         config(8, SIMPLE, c -> c.tex(tex[1], tex[1], tex[12], tex[0], tex[0], tex[0]));
         config(16, SIMPLE, c -> c.tex(tex[0], tex[0], tex[0], tex[0], tex[0], tex[12]));
         config(32, SIMPLE, c -> c.tex(tex[0], tex[0], tex[0], tex[0], tex[12], tex[0]));

        //Lines (2)
         config(3, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
         config(12, SIMPLE, c -> c.tex(tex[1], tex[1], tex[12], tex[12], tex[0], tex[0]));
         config(48, SIMPLE, c -> c.tex(tex[0], tex[0], tex[0], tex[0], tex[12], tex[12]));

        //Elbows (2)
         config(6, SIMPLE, c -> c.tex(tex[1], tex[12], tex[0], tex[1], tex[10], tex[11]));
         config(5, SIMPLE, c -> c.tex(tex[12], tex[1], tex[12], tex[1], tex[9], tex[8]));
         config(9, SIMPLE, c -> c.tex(tex[12], tex[1], tex[1], tex[12], tex[8], tex[9]));
         config(10, SIMPLE, c -> c.tex(tex[1], tex[12], tex[1], tex[12], tex[11], tex[10]));
         config(17, SIMPLE, c -> c.tex(tex[12], tex[0], tex[8], tex[9], tex[12], tex[1]));
         config(18, SIMPLE, c -> c.tex(tex[0], tex[12], tex[11], tex[10], tex[12], tex[1]));
         config(33, SIMPLE, c -> c.tex(tex[12], tex[0], tex[9], tex[8], tex[1], tex[12]));
         config(34, SIMPLE, c -> c.tex(tex[0], tex[12], tex[10], tex[11], tex[1], tex[10]));
         config(20, SIMPLE, c -> c.tex(tex[10], tex[10], tex[0], tex[0], tex[0], tex[0]));
         config(24, SIMPLE, c -> c.tex(tex[9], tex[9], tex[0], tex[0], tex[0], tex[0]));
         config(36, SIMPLE, c -> c.tex(tex[11], tex[11], tex[0], tex[0], tex[0], tex[0]));
         config(40, SIMPLE, c -> c.tex(tex[8], tex[8], tex[0], tex[0], tex[0], tex[0]));

        //Side (3)
         config(7, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[1], tex[4], tex[2]));
         config(11, SIMPLE, c -> c.tex(tex[12], tex[12], tex[1], tex[12], tex[2], tex[4]));
         config(13, SIMPLE, c -> c.tex(tex[12], tex[1], tex[12], tex[12], tex[3], tex[3]));
         config(14, SIMPLE, c -> c.tex(tex[1], tex[12], tex[12], tex[12], tex[5], tex[5]));
         config(19, SIMPLE, c -> c.tex(tex[12], tex[12], tex[2], tex[4], tex[12], tex[1]));
         config(28, SIMPLE, c -> c.tex(tex[4], tex[4], tex[12], tex[12], tex[12], tex[0]));
         config(35, SIMPLE, c -> c.tex(tex[12], tex[12], tex[4], tex[2], tex[1], tex[12]));
         config(44, SIMPLE, c -> c.tex(tex[2], tex[2], tex[12], tex[12], tex[0], tex[12]));
         config(49, SIMPLE, c -> c.tex(tex[12], tex[0], tex[3], tex[3], tex[12], tex[12]));
         config(50, SIMPLE, c -> c.tex(tex[0], tex[12], tex[5], tex[5], tex[12], tex[12]));
         config(52, SIMPLE, c -> c.tex(tex[3], tex[5], tex[12], tex[0], tex[12], tex[12]));
         config(56, SIMPLE, c -> c.tex(tex[5], tex[3], tex[0], tex[12], tex[12], tex[12]));

        //Corner (3)
         config(21, SIMPLE, c -> c.tex(tex[10], tex[10], tex[0], tex[9], tex[0], tex[8]));
         config(22, SIMPLE, c -> c.tex(tex[10], tex[10], tex[0], tex[10], tex[0], tex[11]));
         config(25, SIMPLE, c -> c.tex(tex[9], tex[9], tex[8], tex[0], tex[0], tex[9]));
         config(26, SIMPLE, c -> c.tex(tex[9], tex[9], tex[11], tex[0], tex[0], tex[10]));
         config(37, SIMPLE, c -> c.tex(tex[11], tex[11], tex[0], tex[8], tex[9], tex[0]));
         config(38, SIMPLE, c -> c.tex(tex[11], tex[11], tex[0], tex[11], tex[10], tex[0]));
         config(41, SIMPLE, c -> c.tex(tex[8], tex[8], tex[9], tex[0], tex[8], tex[0]));
         config(42, SIMPLE, c -> c.tex(tex[8], tex[8], tex[10], tex[0], tex[11], tex[0]));

        //Arrow (4)
         config(23, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[4], tex[12], tex[2]));
         config(27, SIMPLE, c -> c.tex(tex[12], tex[12], tex[2], tex[12], tex[12], tex[4]));
         config(29, SIMPLE, c -> c.tex(tex[12], tex[4], tex[12], tex[12], tex[12], tex[3]));
         config(30, SIMPLE, c -> c.tex(tex[4], tex[12], tex[12], tex[12], tex[12], tex[5]));
         config(39, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[2], tex[4], tex[12]));
         config(43, SIMPLE, c -> c.tex(tex[12], tex[12], tex[4], tex[12], tex[2], tex[12]));
         config(45, SIMPLE, c -> c.tex(tex[12], tex[2], tex[12], tex[12], tex[3], tex[12]));
         config(46, SIMPLE, c -> c.tex(tex[2], tex[12], tex[12], tex[12], tex[5], tex[12]));
         config(53, SIMPLE, c -> c.tex(tex[12], tex[5], tex[12], tex[3], tex[12], tex[12]));
         config(54, SIMPLE, c -> c.tex(tex[3], tex[12], tex[12], tex[5], tex[12], tex[12]));
         config(57, SIMPLE, c -> c.tex(tex[12], tex[3], tex[3], tex[12], tex[12], tex[12]));
         config(58, SIMPLE, c -> c.tex(tex[5], tex[12], tex[5], tex[12], tex[12], tex[12]));

        //Cross (4)
         config(15, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[6], tex[6]));
         config(51, SIMPLE, c -> c.tex(tex[12], tex[12], tex[6], tex[6], tex[12], tex[12]));
         config(60, SIMPLE, c -> c.tex(tex[6], tex[6], tex[12], tex[12], tex[12], tex[12]));

        //Five (5)
         config(31, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[12], tex[6]));
         config(47, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[6], tex[12]));
         config(55, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[6], tex[12], tex[12]));
         config(59, SIMPLE, c -> c.tex(tex[12], tex[12], tex[6], tex[12], tex[12], tex[12]));
         config(61, SIMPLE, c -> c.tex(tex[12], tex[6], tex[12], tex[12], tex[12], tex[12]));
         config(62, SIMPLE, c -> c.tex(tex[6], tex[12], tex[12], tex[12], tex[12], tex[12]));

        //All (6)
         config(63, SIMPLE, c -> c.tex(tex[12], tex[12], tex[12], tex[12], tex[12], tex[12]));

        return this;
    }
}
