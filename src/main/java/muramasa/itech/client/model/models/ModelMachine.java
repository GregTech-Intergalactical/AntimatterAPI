package muramasa.itech.client.model.models;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.client.model.bakedmodels.BakedModelMachine;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

public class ModelMachine extends ModelBase {

    private static final ModelResourceLocation MACHINE_BASE = new ModelResourceLocation(ITech.MODID + ":machineparts/machinebase");
    private static final ModelResourceLocation MACHINE_BASE_ITEM = new ModelResourceLocation(ITech.MODID + ":machineparts/machinebaseitem");

    public static IBakedModel baseBaked;

    private static boolean hasBeenBuilt;
    //TODO merge lists, implement integer IDs
//    public static HashMap<String, IBakedModel[]> bakedModels = new HashMap<>();
//    public static HashMap<String, IBakedModel> bakedItemModels = new HashMap<>();
    public static IBakedModel[][] bakedCoverModels = new IBakedModel[CoverType.values().length][5];

    static {
        addTextures("base", Tier.getBasic(), Tier.getTextures(Tier.getBasic()));
        for (Machine type : MachineFlag.BASIC.getTypes()) {
            addTexture("overlay", type.getName(), type.getOverlayTexture());
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            addTexture("base", type.getName(), new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + type.getName()));
            addTexture("overlay", type.getName(), type.getOverlayTexture());
        }
        for (Machine type : MachineFlag.HATCH.getTypes()) {
            addTexture("overlay", type.getName(), type.getOverlayTexture());
        }
        for (CoverType coverType : CoverType.values()) {
            addTexture("cover", coverType.getName(), new ResourceLocation(ITech.MODID, "blocks/machines/covers/" + coverType.getName()));
        }
    }

    public ModelMachine() {
        super("ModelMachine");
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        if (!hasBeenBuilt) {

            IModel baseModel = load(MACHINE_BASE), baseModelItem = load(MACHINE_BASE_ITEM);
            if (baseModel == null) return missingModelBaked;
            baseBaked = baseModel.bake(state, format, bakedTextureGetter);


            ResourceLocation texLoc;
            for (Tier tier : Tier.getBasic()) {
                texLoc = tier.getBaseTexture();
                addBaked("base", tier.getName(), new IBakedModel[] {
                    texAndBake(baseModel, "base", SOUTH, texLoc),
                    texAndBake(baseModel, "base", NORTH, texLoc),
                    texAndBake(baseModel, "base", EAST, texLoc),
                    texAndBake(baseModel, "base", WEST, texLoc),
                });
            }
            for (Machine type : MachineFlag.MULTI.getTypes()) {
                texLoc = new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + type.getName());
                addBaked("base", type.getName(), new IBakedModel[] {
                    texAndBake(baseModel, "base", SOUTH, texLoc),
                    texAndBake(baseModel, "base", NORTH, texLoc),
                    texAndBake(baseModel, "base", EAST, texLoc),
                    texAndBake(baseModel, "base", WEST, texLoc),
                });
            }

            IModel overlayModel;
            for (MachineStack stack : MachineFlag.getStacks(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH)) {
                overlayModel = load(stack.getType().getOverlayModel());
                texLoc = !stack.getType().hasFlag(MachineFlag.MULTI) ? stack.getTier().getBaseTexture() : new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + stack.getType().getName());
                addBaked("overlay", stack.getType().getName() + stack.getTier().getName(), new IBakedModel[] {
                    texAndBake(overlayModel, "0", SOUTH, texLoc),
                    texAndBake(overlayModel, "0", NORTH, texLoc),
                    texAndBake(overlayModel, "0", EAST, texLoc),
                    texAndBake(overlayModel, "0", WEST, texLoc)
                });
                addBaked("item", stack.getType().getName() + stack.getTier().getName(), new IBakedModel[] {
                    texAndBake(baseModelItem, new String[]{"base", "overlay"}, new ResourceLocation[]{texLoc, getTexture("overlay", stack.getType())})
                });
            }

            IModel coverModel;
            for (CoverType coverType : CoverType.values()) {
                if (coverType.getModelLocation() == null) continue;
                coverModel = load(coverType.getModelLocation());
                bakedCoverModels[coverType.ordinal()] = new IBakedModel[] {
                    texAndBake(coverModel, "base", SOUTH, getTexture("cover", coverType)),
                    texAndBake(coverModel, "base", EAST, getTexture("cover", coverType)),
                    texAndBake(coverModel, "base", WEST, getTexture("cover", coverType)),
                    texAndBake(coverModel, "base", DOWN, getTexture("cover", coverType)),
                    texAndBake(coverModel, "base", UP, getTexture("cover", coverType)),
                };
            }

//            finish();


//            for (MachineStack stack : MachineFlag.getStacks(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH)) {
//                type = stack.getType().getName();
//                tier = stack.getTier().getName();
//                overlayModel = load(stack.getType().getOverlayModel());
//
//                //TODO have better handling for this
//                texLoc = getTexture("base", tier) != null ? getTexture("base", tier) : getTexture("base", type);
//                bakedModels.put(type + tier, new IBakedModel[] {
//                    new BakedModelBase(texAndBake(baseModel, "base", SOUTH, texLoc), texAndBake(overlayModel, "0", SOUTH, texLoc)),
//                    new BakedModelBase(texAndBake(baseModel, "base", NORTH, texLoc), texAndBake(overlayModel, "0", NORTH, texLoc)),
//                    new BakedModelBase(texAndBake(baseModel, "base", EAST, texLoc), texAndBake(overlayModel, "0", EAST, texLoc)),
//                    new BakedModelBase(texAndBake(baseModel, "base", WEST, texLoc), texAndBake(overlayModel, "0", WEST, texLoc)),
//                });
//
//                bakedItemModels.put(stack.getType().getName() + stack.getTier().getName(), new BakedModelBase(
//                    texAndBake(baseModelItem, new String[]{"base", "overlay"}, new ResourceLocation[]{texLoc, getTexture("overlay", type)}))
//                );
//
//                if (stack.getType().hasFlag(MachineFlag.HATCH)) {
//                    for (HatchTexture texture : HatchTexture.values()) {
//                        texLoc = getTexture("base", texture.getName());
//                        bakedModels.put(type + texture.getName(), new IBakedModel[] {
//                            new BakedModelBase(texAndBake(baseModel, "base", SOUTH, texLoc), texAndBake(overlayModel, "0", SOUTH, texLoc)),
//                            new BakedModelBase(texAndBake(baseModel, "base", NORTH, texLoc), texAndBake(overlayModel, "0", NORTH, texLoc)),
//                            new BakedModelBase(texAndBake(baseModel, "base", EAST, texLoc), texAndBake(overlayModel, "0", EAST, texLoc)),
//                            new BakedModelBase(texAndBake(baseModel, "base", WEST, texLoc), texAndBake(overlayModel, "0", WEST, texLoc)),
//                        });
//                    }
//                }
//            }
            hasBeenBuilt = true;
        }

        return new BakedModelMachine();
    }
}
