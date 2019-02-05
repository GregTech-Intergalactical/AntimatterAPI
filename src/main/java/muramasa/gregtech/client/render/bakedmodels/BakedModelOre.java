package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.properties.ITechProperties;
import muramasa.gregtech.client.render.RenderHelper;
import muramasa.gregtech.client.render.overrides.ItemOverrideOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class BakedModelOre extends BakedModelBase {

    private static TextureAtlasSprite[] sprites = new TextureAtlasSprite[] {
        RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/stone")),
        RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/stone_granite")),
        RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/stone_diorite")),
        RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/stone_andesite")),
        RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/netherrack")),
        RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/end_stone")),
    };

    private static IBakedModel[] bakedModels;
    private static ItemOverrideOre itemOverride;

    public BakedModelOre(IBakedModel[] bakedModels) {
        super(bakedModels[0]);
        this.bakedModels = bakedModels;
        if (itemOverride == null) {
            itemOverride = new ItemOverrideOre(bakedModels);
        }
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();
        if (!(state instanceof IExtendedBlockState)) return quadList;
        IExtendedBlockState exState = (IExtendedBlockState) state;

        int setId = Materials.get(exState.getValue(ITechProperties.MATERIAL)).getSet().ordinal();
        quadList.addAll(bakedModels[setId].getQuads(state, side, rand));

        int stoneId = exState.getValue(ITechProperties.STONE);
        if (stoneId > 0) {
            retexture(quadList, 0, sprites[stoneId]);
        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
