package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class AntimatterModelLoader<T extends IModelGeometry<T>> implements IModelLoader<T> {

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
    public T read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return null;
    }
}
