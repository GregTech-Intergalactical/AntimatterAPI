package muramasa.itech.client.render.bakedmodels;

import muramasa.itech.client.render.RenderHelper;
import muramasa.itech.common.blocks.BlockOres;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedModelOre extends BakedModelBase {

    private static HashMap<String, TextureAtlasSprite> spriteLookup = new HashMap<>();

    static {
        spriteLookup.put("minecraft:dirt", RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/dirt")));
        spriteLookup.put("minecraft:sand", RenderHelper.getSprite(new ResourceLocation("minecraft", "blocks/sand")));
    }

    public BakedModelOre(IBakedModel bakedModel) {
        super(bakedModel);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();
        if (!(state instanceof IExtendedBlockState)) return quadList;
        IExtendedBlockState exState = (IExtendedBlockState) state;

        quadList.addAll(bakedModel.getQuads(state, side, rand));

//        System.out.println("BM: " + exState.getValue(BlockOres.TEXTURE));
        TextureAtlasSprite sprite = spriteLookup.get(exState.getValue(BlockOres.TEXTURE));
        if (sprite != null) {
            quadList = retexture(quadList, 0, sprite);
        } else {
//            System.out.println("sprite null");
        }

//        quadList = retexture(quadList, 1, RenderHelper.getSprite(new ResourceLocation(ITech.MODID + ":blocks/hazard")));

        return quadList;
    }
}
