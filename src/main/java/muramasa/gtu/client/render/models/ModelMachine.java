package muramasa.gtu.client.render.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.texture.TextureType;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedBase;
import muramasa.gtu.client.render.bakedmodels.BakedMachine;
import muramasa.gtu.client.render.bakedmodels.BakedMachineBasic;
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
import java.util.Arrays;
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
        if (!Configs.MISC.BASIC_MACHINE_MODELS) {
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

        BakedMachine.ITEMS = new Object2ObjectOpenHashMap<>();
        for (Machine type : machines) {
            for (Tier tier : type.getTiers()) {
                IModel model = ModelUtils.tex(BASE, "0", type.getBaseTexture(tier));
                model = ModelUtils.tex(model, new String[] {"1", "2", "3", "4", "5", "6"}, type.getOverlayTextures(MachineState.ACTIVE));
                BakedMachine.ITEMS.put(type.getId() + "_" + tier.getId(), new BakedBase(model.bake(state, format, getter)));
            }
        }

        BakedMachine.COVERS = new Object2ObjectOpenHashMap<>();
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            ModelResourceLocation loc = Configs.MISC.BASIC_MACHINE_MODELS ? Cover.getBasicModel() : cover.getModel();
            BakedMachine.COVERS.put(cover.getId(), ModelUtils.tex(ModelUtils.load(loc), "base", cover.getTextures()[0]).bake(state, format, getter));
        }

        return Configs.MISC.BASIC_MACHINE_MODELS ? new BakedMachineBasic() : new BakedMachine();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> locations = new ArrayList<>();
        for (Tier tier : Tier.getAllElectric()) {
            locations.add(tier.getBaseTexture());
        }
        for (Tier tier : Tier.getSteam()) {
            locations.add(tier.getBaseTexture());
        }
        for (Machine type : getTypes(BASIC, HATCH, MULTI)) {
            locations.addAll(type.getTextures());
        }
        locations.addAll(Machines.INVALID.getTextures());
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            locations.addAll(Arrays.asList(cover.getTextures()));
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
