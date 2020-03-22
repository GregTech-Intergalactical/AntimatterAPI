package muramasa.antimatter.datagen.builder;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

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

    public AntimatterBlockModelBuilder loader(AntimatterModelLoader loader) {
        this.loader = loader.getLoc();
        return this;
    }

    public AntimatterBlockModelBuilder model(String parent, String... textures) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(new JsonObject(), parent, buildTextures(textures)));
    }

    public AntimatterBlockModelBuilder model(String parent, Texture... textures) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(new JsonObject(), parent, buildTextures(textures)));
    }

    public AntimatterBlockModelBuilder model(String parent, Function<ImmutableMap.Builder<String, Texture>, ImmutableMap.Builder<String, Texture>> func) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(new JsonObject(), parent, buildTextures(func.apply(new ImmutableMap.Builder<>()).build())));
    }

    public AntimatterBlockModelBuilder model(String parent, ImmutableMap<String, Texture> map) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("model", addModelObject(new JsonObject(), parent, buildTextures(map)));
    }

    public AntimatterBlockModelBuilder rot(int... rotations) {
        loader(AntimatterModelManager.LOADER_MAIN);
        return property("rotation", getRotationObject(rotations));
    }

    public AntimatterBlockModelBuilder config(int id, String parent, Function<DynamicConfigBuilder, DynamicConfigBuilder> builderFunc) {
        return config(id, (b, l) -> l.add(builderFunc.apply(b.of(parent))));
    }

    public AntimatterBlockModelBuilder config(int id, IConfigFunction configFunc) {
        loader(AntimatterModelManager.LOADER_DYNAMIC);
        ImmutableList<DynamicConfigBuilder> builders = configFunc.apply(new DynamicConfigBuilder(), new ImmutableList.Builder<>()).build();
        properties.add(o -> {
            if (!o.has("config")) o.add("config", new JsonArray());
            JsonObject modelObject = new JsonObject();
            modelObject.add("id", new JsonPrimitive(id));
            modelObject.add("models", getModelObjects(builders));
            o.get("config").getAsJsonArray().add(modelObject);
        });
        return this;
    }

    public interface IConfigFunction {

        ImmutableList.Builder<DynamicConfigBuilder> apply(DynamicConfigBuilder b, ImmutableList.Builder<DynamicConfigBuilder> l);
    }

    public JsonArray getRotationObject(int[] rotations) {
        JsonArray rotationArray = new JsonArray();
        Arrays.stream(rotations).forEach(rotationArray::add);
        return rotationArray;
    }

    public JsonArray getModelObjects(ImmutableList<DynamicConfigBuilder> builders) {
        JsonArray models = new JsonArray();
        builders.forEach(b -> {
            JsonObject m = addModelObject(new JsonObject(), b.parent, b.textures);
            if (b.hasRots()) m.add("rotation", getRotationObject(b.rotations));
            models.add(m);
        });
        return models;
    }

    public JsonObject addModelObject(JsonObject o, String parent, ImmutableMap<String, String> textures) {
        if (!parent.contains(":")) parent = StringUtils.replace(StringUtils.replace(parent, "simple", SIMPLE), "layered", LAYERED);
        o.addProperty("parent", parent);
        JsonObject texture = new JsonObject();
        textures.forEach((k, v) -> texture.addProperty(k, v.replaceAll("mc:", "minecraft:")));
        o.add("textures", texture);
        return o;
    }

    public AntimatterBlockModelBuilder staticConfigId(String mapId) {
        loader(AntimatterModelManager.LOADER_DYNAMIC);
        return property("staticConfigId", mapId);
    }

    public static ImmutableMap<String, String> buildTextures(ImmutableMap<String, Texture> map) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        map.forEach((k, v) -> builder.put(k, v.toString()));
        return builder.build();
    }

    public static ImmutableMap<String, String> buildTextures(Texture... textures) {
        return buildTextures(Arrays.stream(textures).map(ResourceLocation::toString).toArray(String[]::new));
    }

    public static ImmutableMap<String, String> buildTextures(String... textures) {
        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        if (textures.length == 1) {
            builder.put("all", textures[0]);
        } else if (textures.length == 6) {
            for (int i = 0; i < 6; i++) {
                builder.put(Ref.DIRECTIONS[i].toString(), textures[i]);
            }
        }
        return builder.build();
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
         config(1, SIMPLE, (c) -> c.tex(tex[12], tex[12], tex[1], tex[1], tex[1], tex[1]));
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
