package muramasa.gtu.api.blocks;

import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.properties.UnlistedInteger;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.client.render.GTModelLoader;
import muramasa.gtu.client.render.models.ModelTurbineCasing;
import muramasa.gtu.common.tileentities.multi.TileEntityLargeTurbine;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Set;

public class BlockCasingTurbine extends BlockCasing {

    public static UnlistedInteger CTM = new UnlistedInteger(), TILE_FACING = new UnlistedInteger();

    public BlockCasingTurbine(String id) {
        super(id);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CTM, TILE_FACING).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile;
        //TODO pass ctm array, so shared wall turbines are possible
        for (int s = 0; s < 6; s++) {
            if ((tile = world.getTileEntity(pos.offset(EnumFacing.VALUES[s]))) instanceof TileEntityLargeTurbine) {
                return exState.withProperty(CTM, (1 << s) + (((TileEntityMachine) tile).getFacing().getIndex() * 100)).withProperty(TILE_FACING, ((TileEntityLargeTurbine) tile).getFacing().getIndex());
            } else if ((tile = world.getTileEntity(pos.offset(EnumFacing.VALUES[s]).down())) instanceof TileEntityLargeTurbine) {
                return exState.withProperty(CTM, (1 << s) + (1 << EnumFacing.DOWN.getIndex()) + (((TileEntityLargeTurbine) tile).getFacing().getIndex() * 100)).withProperty(TILE_FACING, ((TileEntityLargeTurbine) tile).getFacing().getIndex());
            } else if ((tile = world.getTileEntity(pos.offset(EnumFacing.VALUES[s]).up())) instanceof TileEntityLargeTurbine) {
                return exState.withProperty(CTM, (1 << s) + (1 << EnumFacing.UP.getIndex()) + (((TileEntityLargeTurbine) tile).getFacing().getIndex() * 100)).withProperty(TILE_FACING, ((TileEntityLargeTurbine) tile).getFacing().getIndex());
            }
        }
        return exState.withProperty(CTM, -1).withProperty(TILE_FACING, -1);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTextures(Set<ResourceLocation> textures) {
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE));
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE_ACTIVE));
        textures.add(getData().getBase(0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        super.onModelRegistration();
        GTModelLoader.register(getId(), new ModelTurbineCasing());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {

    }
}
