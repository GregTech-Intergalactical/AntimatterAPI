package muramasa.gregtech.client.render.models;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.MachineState;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.api.texture.TextureType;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import muramasa.gregtech.client.render.bakedmodels.BakedMachine;
import muramasa.gregtech.client.render.bakedmodels.BakedMachineBasic;
import muramasa.gregtech.client.render.bakedmodels.BakedMachineItem;
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

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class ModelMachine extends ModelBase {

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IModel BASE = load("machine/base");
        BakedMachine.BASE = BASE.bake(state, format, getter);

        Collection<Machine> machines = Machines.getTypes(BASIC, MULTI, HATCH);
        machines.add(Machines.INVALID);

        if (!Ref.BASIC_MACHINE_MODELS) {
            //TODO use Pair<int, IBakedModel>[] instead?

            BakedMachine.OVERLAYS = new IBakedModel[Machine.getLastInternalId()][6];
            IBakedModel overlay;
            for (Machine type : machines) {
                overlay = load(type.getOverlayModel(TextureType.BOTTOM)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][0] = overlay;
                overlay = load(type.getOverlayModel(TextureType.TOP)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][1] = overlay;
                overlay = load(type.getOverlayModel(TextureType.FRONT)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][2] = overlay;
                overlay = load(type.getOverlayModel(TextureType.BACK)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][3] = overlay;
                overlay = load(type.getOverlayModel(TextureType.SIDE)).bake(state, format, getter);
                if (overlay.getQuads(null, null, 0).size() > 0) BakedMachine.OVERLAYS[type.getInternalId()][4] = overlay;
            }

            IModel overlayEmpty = load(new ModelResourceLocation(Ref.MODID + ":machine/overlay_empty"));
            BakedMachine.OVERLAY_EMPTY = new IBakedModel[6];
            for (int i = 0; i < 6; i++) {
                BakedMachine.OVERLAY_EMPTY[i] = overlayEmpty.bake(TRSRTransformation.from(EnumFacing.VALUES[i]), format, getter);
            }
        }

        BakedMachineItem.OVERLAYS = new IBakedModel[Machine.getLastInternalId()];
        for (Machine type : machines) {
            BakedMachineItem.OVERLAYS[type.getInternalId()] = new BakedBase(
                texAndBake(BASE, new String[] {"1", "2", "3", "4", "5", "6"}, type.getOverlayTextures(MachineState.ACTIVE)
            ));
        }

        BakedMachine.COVERS = new IBakedModel[Cover.getLastInternalId()];
        for (Cover cover : GregTechAPI.getRegisteredCovers()) {
            if (cover.isEmpty()) continue;
            ModelResourceLocation loc = Ref.BASIC_MACHINE_MODELS ? cover.getBasicModel() : cover.getModel();
            BakedMachine.COVERS[cover.getInternalId()] = texAndBake(load(loc), "base", cover.getTextures()[0]);
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
        return locations;
    }
}
