package muramasa.itech.client.model;

import muramasa.itech.ITech;
import muramasa.itech.client.model.models.ModelCable;
import muramasa.itech.client.model.models.ModelHatch;
import muramasa.itech.client.model.models.ModelMachine;
import muramasa.itech.client.model.models.ModelMultiMachine;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelLoader implements ICustomModelLoader {

    private IResourceManager resourceManager;

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(ITech.MODID) && (
            modelLocation.getResourcePath().equals("blockmachines") ||
            modelLocation.getResourcePath().equals("blockmultimachines") ||
            modelLocation.getResourcePath().equals("blockhatches") ||
            modelLocation.getResourcePath().equals("blockcables")
        );
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        switch (modelLocation.getResourcePath()) {
            case "blockmachines": return new ModelMachine();
            case "blockmultimachines": return new ModelMultiMachine();
            case "blockhatches": return new ModelHatch();
            case "blockcables": return new ModelCable();
            default: return ModelLoaderRegistry.getMissingModel();
        }
    }
}
