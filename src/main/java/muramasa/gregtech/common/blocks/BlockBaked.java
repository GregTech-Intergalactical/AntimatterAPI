package muramasa.gregtech.common.blocks;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.properties.GTProperties;
import muramasa.gregtech.api.texture.IBakedTile;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Collections;
import java.util.List;

public abstract class BlockBaked extends Block {

    public BlockBaked(Material material) {
        super(material);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(GTProperties.TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TextureData data = hasTileEntity ? getTileData(state, world, pos) : getBlockData();
        return exState.withProperty(GTProperties.TEXTURE, data);
    }

    public TextureData getTileData(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof IBakedTile) {
            return ((IBakedTile) tile).getTextureData();
        }
        return null;
    }

    public TextureData getBlockData() {
        return null;
    }

    public List<Texture> getTextures() {
        return Collections.emptyList();
    }

    public ModelResourceLocation getModel() {
        return new ModelResourceLocation(Ref.MODID + ":basic");
    }
}
