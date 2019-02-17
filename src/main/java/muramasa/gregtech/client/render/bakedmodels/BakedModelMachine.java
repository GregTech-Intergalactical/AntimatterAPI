package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.cover.CoverBehaviour;
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
    private IBakedModel[] bakedOverlays, bakedCovers;

    public BakedModelMachine() {

    }

    public BakedModelMachine(IBakedModel base, IBakedModel[] overlays, IBakedModel[] covers, HashMap<String, IBakedModel> bakedItems) {
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
        List<BakedQuad> overlayQuads = /*retexture(*/bakedOverlays[type].getQuads(state, side, rand)/*, 0, baseSprite)*/;
//        //TODO fix this
//        if (overlay > 0) {
//            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(Machines.ALLOY_SMELTER.getOverlayTexture(1)));
//        } else {
//            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(Machines.ALLOY_SMELTER.getOverlayTexture(0)));
//        }
        if (overlay > 0) {
            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(Machines.get(type).getOverlayTexture(overlay)));
        }
//        if (type == Machines.ALLOY_SMELTER.getInternalId()) overlayQuads.clear();
        if (facing > 0) {
            overlayQuads = transform(overlayQuads, facingToAxisAngle[facing + 2]);
        }
        quadList.addAll(overlayQuads);

        //Add cover quads
        if (hasUnlistedProperty(exState, GTProperties.COVERS)) {
            List<BakedQuad> coverQuads = new LinkedList<>();
            CoverBehaviour[] covers = exState.getValue(GTProperties.COVERS);
            if (covers == null) return quadList;
            for (int i = 0; i < covers.length; i++) {
                if (covers[i].isEmpty()) continue;
                coverQuads.clear();
                coverQuads.addAll(covers[i].onRender(transform(bakedCovers[covers[i].getInternalId()].getQuads(exState, side, rand), facingToAxisAngle[i])));
                if (covers[i].retextureToMachineTier()) {
                    //TODO
                }
                quadList.addAll(coverQuads);
            }
        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
