package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.client.model.DynamicModel;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.IModelLoader;

public class AntimatterModelLoader implements IModelLoader<AntimatterModel> {

    protected ResourceLocation loc;

    public AntimatterModelLoader(ResourceLocation loc) {
        this.loc = loc;
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public AntimatterModel read(JsonDeserializationContext context, JsonObject json) {
        try {
            IUnbakedModel baseModel = (json.has("model") && json.get("model").isJsonObject()) ? context.deserialize(json.get("model"), BlockModel.class) : ModelUtils.getMissingModel();
            if (json.has("config") && json.get("config").isJsonArray()) { //DynamicModel
                Int2ObjectOpenHashMap<Tuple<String, IUnbakedModel>> configModels = new Int2ObjectOpenHashMap<>();
                for (JsonElement e : json.getAsJsonArray("config")) {
                    if (!e.isJsonObject() || !e.getAsJsonObject().has("id") || !e.getAsJsonObject().has("model")) continue;
                    IUnbakedModel model = context.deserialize(e.getAsJsonObject().get("model"), BlockModel.class);
                    configModels.put(e.getAsJsonObject().get("id").getAsInt(), new Tuple<>(e.toString(), model));
                }
                return new DynamicModel(baseModel, configModels);
            } else { //NormalModel
                return new AntimatterModel(baseModel);
            }
        } catch (Exception e) {
            Antimatter.LOGGER.error("ModelLoader Exception for " + getLoc().toString());
            e.printStackTrace();
            return new DynamicModel(ModelUtils.getMissingModel(), new Int2ObjectOpenHashMap<>());
        }
    }
}
