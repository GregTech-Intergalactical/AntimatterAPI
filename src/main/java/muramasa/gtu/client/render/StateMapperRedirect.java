package muramasa.gtu.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperRedirect extends StateMapperBase {

    private ModelResourceLocation defaultLocation;

    public StateMapperRedirect(ResourceLocation loc) {
        this(new ModelResourceLocation(loc, "normal"));
    }

    public StateMapperRedirect(ModelResourceLocation loc) {
        this.defaultLocation = loc;
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return defaultLocation;
    }
}
