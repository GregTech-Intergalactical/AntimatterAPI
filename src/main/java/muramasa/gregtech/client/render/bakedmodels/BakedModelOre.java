package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.client.render.overrides.ItemOverrideOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class BakedModelOre extends BakedModelBase {

    private static Texture[] textures = new Texture[] {
        new Texture("minecraft", "blocks/stone"),
        new Texture("minecraft", "blocks/stone_granite"),
        new Texture("minecraft", "blocks/stone_diorite"),
        new Texture("minecraft", "blocks/stone_andesite"),
        new Texture("minecraft", "blocks/netherrack"),
        new Texture("minecraft", "blocks/end_stone"),
        StoneType.GRANITE_RED.getTexture(),
        StoneType.GRANITE_BLACK.getTexture(),
        StoneType.MARBLE.getTexture(),
        StoneType.BASALT.getTexture(),
    };

    private static IBakedModel[] bakedModels;
    private static ItemOverrideOre itemOverride;

    public BakedModelOre() {

    }

    public BakedModelOre(IBakedModel[] bakedModels) {
        super(bakedModels[0]);
        this.bakedModels = bakedModels;
        itemOverride = new ItemOverrideOre(bakedModels);
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();
        if (!(state instanceof IExtendedBlockState)) return quadList;
        IExtendedBlockState exState = (IExtendedBlockState) state;

        int setId = exState.getValue(GTProperties.SET);
        quadList.addAll(bakedModels[setId].getQuads(state, side, rand));

        int stoneId = exState.getValue(GTProperties.STONE);
        if (stoneId > 0) {
            tex(quadList, 0, textures[stoneId]);
        }

        return quadList;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverride;
    }
}
