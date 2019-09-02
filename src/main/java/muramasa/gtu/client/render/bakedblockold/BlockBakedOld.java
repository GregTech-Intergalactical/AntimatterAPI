package muramasa.gtu.client.render.bakedblockold;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.texture.IBakedTile;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.util.Utils;
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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Deprecated
public abstract class BlockBakedOld extends Block {

    protected static ModelResourceLocation BASIC = new ModelResourceLocation(Ref.MODID + ":basic");
    protected static ModelResourceLocation LAYERED = new ModelResourceLocation(Ref.MODID + ":layered");

    private TextureData defaultData;
    private ModelResourceLocation model;

    //TODO cache default bakedmodel here

    public BlockBakedOld() {
        super(Material.ROCK);
        this.defaultData = TextureData.get().base(Textures.ERROR);
        model = BASIC;
    }

    public BlockBakedOld(TextureData data) {
        super(Material.ROCK);
        this.defaultData = data;
        model = BASIC;
    }

    public BlockBakedOld(TextureData data, ModelResourceLocation model) {
        this(data);
        this.model = model;
    }

    public void setData(TextureData data) {
        this.defaultData = data;
    }

    public void setModel(ModelResourceLocation model) {
        this.model = model;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(GTProperties.TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        return exState.withProperty(GTProperties.TEXTURE, getData(state, world, pos));
    }

    private TextureData getData(IBlockState state, IBlockAccess world, BlockPos pos) {
        TextureData data = hasTileEntity(state) ? getTileData(state, world, pos) : getDefaultData();
        return data != null ? data : defaultData;
    }

    @Nullable
    public TextureData getTileData(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = Utils.getTile(world, pos);
        return tile instanceof IBakedTile ? ((IBakedTile) tile).getTextureData() : null;
    }

    public TextureData getDefaultData() {
        return defaultData;
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
