package muramasa.antimatter.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.HashMap;

public class AntimatterModelLoader implements ICustomModelLoader {

    private static HashMap<ResourceLocation, IUnbakedModel> LOOKUP = new HashMap<>();

    private IResourceManager resourceManager;

    public static void put(Block block, IUnbakedModel model) {
        LOOKUP.put(new ResourceLocation(block.getRegistryName().getNamespace(), "models/block/" + block.getRegistryName().getPath()), model);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(ResourceLocation path) {
        return LOOKUP.containsKey(path);
    }

    @Override
    public IUnbakedModel loadModel(ResourceLocation path) {
        IUnbakedModel model = LOOKUP.get(path);
        return model != null ? model : ModelLoaderRegistry.getMissingModel();
    }
}
