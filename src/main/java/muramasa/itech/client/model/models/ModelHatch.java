package muramasa.itech.client.model.models;

import muramasa.itech.ITech;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.api.machines.types.MultiMachine;
import muramasa.itech.client.model.bakedmodels.BakedModelBase;
import muramasa.itech.client.model.bakedmodels.BakedModelHatch;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.HashMap;
import java.util.function.Function;

public class ModelHatch extends ModelBase {

    private static final ModelResourceLocation HATCH_BASE = new ModelResourceLocation(ITech.MODID + ":machineparts/hatchbase");

    private static final HashMap<String, ResourceLocation> multiTextures = new HashMap<>();

    static {
        for (MultiMachine type : MachineList.getAllMultiTypes()) {
            multiTextures.put(type.getName(), type.getBaseTexture());
        }
    }

    public ModelHatch() {
        super("ModelHatch", baseTextures.values(), multiTextures.values());
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel baseModel = load(HATCH_BASE);

        HashMap<String, IBakedModel> bakedModels = new HashMap<>();
        for (Tier tier : Tier.getElectric()) {
            bakedModels.put(tier.getName(), new BakedModelBase(texAndBake(baseModel, "0", tier.getBaseTexture())));
        }
        for (MultiMachine type : MachineList.getAllMultiTypes()) {
            bakedModels.put(type.getName(), new BakedModelBase(texAndBake(baseModel, "0", type.getBaseTexture())));
        }

        return new BakedModelHatch(bakedModels);
    }
}
