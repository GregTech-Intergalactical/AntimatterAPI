package muramasa.itech.client.render.bakedmodels;

import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.client.render.RenderHelper;
import muramasa.itech.client.render.overrides.ItemOverrideMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedModelMachine extends BakedModelBase {

    private static TextureAtlasSprite[] baseSprites;

    private static ItemOverrideMachine itemOverride;

    private static float[] facingToRadians = new float[] {
        0f, 3.1416f, 1.5708f, 4.7124f
    };

    private static float[] coverFacingToRadians = new float[] {
        0f, 4.7124f, 1.5708f, 1.5708f, 4.7124f
    };

    static {
        //TODO better handling for this...
        baseSprites = new TextureAtlasSprite[Machine.getLastInternalId()];
        for (int i = 0; i < Tier.getBasic().length; i++) {
            baseSprites[i] = RenderHelper.getSprite(Tier.get(i).getBaseTexture());
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            baseSprites[type.getInternalId()] = RenderHelper.getSprite(new ResourceLocation(Ref.MODID + ":blocks/machines/base/" + type.getName()));
        }
    }

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

        int type = exState.getValue(ITechProperties.TYPE);
        int tier = exState.getValue(ITechProperties.TIER);
        int overlay = exState.getValue(ITechProperties.STATE);
        int facing = exState.getValue(ITechProperties.FACING);

        //Add base quads
        TextureAtlasSprite sprite;
        if (hasUnlistedProperty(exState, ITechProperties.TEXTURE)) {
            sprite = baseSprites[exState.getValue(ITechProperties.TEXTURE)];
        } else {
            if (tier < 7) {
                sprite = baseSprites[tier];
            } else {
                sprite = baseSprites[type];
            }
        }
        if (sprite == null) sprite = baseSprites[2];
        //TODO optimize base model by adding tintindex 0 to faces that are visible
        quadList.addAll(retexture(bakedBase.getQuads(state, side, rand), sprite));

        //Add overlay quads
        List<BakedQuad> overlayQuads = retexture(bakedOverlays[type].getQuads(state, side, rand), 0, sprite);
        if (overlay > 0) {
            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(MachineList.ALLOY_SMELTER.getOverlayTexture(1)));
        } else {
            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(MachineList.ALLOY_SMELTER.getOverlayTexture(0)));
        }
        if (facing > 0) {
            overlayQuads = transform(overlayQuads, facingToRadians[facing]);
        }
        quadList.addAll(overlayQuads);

        //Add cover quads
        if (hasUnlistedProperty(exState, ITechProperties.COVERS)) {
            CoverType[] covers = exState.getValue(ITechProperties.COVERS);
            for (int i = 0; i < covers.length; i++) {
                if (covers[i] == CoverType.NONE) continue;
                if (i > 0) {
                    if (i >= 3) {
                        quadList.addAll(transform(bakedCovers[covers[i].ordinal()].getQuads(exState, side, rand), new Vector3f(1, 0, 0), coverFacingToRadians[i]));
                    } else {
                        quadList.addAll(transform(bakedCovers[covers[i].ordinal()].getQuads(exState, side, rand), coverFacingToRadians[i]));
                    }
                } else {
                    quadList.addAll(bakedCovers[covers[i].ordinal()].getQuads(exState, side, rand));
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
