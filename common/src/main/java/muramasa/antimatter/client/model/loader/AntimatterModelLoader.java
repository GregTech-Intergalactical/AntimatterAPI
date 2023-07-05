package muramasa.antimatter.client.model.loader;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.IAntimatterModel;
import net.minecraft.resources.ResourceLocation;

public abstract class AntimatterModelLoader<T extends IAntimatterModel> implements IAntimatterModelLoader<T> {
    private final ResourceLocation loc;

    public AntimatterModelLoader(ResourceLocation loc) {
        this.loc = loc;
        AntimatterAPI.register(IAntimatterModelLoader.class, this);
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    @Override
    public String getId() {
        return getLoc().getPath();
    }
}
