package muramasa.gregtech.client.render;

import muramasa.gregtech.Ref;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.HashMap;

public class GTModelLoader implements ICustomModelLoader {

    private static HashMap<String, IModel> modelLookup = new HashMap<>();

    private IResourceManager resourceManager;

    public static void register(String registryPath, IModel model) {
        if (!modelLookup.containsKey(registryPath)) modelLookup.put(registryPath, model);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(ResourceLocation modelLoc) {
        return modelLoc.getResourceDomain().equals(Ref.MODID) && modelLookup.containsKey(modelLoc.getResourcePath());
    }

    @Override
    public IModel loadModel(ResourceLocation modelLoc) throws Exception {
        IModel model = modelLookup.get(modelLoc.getResourcePath());
        return model != null ? model : ModelLoaderRegistry.getMissingModel();
    }
}
