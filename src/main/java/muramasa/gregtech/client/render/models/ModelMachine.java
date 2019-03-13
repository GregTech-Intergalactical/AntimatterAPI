package muramasa.gregtech.client.render.models;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.MachineState;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.texture.TextureType;
import muramasa.gregtech.client.render.bakedmodels.BakedModelBase;
import muramasa.gregtech.client.render.bakedmodels.BakedModelMachine;
import muramasa.gregtech.client.render.bakedmodels.BakedModelMachineItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
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
            if (type.hasFlag(MULTI)) addTextures(type.getBaseTextures(Tier.MULTI));
        }
        addTextures(Machines.INVALID.getTextures());
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            addTextures(cover.getTextures());
        }
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IModel BASE = load("machine/base");
        BakedModelMachine.BASE = BASE.bake(state, format, getter);

        Collection<Machine> machines = Machines.getTypes(BASIC, MULTI, HATCH);
        machines.add(Machines.INVALID);

        if (!Ref.BASIC_MACHINE_MODELS) {
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
            BakedModelMachine.OVERLAY_EMPTY = load(new ModelResourceLocation(Ref.MODID + ":machine/overlay_empty")).bake(state, format, getter);
        }

        BakedModelMachineItem.OVERLAYS = new IBakedModel[Machine.getLastInternalId()];
        for (Machine type : machines) {
            BakedModelMachineItem.OVERLAYS[type.getInternalId()] = new BakedModelBase(
                texAndBake(BASE, new String[] {"1", "2", "3", "4", "5", "6"}, type.getOverlayTextures(MachineState.ACTIVE)
            ));
        }

        BakedModelMachine.COVERS = new IBakedModel[Cover.getLastInternalId()];
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            ModelResourceLocation loc = Ref.BASIC_MACHINE_MODELS ? cover.getBasicModel() : cover.getModel();
            BakedModelMachine.COVERS[cover.getInternalId()] = texAndBake(load(loc), "base", cover.getTextures()[0]);
        }

        return Ref.BASIC_MACHINE_MODELS ? new BakedModelMachine() : new BakedModelMachine();
    }
}
