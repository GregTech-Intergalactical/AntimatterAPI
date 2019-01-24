package muramasa.itech.client.model.bakedmodels;

import muramasa.itech.api.properties.ITechProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BakedModelMultiMachine extends BakedModelBase {

    private IBakedModel baseModel;
    private HashMap<String, IBakedModel> bakedModels;

    public BakedModelMultiMachine(IBakedModel baseModel, HashMap<String, IBakedModel> bakedModels) {
        this.baseModel = baseModel;
        this.bakedModels = bakedModels;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();

        if (!(state instanceof IExtendedBlockState)) return quadList;
        IExtendedBlockState extendedState = (IExtendedBlockState) state;

        IBakedModel bakedModel = bakedModels.get(extendedState.getValue(ITechProperties.TYPE));
        if (bakedModel != null) {
            quadList.addAll(bakedModel.getQuads(state, side, rand));
        }

        return quadList;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
