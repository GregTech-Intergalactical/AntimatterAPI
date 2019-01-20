package muramasa.itech.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class StateMapperRedirect extends StateMapperBase {

    private ModelResourceLocation defaultLocation;

    public StateMapperRedirect(Block block) {
        this.defaultLocation = new ModelResourceLocation(block.getRegistryName(), "normal");
    }

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return defaultLocation;
    }
}
