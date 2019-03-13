package muramasa.gregtech.client.render.models;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.texture.TextureType;
import muramasa.gregtech.client.render.bakedmodels.BakedModelMachine;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class ModelMachine extends ModelBase {

    public ModelMachine() {
        super("ModelMachine");
        addTextures(Tier.getTextures(Tier.getAllElectric()));
        addTextures(Tier.getTextures(Tier.getSteam()));
        for (Machine type : getTypes(BASIC, HATCH, MULTI)) {
            addTextures(type.getTextures());
            if (type.hasFlag(MULTI)) addTexture(type.getBaseTexture(Tier.MULTI));
        }
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            addTextures(cover.getTextures());
        }
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        BakedModelMachine.BAKED = (Ref.BASIC_MACHINE_MODELS ? load("machine_part/machine_base_basic") : load("machine_part/machine_base")).bake(state, format, getter);

        if (!Ref.BASIC_MACHINE_MODELS) {
            Collection<Machine> machines = Machines.getTypes(BASIC, MULTI, HATCH);
            machines.add(Machines.INVALID);

            //TODO use Pair<int, IBakedModel>[] instead?

            BakedModelMachine.OVERLAYS = new IBakedModel[Machine.getLastInternalId()][6];
            IBakedModel overlay;
            for (Machine type : machines) {
                overlay = load(type.getOverlayModel(TextureType.BOTTOM)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedModelMachine.OVERLAYS[type.getInternalId()][0] = overlay;
                overlay = load(type.getOverlayModel(TextureType.TOP)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedModelMachine.OVERLAYS[type.getInternalId()][1] = overlay;
                overlay = load(type.getOverlayModel(TextureType.FRONT)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedModelMachine.OVERLAYS[type.getInternalId()][2] = overlay;
                overlay = load(type.getOverlayModel(TextureType.BACK)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedModelMachine.OVERLAYS[type.getInternalId()][3] = overlay;
                overlay = load(type.getOverlayModel(TextureType.SIDE)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedModelMachine.OVERLAYS[type.getInternalId()][4] = overlay;
            }
            BakedModelMachine.OVERLAY_EMPTY = load(new ModelResourceLocation(Ref.MODID + ":machine_part/overlay_empty")).bake(state, format, getter);
        }

        BakedModelMachine.COVERS = new IBakedModel[Cover.getLastInternalId()];
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            ModelResourceLocation loc = Ref.BASIC_MACHINE_MODELS ? cover.getBasicModel() : cover.getModel();
            BakedModelMachine.COVERS[cover.getInternalId()] = texAndBake(load(loc), "base", cover.getTextures()[0]);
        }

        return Ref.BASIC_MACHINE_MODELS ? new BakedModelMachine() : new BakedModelMachine();

//        HashMap<String, IBakedModel> bakedItems = new HashMap<>();
//        Collection<MachineStack> machineStacks = Machines.getStacks(BASIC, MULTI, HATCH);
//        machineStacks.add(Machines.get(Machines.INVALID, Tier.LV));
//        for (MachineStack stack : machineStacks) {
//            bakedItems.put(stack.getType().getName() + stack.getTier().getName(), new BakedModelBase(
//                texAndBake(machineBaseBasic, new String[]{"base", "front", "back", "top", "bottom", "side"}, new ResourceLocation[]{
//                    stack.getType().getBaseTexture(stack.getTier()),
//                    stack.getType().getOverlayTexture(TextureType.FRONT, 1),
//                    stack.getType().getOverlayTexture(TextureType.BACK, 1),
//                    stack.getType().getOverlayTexture(TextureType.TOP, 1),
//                    stack.getType().getOverlayTexture(TextureType.BOTTOM, 1),
//                    stack.getType().getOverlayTexture(TextureType.SIDE, 1)
//                })
//            ));
//        }
    }
}
