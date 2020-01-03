package muramasa.antimatter.client;

import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.HashMap;

public class AntimatterModelLoader implements ICustomModelLoader {

    private static HashMap<ResourceLocation, IUnbakedModel> modelLookup = new HashMap<>();

    private IResourceManager resourceManager;

    public static void register(String path, IUnbakedModel model) {
        ResourceLocation loc = new ResourceLocation(Ref.MODID, path);
        if (!modelLookup.containsKey(loc)) modelLookup.put(loc, model);
    }

//    public static void register(String registryPath, BlockBakedOld block) {
//        if (!modelLookup.containsKey(registryPath)) modelLookup.put(registryPath, new ModelTextureData(block));
//    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(ResourceLocation loc) {
        return modelLookup.containsKey(loc);
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation modelLoc) {
        IUnbakedModel model = modelLookup.get(modelLoc);
        return model != null ? model : ModelLoaderRegistry.getMissingModel();
    }
}
