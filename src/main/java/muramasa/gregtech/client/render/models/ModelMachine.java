package muramasa.gregtech.client.render.models;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.client.render.bakedmodels.BakedModelBase;
import muramasa.gregtech.client.render.bakedmodels.BakedModelMachine;
import muramasa.gregtech.common.utils.Ref;
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

            IBakedModel bakedBase = texAndBake(machineBase, "base", Tier.LV.getBaseTexture());

            IBakedModel[] bakedOverlays = new IBakedModel[Machine.getLastInternalId()];
            for (Machine type : Machines.getTypes(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH)) {
                model = load(type.getOverlayModel());
                bakedOverlays[type.getId()] = texAndBake(model, "0", Tier.LV.getBaseTexture());
            }

            HashMap<String, IBakedModel> bakedItems = new HashMap<>();
            for (MachineStack stack : Machines.getStacks(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH)) {
//                texLoc = !stack.getMaterial().hasFlag(MachineFlag.MULTI) ? stack.getTier().getBaseTexture() : new ResourceLocation(Ref.MODID + ":blocks/machines/base/" + stack.getMaterial().getName());
                texLoc = stack.getMachineType().getBaseTexture(stack.getTier());
                bakedItems.put(stack.getType() + stack.getTier(), new BakedModelBase(
                    texAndBake(itemBase, new String[]{"base", "overlay"}, new ResourceLocation[]{texLoc, stack.getMachineType().getOverlayTexture(0)})
                ));
            }

            IBakedModel[] bakedCovers = new IBakedModel[CoverType.values().length];
            for (CoverType coverType : CoverType.values()) {
                if (!coverType.canBeRendered()) continue;
                model = load(coverType.getModelLoc());
                texLoc = coverType.getTextureLoc();
                bakedCovers[coverType.ordinal()] = texAndBake(model, "base", texLoc);
            }

            hasBeenBuilt = true;
            return new BakedModelMachine(bakedBase, bakedOverlays, bakedCovers, bakedItems);
        }

        return new BakedModelMachine();
    }
}
