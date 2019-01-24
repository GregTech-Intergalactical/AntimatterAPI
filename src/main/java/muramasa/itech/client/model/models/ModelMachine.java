package muramasa.itech.client.model.models;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.client.model.bakedmodels.BakedModelBase;
import muramasa.itech.client.model.bakedmodels.BakedModelBaseMulti;
import muramasa.itech.client.model.bakedmodels.BakedModelMachine;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

public class ModelMachine extends ModelBase {

    private static final ModelResourceLocation MACHINE_BASE = new ModelResourceLocation(ITech.MODID + ":machineparts/machinebase");
    private static final ModelResourceLocation MACHINE_BASE_ITEM = new ModelResourceLocation(ITech.MODID + ":machineparts/machinebaseitem");

    private static final HashMap<String, ResourceLocation> baseTextures = new HashMap<>(), overlayTextures = new HashMap<>(), coverTextures = new HashMap<>();

    public static IBakedModel baseBaked;

    static {
        for (Tier tier : Tier.getAllBasic()) {
            baseTextures.put(tier.getName(), tier.getBaseTexture());
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            baseTextures.put(type.getName(), type.getBaseTexture());
        }
        for (Machine type : MachineFlag.BASIC.getTypes()) {
            overlayTextures.put(type.getName(), type.getOverlayTexture());
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            overlayTextures.put(type.getName(), type.getOverlayTexture());
        }
        for (CoverType coverType : CoverType.values()) {
            coverTextures.put(coverType.getName(), new ResourceLocation(ITech.MODID, "blocks/machines/covers/" + coverType.getName()));
        }
    }

    public ModelMachine() {
        super("ModelMachine", baseTextures.values(), overlayTextures.values(), coverTextures.values());
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        System.out.println("MODEL MACHINE BAKE");
        IModel baseModel = load(MACHINE_BASE), baseModelItem = load(MACHINE_BASE_ITEM);
        if (baseModel == null) return missingModelBaked;
        baseBaked = baseModel.bake(state, format, bakedTextureGetter);

        HashMap<String, IBakedModel[]> bakedModels = new HashMap<>();
        HashMap<String, IBakedModel> bakedModelsItem = new HashMap<>();
        Collection<MachineStack> machineStacks = MachineFlag.BASIC.getStacks();
        machineStacks.addAll(MachineFlag.MULTI.getStacks());
        for (MachineStack stack : machineStacks) {
            String tier = stack.getTier().getName(), type = stack.getType().getName();
            IModel overlayModel = load(stack.getType().getOverlayModel());

            //TODO have better handling for this
            ResourceLocation baseTexLoc = baseTextures.get(tier) != null ? baseTextures.get(tier) : baseTextures.get(type);

            bakedModels.put(type + tier, new IBakedModel[] {
                new BakedModelBaseMulti(texAndBake(baseModel, "base", SOUTH, baseTexLoc), texAndBake(overlayModel, "0", SOUTH, baseTexLoc)),
                new BakedModelBaseMulti(texAndBake(baseModel, "base", NORTH, baseTexLoc), texAndBake(overlayModel, "0", NORTH, baseTexLoc)),
                new BakedModelBaseMulti(texAndBake(baseModel, "base", EAST, baseTexLoc), texAndBake(overlayModel, "0", EAST, baseTexLoc)),
                new BakedModelBaseMulti(texAndBake(baseModel, "base", WEST, baseTexLoc), texAndBake(overlayModel, "0", WEST, baseTexLoc)),
            });
            bakedModelsItem.put(stack.getType().getName() + stack.getTier().getName(), new BakedModelBase(
                texAndBake(baseModelItem, new String[]{"base", "overlay"}, new ResourceLocation[]{baseTexLoc, overlayTextures.get(type)}))
            );
        }

        IBakedModel[][] bakedCovers = new IBakedModel[CoverType.values().length][5];
        for (CoverType coverType : CoverType.values()) {
            if (coverType.getModelLocation() == null) continue;
            IModel coverModel = load(coverType.getModelLocation());
            bakedCovers[coverType.ordinal()] = new IBakedModel[] {
                texAndBake(coverModel, "base", SOUTH, coverTextures.get(coverType.getName())),
                texAndBake(coverModel, "base", EAST, coverTextures.get(coverType.getName())),
                texAndBake(coverModel, "base", WEST, coverTextures.get(coverType.getName())),
                texAndBake(coverModel, "base", DOWN, coverTextures.get(coverType.getName())),
                texAndBake(coverModel, "base", UP, coverTextures.get(coverType.getName())),
            };
        }

        return new BakedModelMachine(bakedModels, bakedModelsItem, bakedCovers);
    }
}
