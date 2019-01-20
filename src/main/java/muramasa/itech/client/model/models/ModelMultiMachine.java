package muramasa.itech.client.model.models;

import muramasa.itech.ITech;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.types.MultiMachine;
import muramasa.itech.client.model.bakedmodels.BakedModelMultiMachine;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.HashMap;
import java.util.function.Function;

public class ModelMultiMachine extends ModelBase {

    private static final ModelResourceLocation MULTIMACHINE_BASE = new ModelResourceLocation(ITech.MODID + ":machineparts/multimachinebase");

    private static HashMap<String, ResourceLocation> baseTextures = new HashMap<>();

    static {
        for (MultiMachine type : MachineList.getAllMultiTypes()) {
            baseTextures.put(type.getName(), type.getBaseTexture());
        }
    }

    public ModelMultiMachine() {
        super("ModelMultiMachine", baseTextures.values());
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel baseModel = load(MULTIMACHINE_BASE);

        HashMap<String, IBakedModel> bakedModels = new HashMap<>();
        for (MultiMachine type : MachineList.getAllMultiTypes()) {
            bakedModels.put(type.getName(), texAndBake(baseModel, new String[]{"base", "overlay"}, new ResourceLocation[]{type.getBaseTexture(), type.getOverlayTexture()}));
        }

        return new BakedModelMultiMachine(baseModel.bake(state, format, bakedTextureGetter), bakedModels);
    }
}
