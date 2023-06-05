package muramasa.antimatter.client.model.loader.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.model.AntimatterModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class DefaultModelLoader extends AntimatterModelLoader<AntimatterModel> {
    public DefaultModelLoader(ResourceLocation loc) {
        super(loc);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public AntimatterModel readModel(JsonDeserializationContext context, JsonObject json) {
        try {
            UnbakedModel baseModel = (json.has("model") && json.get("model").isJsonObject()) ? context.deserialize(json.get("model"), BlockModel.class) : ModelUtils.getMissingModel();
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
