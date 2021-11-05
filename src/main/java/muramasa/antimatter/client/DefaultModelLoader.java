package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.client.model.AntimatterModel;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class DefaultModelLoader extends AntimatterModelLoader<AntimatterModel> {
    public DefaultModelLoader(ResourceLocation loc) {
        super(loc);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public AntimatterModel read(JsonDeserializationContext context, JsonObject json) {
        try {
            IUnbakedModel baseModel = (json.has("model") && json.get("model").isJsonObject()) ? context.deserialize(json.get("model"), BlockModel.class) : ModelUtils.getMissingModel();
            return new AntimatterModel(baseModel, buildRotations(json));
        } catch (Exception e) {
            return onModelLoadingException(e);
        }
    }

    public AntimatterModel onModelLoadingException(Exception e) {
        Antimatter.LOGGER.error("ModelLoader Exception for " + getLoc().toString());
        e.printStackTrace();
        return new AntimatterModel(ModelUtils.getMissingModel());
    }
}
