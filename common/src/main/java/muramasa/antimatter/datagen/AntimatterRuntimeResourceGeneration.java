package muramasa.antimatter.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.json.JAntimatterModel;
import muramasa.antimatter.datagen.json.JLoaderModel;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.util.UnsafeByteArrayOutputStream;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class AntimatterRuntimeResourceGeneration {
    public static final RuntimeResourcePack DYNAMIC_RESOURCE_PACK = RuntimeResourcePack.create(new ResourceLocation(Ref.ID, "dynamic"));
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Advancement.Builder.class, (JsonSerializer<Advancement.Builder>) (src, typeOfSrc, context) -> src.serializeToJson())
            .registerTypeAdapter(JAntimatterModel.class, new JAntimatterModel.JAntimatterModelSerializer())
            .registerTypeAdapter(JTextures.class, new JTextures.Serializer())
            .create();

    public static ResourceLocation getLootLoc(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "loot_tables/blocks/" + id.getPath());
    }

    public static ResourceLocation getStateLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "blockstates/", registryId.getPath()));
    }

    public static ResourceLocation getModelLoc(ResourceLocation registryId) {
        return new ResourceLocation(registryId.getNamespace(), String.join("", "models/", registryId.getPath()));
    }

    public static ResourceLocation getRecipeLog(ResourceLocation recipeId) {
        return new ResourceLocation(recipeId.getNamespace(), String.join("", "recipes/", recipeId.getPath()));
    }

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", identifier, "/", tagId.getPath()));
    }

    public static byte[] serialize(Object object) {
        UnsafeByteArrayOutputStream ubaos = new UnsafeByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(ubaos);
        GSON.toJson(object, writer);
        try {
            writer.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return ubaos.getBytes();
    }

    public static ResourceLocation fix(ResourceLocation identifier, String prefix, String append) {
        return new ResourceLocation(identifier.getNamespace(), prefix + '/' + identifier.getPath() + '.' + append);
    }
}
