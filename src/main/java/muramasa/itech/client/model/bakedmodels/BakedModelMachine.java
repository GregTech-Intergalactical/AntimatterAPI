package muramasa.itech.client.model.bakedmodels;

import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.HatchTexture;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.client.model.models.ModelBase;
import muramasa.itech.client.model.models.ModelMachine;
import muramasa.itech.client.model.overrides.ItemOverrideMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class BakedModelMachine extends BakedModelBase {

    private static ItemOverrideMachine itemOverride;

    public BakedModelMachine() {
        this.itemOverride = new ItemOverrideMachine();
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quadList = new LinkedList<>();

        if (!(state instanceof IExtendedBlockState)) return quadList;

        IExtendedBlockState exState = (IExtendedBlockState) state;

        String type = exState.getValue(ITechProperties.TYPE), tier = exState.getValue(ITechProperties.TIER);
        int facing = exState.getClean().getValue(ITechProperties.FACING).getIndex() - 2;

        if (type == null || type.isEmpty() || tier == null || tier.isEmpty()) return quadList;

        if (hasProperty(exState, ITechProperties.HATCH_TEXTURE)) {
            HatchTexture texture = exState.getClean().getValue(ITechProperties.HATCH_TEXTURE);
            quadList.addAll(ModelBase.getBaked("base", (texture == HatchTexture.NONE ? tier : texture.getName()))[facing].getQuads(state, side, rand));
        } else {
            IBakedModel[] baseModels = ModelBase.getBaked("base", tier);
            baseModels = baseModels != null ? baseModels : ModelBase.getBaked("base", type);
            quadList.addAll(baseModels[facing].getQuads(state, side, rand));
        }

        quadList.addAll(ModelBase.getBaked("overlay", type + tier)[facing].getQuads(state, side, rand));

        if (hasUnlistedProperty(exState, ITechProperties.COVERS)) {
            CoverType[] covers = exState.getValue(ITechProperties.COVERS);
            if (covers != null) {
                for (int i = 0; i < covers.length; i++) {
                    if (covers[i] != CoverType.NONE) {
                        quadList.addAll(ModelMachine.bakedCoverModels[covers[i].ordinal()][i].getQuads(exState, side, rand));
                    }
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
