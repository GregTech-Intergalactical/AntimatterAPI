package muramasa.itech.client.render.models;

import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.client.render.bakedmodels.BakedModelBase;
import muramasa.itech.client.render.bakedmodels.BakedModelMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.HashMap;
import java.util.function.Function;

public class ModelMachine extends ModelBase {

    public static boolean hasBeenBuilt;

    public ModelMachine() {
        super("ModelMachine");
        addTextures(Tier.getTextures(Tier.getBasic()));
        for (Machine type : MachineFlag.BASIC.getTypes()) {
            addTexture(type.getOverlayTexture(0));
            if (type.getName().equals("alloy_smelter")) {
                addTexture(type.getOverlayTexture(1));
            }
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            addTexture(new ResourceLocation(Ref.MODID + ":blocks/machines/base/" + type.getName()));
            addTexture(type.getOverlayTexture(0));
        }
        for (Machine type : MachineFlag.HATCH.getTypes()) {
            addTexture(type.getOverlayTexture(0));
        }
        for (CoverType coverType : CoverType.values()) {
            addTexture(coverType.getTextureLoc());
        }
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        if (!hasBeenBuilt) {
            IModel machineBase = load(new ModelResourceLocation(Ref.MODID + ":machine_part/machine_base"));
            IModel itemBase = load(new ModelResourceLocation(Ref.MODID + ":machine_part/machine_base_item"));
            IModel model;
            ResourceLocation texLoc;

            IBakedModel[] bakedBase;
            bakedBase = new IBakedModel[] {
                texAndBake(machineBase, "base", SOUTH, Tier.LV.getBaseTexture()),
                texAndBake(machineBase, "base", NORTH, Tier.LV.getBaseTexture()),
                texAndBake(machineBase, "base", EAST, Tier.LV.getBaseTexture()),
                texAndBake(machineBase, "base", WEST, Tier.LV.getBaseTexture()),
            };

            for (Machine type : MachineFlag.MULTI.getTypes()) {
                texLoc = new ResourceLocation(Ref.MODID + ":blocks/machines/base/" + type.getName());
//                addBaked("base", type.getName(), new IBakedModel[] {
//                    texAndBake(machineBase, "base", SOUTH, texLoc),
//                    texAndBake(machineBase, "base", NORTH, texLoc),
//                    texAndBake(machineBase, "base", EAST, texLoc),
//                    texAndBake(machineBase, "base", WEST, texLoc),
//                });
            }

            IBakedModel[][] bakedOverlays = new IBakedModel[Machine.getLastInternalId()][4];

            for (Machine type : MachineFlag.getTypes(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH)) {
                model = load(type.getOverlayModel());
                bakedOverlays[type.getInternalId()] = new IBakedModel[] {
                    texAndBake(model, "0", SOUTH, Tier.LV.getBaseTexture()),
                    texAndBake(model, "0", NORTH, Tier.LV.getBaseTexture()),
                    texAndBake(model, "0", EAST, Tier.LV.getBaseTexture()),
                    texAndBake(model, "0", WEST, Tier.LV.getBaseTexture())
                };
            }

            HashMap<String, IBakedModel> bakedItems = new HashMap<>();
            for (MachineStack stack : MachineFlag.getStacks(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH)) {
                texLoc = !stack.getType().hasFlag(MachineFlag.MULTI) ? stack.getTier().getBaseTexture() : new ResourceLocation(Ref.MODID + ":blocks/machines/base/" + stack.getType().getName());
                bakedItems.put(stack.getType().getName() + stack.getTier().getName(), new BakedModelBase(
                    texAndBake(itemBase, new String[]{"base", "overlay"}, new ResourceLocation[]{texLoc, stack.getType().getOverlayTexture(0)})
                ));
            }

            IBakedModel[][] bakedCovers = new IBakedModel[Machine.getLastInternalId()][4];
            for (CoverType coverType : CoverType.values()) {
                if (!coverType.canBeRendered()) continue;
                model = load(coverType.getModelLoc());
                texLoc = coverType.getTextureLoc();
                bakedCovers[coverType.ordinal()] = new IBakedModel[] {
                    texAndBake(model, "base", SOUTH, texLoc),
                    texAndBake(model, "base", EAST, texLoc),
                    texAndBake(model, "base", WEST, texLoc),
                    texAndBake(model, "base", DOWN, texLoc),
                    texAndBake(model, "base", UP, texLoc),
                };
            }

            hasBeenBuilt = true;
            return new BakedModelMachine(bakedBase, bakedOverlays, bakedCovers, bakedItems);
        }

        return new BakedModelMachine();
    }
}
