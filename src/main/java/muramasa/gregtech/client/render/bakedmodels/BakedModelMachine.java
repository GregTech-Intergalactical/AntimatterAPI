package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.client.render.RenderHelper;
import muramasa.gregtech.client.render.overrides.ItemOverrideMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedModelMachine extends BakedModelBase {

    private static ItemOverrideMachine itemOverride;

    private static AxisAngle4f[] facingToAxisAngle = new AxisAngle4f[] {
        new AxisAngle4f(new Vector3f(1, 0, 0), 4.7124f),
        new AxisAngle4f(new Vector3f(1, 0, 0), 1.5708f),
        new AxisAngle4f(new Vector3f(0, 1, 0), 0f),
        new AxisAngle4f(new Vector3f(0, 1, 0), 3.1416f),
        new AxisAngle4f(new Vector3f(0, 1, 0), 1.5708f),
        new AxisAngle4f(new Vector3f(0, 1, 0), 4.7124f)
    };

    private IBakedModel bakedBase;
    private IBakedModel[][] bakedOverlays;
    private IBakedModel[] bakedCovers;

    public BakedModelMachine() {

    }

    public BakedModelMachine(IBakedModel base, IBakedModel[][] overlays, IBakedModel[] covers, HashMap<String, IBakedModel> bakedItems) {
        bakedBase = base;
        bakedOverlays = overlays;
        bakedCovers = covers;
        if (itemOverride == null) {
            itemOverride = new ItemOverrideMachine(bakedItems);
        }
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();
        if (!(state instanceof IExtendedBlockState)) return quadList;
        IExtendedBlockState exState = (IExtendedBlockState) state;

        int type = exState.getValue(GTProperties.TYPE);
        int tier = exState.getValue(GTProperties.TIER);
        int overlay = exState.getValue(GTProperties.OVERLAY);
        int facing = exState.getValue(GTProperties.FACING);

        //TODO optimize base model by adding tintindex 0 to faces that are visible only
        TextureAtlasSprite baseSprite = RenderHelper.getSprite(exState.getValue(GTProperties.TEXTURE));
        quadList.addAll(retexture(bakedBase.getQuads(state, side, rand), baseSprite));

        //Add overlay quads
        //F:0, B:1, T:2, B:3, S:4

        List<BakedQuad> overlayQuads = new LinkedList<>();
//        if (overlay > 0) {
            overlayQuads.addAll(retexture(bakedOverlays[type][0].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "top"))));
            overlayQuads.addAll(retexture(bakedOverlays[type][1].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "bottom"))));
            overlayQuads.addAll(retexture(bakedOverlays[type][2].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "front"))));
            overlayQuads.addAll(retexture(bakedOverlays[type][3].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "back"))));
            overlayQuads.addAll(retexture(bakedOverlays[type][4].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "side"))));
//        } else {
//            overlayQuads.addAll(bakedOverlays[type][0].getQuads(state, side, rand));
//            overlayQuads.addAll(bakedOverlays[type][1].getQuads(state, side, rand));
//            overlayQuads.addAll(bakedOverlays[type][2].getQuads(state, side, rand));
//            overlayQuads.addAll(bakedOverlays[type][3].getQuads(state, side, rand));
//            overlayQuads.addAll(bakedOverlays[type][4].getQuads(state, side, rand));
//        }
        if (facing > 0) {
            overlayQuads = transform(overlayQuads, facingToAxisAngle[facing + 2]);
        }

        quadList.addAll(overlayQuads);


//        if (hasUnlistedProperty(exState, GTProperties.COVERS)) {
//            CoverBehaviour[] covers = exState.getValue(GTProperties.COVERS);
//            for (int i = 0; i < covers.length; i++) {
//                if (covers[i].isEmpty()) {
//                    quadList.addAll(filter(overlayQuads, i));
//                } else {
//                    quadList.addAll(covers[i].onRender(transform(bakedCovers[covers[i].getInternalId()].getQuads(exState, side, rand), facingToAxisAngle[i])));
//                    if (covers[i].retextureToMachineTier()) {
//                        //TODO
//                    }
//                }
//            }
//        } else {
//            List<BakedQuad> overlayQuads = new LinkedList<>();
//            if (overlay > 0) {
//                overlayQuads.addAll(retexture(bakedOverlays[type][0].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "top"))));
//                overlayQuads.addAll(retexture(bakedOverlays[type][1].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "bottom"))));
//                overlayQuads.addAll(retexture(bakedOverlays[type][2].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "front"))));
//                overlayQuads.addAll(retexture(bakedOverlays[type][3].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "back"))));
//                overlayQuads.addAll(retexture(bakedOverlays[type][4].getQuads(state, side, rand), RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay, "side"))));
//            } else {
//                overlayQuads.addAll(bakedOverlays[type][0].getQuads(state, side, rand));
//                overlayQuads.addAll(bakedOverlays[type][1].getQuads(state, side, rand));
//                overlayQuads.addAll(bakedOverlays[type][2].getQuads(state, side, rand));
//                overlayQuads.addAll(bakedOverlays[type][3].getQuads(state, side, rand));
//                overlayQuads.addAll(bakedOverlays[type][4].getQuads(state, side, rand));
//            }
//            if (facing > 0) {
//                overlayQuads = transform(overlayQuads, facingToAxisAngle[facing + 2]);
//            }
//            quadList.addAll(overlayQuads);
//        }

//        //Add cover quads
        if (hasUnlistedProperty(exState, GTProperties.COVERS)) {
            Cover[] covers = exState.getValue(GTProperties.COVERS);
            if (covers == null) return quadList;
            for (int i = 0; i < covers.length; i++) {
                if (covers[i].isEmpty()) continue;
                quadList.addAll(covers[i].onRender(transform(bakedCovers[covers[i].getInternalId()].getQuads(exState, side, rand), facingToAxisAngle[i])));
                if (covers[i].retextureToMachineTier()) {
                    //TODO
                }
            }
        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
