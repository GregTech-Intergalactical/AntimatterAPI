package muramasa.gtu.common.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.texture.IBakedTile;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.overrides.ItemOverrideTextureData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public abstract class BlockBaked extends Block {

    private TextureData data;
    private ModelResourceLocation model;

    public BlockBaked(TextureData data, ModelResourceLocation model) {
        super(Material.ROCK);
        this.data = data;
        this.model = model;
    }

    public BlockBaked(TextureData data) {
        this(data, new ModelResourceLocation(Ref.MODID + ":basic"));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(GTProperties.TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        return exState.withProperty(GTProperties.TEXTURE, hasTileEntity(state) ? getTileData(state, world, pos) : getBlockData());
    }

    public TextureData getTileData(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof IBakedTile ? ((IBakedTile) tile).getTextureData() : null;
    }

    public TextureData getBlockData() {
        return data;
    }

    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel() {
        return model;
    }

    @SideOnly(Side.CLIENT)
    public List<Texture> getTextures() {
        return Collections.emptyList();
    }

    @SideOnly(Side.CLIENT)
    public ItemOverrideList getOverride(IBakedModel baked) {
        return new ItemOverrideTextureData(baked);
    }
}
