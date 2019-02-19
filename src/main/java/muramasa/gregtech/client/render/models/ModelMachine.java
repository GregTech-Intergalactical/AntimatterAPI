package muramasa.gregtech.client.render.models;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.cover.CoverBehaviour;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

public class ModelMachine extends ModelBase {

    public ModelMachine() {
        super("ModelMachine");
        addTextures(Tier.getTextures(Tier.getBasic()));
        for (Machine type : MachineFlag.BASIC.getTypes()) {
            addTexture(type.getOverlayTexture(0, "front"));
            addTexture(type.getOverlayTexture(0, "back"));
            addTexture(type.getOverlayTexture(0, "top"));
            addTexture(type.getOverlayTexture(0, "bottom"));
            addTexture(type.getOverlayTexture(0, "side"));

            addTexture(type.getOverlayTexture(1, "front"));
            addTexture(type.getOverlayTexture(1, "back"));
            addTexture(type.getOverlayTexture(1, "top"));
            addTexture(type.getOverlayTexture(1, "bottom"));
            addTexture(type.getOverlayTexture(1, "side"));
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            addTexture(type.getBaseTexture("multi"));
            addTexture(type.getOverlayTexture(0, "front"));
            addTexture(type.getOverlayTexture(0, "back"));
            addTexture(type.getOverlayTexture(0, "top"));
            addTexture(type.getOverlayTexture(0, "bottom"));
            addTexture(type.getOverlayTexture(0, "side"));

            addTexture(type.getOverlayTexture(1, "front"));
            addTexture(type.getOverlayTexture(1, "back"));
            addTexture(type.getOverlayTexture(1, "top"));
            addTexture(type.getOverlayTexture(1, "bottom"));
            addTexture(type.getOverlayTexture(1, "side"));
        }
        for (Machine type : MachineFlag.HATCH.getTypes()) {
            addTexture(type.getOverlayTexture(0, "front"));
            addTexture(type.getOverlayTexture(0, "back"));
            addTexture(type.getOverlayTexture(0, "top"));
            addTexture(type.getOverlayTexture(0, "bottom"));
            addTexture(type.getOverlayTexture(0, "side"));

            addTexture(type.getOverlayTexture(1, "front"));
            addTexture(type.getOverlayTexture(1, "back"));
            addTexture(type.getOverlayTexture(1, "top"));
            addTexture(type.getOverlayTexture(1, "bottom"));
            addTexture(type.getOverlayTexture(1, "side"));
        }
        for (CoverBehaviour cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            addTexture(cover.getTextureLoc());
        }
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IModel machineBase = load(new ModelResourceLocation(Ref.MODID + ":machine_part/machine_base"));
        IModel machineBaseBasic = load(new ModelResourceLocation(Ref.MODID + ":machine_part/machine_base_basic"));
        IModel model;

        IBakedModel bakedBase = machineBase.bake(state, format, getter);
        IBakedModel[][] bakedOverlays = new IBakedModel[Machine.getLastInternalId()][5];

        Collection<Machine> machines = Machines.getTypes(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH);
        machines.add(Machines.INVALID);
        for (Machine type : machines) {
//            bakedOverlays[type.getInternalId()] = load(type.getOverlayModel()).bake(state, format, getter);
            bakedOverlays[type.getInternalId()][0] = load(type.getOverlayModel("top")).bake(state, format, getter);
            bakedOverlays[type.getInternalId()][1] = load(type.getOverlayModel("bottom")).bake(state, format, getter);
            bakedOverlays[type.getInternalId()][2] = load(type.getOverlayModel("front")).bake(state, format, getter);
            bakedOverlays[type.getInternalId()][3] = load(type.getOverlayModel("back")).bake(state, format, getter);
            bakedOverlays[type.getInternalId()][4] = load(type.getOverlayModel("side")).bake(state, format, getter);
        }

        HashMap<String, IBakedModel> bakedItems = new HashMap<>();
        Collection<MachineStack> machineStacks = Machines.getStacks(MachineFlag.BASIC, MachineFlag.MULTI, MachineFlag.HATCH);
        machineStacks.add(Machines.get(Machines.INVALID, Tier.LV));
        for (MachineStack stack : machineStacks) {
            bakedItems.put(stack.getType() + stack.getTier(), new BakedModelBase(
                texAndBake(machineBaseBasic, new String[]{"base", "front", "back", "top", "bottom", "side"}, new ResourceLocation[]{
                    stack.getMachineType().getBaseTexture(stack.getTier()),
                    stack.getMachineType().getOverlayTexture(1, "front"),
                    stack.getMachineType().getOverlayTexture(1, "back"),
                    stack.getMachineType().getOverlayTexture(1, "top"),
                    stack.getMachineType().getOverlayTexture(1, "bottom"),
                    stack.getMachineType().getOverlayTexture(1, "side")
                })
            ));
        }

        IBakedModel[] bakedCovers = new IBakedModel[CoverBehaviour.getLastInternalId()];
        for (CoverBehaviour cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            model = load(cover.getModelLoc());
            bakedCovers[cover.getInternalId()] = model.bake(state, format, getter);
        }

        return new BakedModelMachine(bakedBase, bakedOverlays, bakedCovers, bakedItems);
    }
}
