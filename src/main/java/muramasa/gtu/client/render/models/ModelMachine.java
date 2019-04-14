package muramasa.gtu.client.render.models;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureType;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedBase;
import muramasa.gtu.client.render.bakedmodels.BakedMachine;
import muramasa.gtu.client.render.bakedmodels.BakedMachineBasic;
import muramasa.gtu.client.render.bakedmodels.BakedMachineItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class ModelMachine implements IModel {

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IModel BASE = ModelUtils.load("machine/base");
        BakedMachine.BASE = BASE.bake(state, format, getter);
        Collection<Machine> machines = Machines.getTypes(BASIC, MULTI, HATCH);
        machines.add(Machines.INVALID);
        if (!Ref.BASIC_MACHINE_MODELS) {
            //TODO merge overlays into single model
            BakedMachine.OVERLAYS = new IBakedModel[Machine.getLastInternalId()][6];
            IBakedModel overlay;
            for (Machine type : machines) {
                overlay = ModelUtils.load(type.getOverlayModel(TextureType.BOTTOM)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][0] = overlay;
                overlay = ModelUtils.load(type.getOverlayModel(TextureType.TOP)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][1] = overlay;
                overlay = ModelUtils.load(type.getOverlayModel(TextureType.FRONT)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][2] = overlay;
                overlay = ModelUtils.load(type.getOverlayModel(TextureType.BACK)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][3] = overlay;
                overlay = ModelUtils.load(type.getOverlayModel(TextureType.SIDE)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][4] = overlay;
            }

            IModel overlayEmpty = ModelUtils.load(new ModelResourceLocation(Ref.MODID + ":machine/overlay_empty"));
            BakedMachine.OVERLAY_EMPTY = new IBakedModel[6];
            for (int i = 0; i < 6; i++) {
                BakedMachine.OVERLAY_EMPTY[i] = overlayEmpty.bake(TRSRTransformation.from(EnumFacing.VALUES[i]), format, getter);
            }
        }

        BakedMachineItem.OVERLAYS = new IBakedModel[Machine.getLastInternalId()];
        for (Machine type : machines) {
            BakedMachineItem.OVERLAYS[type.getInternalId()] = new BakedBase(
                ModelUtils.tex(BASE, new String[] {"1", "2", "3", "4", "5", "6"}, type.getOverlayTextures(MachineState.ACTIVE)).bake(state, format, getter)
            );
        }

        BakedMachine.COVERS = new IBakedModel[Cover.getLastInternalId()];
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            ModelResourceLocation loc = Ref.BASIC_MACHINE_MODELS ? Cover.getBasicModel() : cover.getModel();
            BakedMachine.COVERS[cover.getInternalId()] = ModelUtils.tex(ModelUtils.load(loc), "base", cover.getTextures()[0]).bake(state, format, getter);
        }

        return Ref.BASIC_MACHINE_MODELS ? new BakedMachineBasic() : new BakedMachine();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> locations = new ArrayList<>();
        for (Tier tier : Tier.getAllElectric()) {
            locations.add(tier.getBaseTexture().getLoc());
        }
        for (Tier tier : Tier.getSteam()) {
            locations.add(tier.getBaseTexture().getLoc());
        }
        for (Machine type : getTypes(BASIC, HATCH, MULTI)) {
            for (Texture texture : type.getTextures()) {
                locations.add(texture.getLoc());
            }
            if (type.hasFlag(MULTI)) {
                locations.add(type.getBaseTexture(Tier.MULTI).getLoc());
            }
        }
        for (Texture texture : Machines.INVALID.getTextures()) {
            locations.add(texture.getLoc());
        }
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            for (Texture texture : cover.getTextures()) {
                locations.add(texture.getLoc());
            }
        }
//        Arrays.stream(Tier.getAllElectric()).forEach(t -> locs.add(t.getBaseTexture().getLoc()));
//        Arrays.stream(Tier.getSteam()).forEach(t -> locs.add(t.getBaseTexture().getLoc()));
//        getTypes(BASIC, HATCH, MULTI).forEach(m -> m.getTextures().forEach(t -> locs.add(t.getLoc())));
//        getTypes(MULTI).forEach(m -> locs.add(m.getBaseTexture(Tier.MULTI).getLoc()));
//        Machines.INVALID.getTextures().forEach(t -> locs.add(t.getLoc()));
//        GregTechAPI.getRegisteredCovers().forEach(c -> { if (!c.isEmpty()) Arrays.stream(c.getTextures()).forEach(t -> locs.add(t.getLoc())); });
        return locations;
    }
}
