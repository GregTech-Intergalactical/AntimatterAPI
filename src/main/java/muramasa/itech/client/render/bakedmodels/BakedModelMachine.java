package muramasa.itech.client.render.bakedmodels;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.client.render.RenderHelper;
import muramasa.itech.client.render.overrides.ItemOverrideMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedModelMachine extends BakedModelBase {

    private static TextureAtlasSprite[] baseSprites;

    private static ItemOverrideMachine itemOverride;

    static {
        //TODO better handling for this...
        baseSprites = new TextureAtlasSprite[Machine.getLastInternalId()];
        for (int i = 0; i < Tier.getBasic().length; i++) {
            baseSprites[i] = RenderHelper.getSprite(Tier.get(i).getBaseTexture());
        }
        for (Machine type : MachineFlag.MULTI.getTypes()) {
            baseSprites[type.getInternalId()] = RenderHelper.getSprite(new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + type.getName()));
        }
    }

    private IBakedModel[] bakedBase;
    private IBakedModel[][] bakedOverlays, bakedCovers;

    public BakedModelMachine() {

}

    public BakedModelMachine(IBakedModel[] base, IBakedModel[][] overlays, IBakedModel[][] covers, HashMap<String, IBakedModel> bakedItems) {
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
        int overlay = exState.getValue(ITechProperties.STATE).getOverlayId();
        int facing = state.getValue(ITechProperties.FACING).getIndex() - 2;

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
        quadList.addAll(retexture(bakedBase[facing].getQuads(state, side, rand), sprite)); //TODO optimize base model by adding tintindex 0 to faces that are visible

        List<BakedQuad> overlayQuads = retexture(bakedOverlays[type][facing].getQuads(state, side, rand), 0, sprite);
        if (overlay > 0) {
            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(MachineList.ALLOYSMELTER.getOverlayTexture(1)));
        } else {
            overlayQuads = retexture(overlayQuads, 1, RenderHelper.getSprite(MachineList.ALLOYSMELTER.getOverlayTexture(0)));
        }
        quadList.addAll(overlayQuads);

        //Add cover quads
        if (hasUnlistedProperty(exState, ITechProperties.COVERS)) {
            CoverType[] covers = exState.getValue(ITechProperties.COVERS);
            for (int i = 0; i < covers.length; i++) {
                if (covers[i] == CoverType.NONE) continue;
                quadList.addAll(bakedCovers[covers[i].ordinal()][i].getQuads(exState, side, rand));
            }
        }

        System.out.println("Overlay ID: " + exState.getValue(ITechProperties.STATE));


//        List<BakedQuad> test = new LinkedList<>(quadList);
//        quadList.clear();
//
//
//        for (BakedQuad quad : test) {
//            quadList.add(transform(quad, TRSRTransformation.blockCornerToCenter(ModelRotation.X0_Y180.)));
//        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
