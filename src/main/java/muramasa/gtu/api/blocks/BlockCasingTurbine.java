package muramasa.gtu.api.blocks;

import muramasa.gtu.api.data.Textures;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.registry.IRegistry;

//TODO use extended state and getActualState with bakedmodel?
public class BlockCasingTurbine extends BlockCasing {

    protected PropertyInteger TEXTURE = PropertyInteger.create("texture", 0, 16);

    public BlockCasingTurbine(String id) {
        super(id);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TEXTURE).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TEXTURE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TEXTURE, meta);
    }

    @Override
    public void onTextureStitch(TextureMap map) {
        for (int i = 0; i < Textures.LARGE_TURBINE.length; i++) {
            map.registerSprite(Textures.LARGE_TURBINE[i]);
        }
        for (int i = 0; i < Textures.LARGE_TURBINE_ACTIVE.length; i++) {
            map.registerSprite(Textures.LARGE_TURBINE_ACTIVE[i]);
        }
    }

    @Override
    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {

    }
}
