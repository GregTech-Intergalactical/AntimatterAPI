package muramasa.antimatter.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import muramasa.antimatter.client.model.ProxyModel;
import net.minecraft.resources.ResourceLocation;

public class ProxyModelLoader extends AntimatterModelLoader<ProxyModel> {

        public ProxyModelLoader(ResourceLocation location) {
            super(location);
        }

        @Override
        public ProxyModel readModel(JsonDeserializationContext context, JsonObject json) {
            return new ProxyModel();
        }
    }