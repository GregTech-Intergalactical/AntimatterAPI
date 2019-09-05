package muramasa.gtu.api.blocks;

import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.properties.UnlistedIntArray;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.client.render.models.ModelTurbineCasing;
import muramasa.gtu.common.tileentities.multi.TileEntityLargeTurbine;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Set;

public class BlockTurbineCasing extends BlockCasing {

    public static UnlistedIntArray CT = new UnlistedIntArray();

    public BlockTurbineCasing(String id) {
        super(id);
        registerCustomModel(getId(), new ModelTurbineCasing(this), false);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CT).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile;
        int[] ct = new int[6];
        for (int s = 0; s < 6; s++) {
            if ((tile = world.getTileEntity(pos.offset(EnumFacing.VALUES[s]))) instanceof TileEntityLargeTurbine) {
                ct[s] = (1 << s) + (((TileEntityMachine) tile).getFacing().getIndex() * 100) /*+ ((TileEntityLargeTurbine) tile).getClientProgress() > 0 ? 1000 : 0*/;
            } else if ((tile = world.getTileEntity(pos.offset(EnumFacing.VALUES[s]).down())) instanceof TileEntityLargeTurbine) {
                ct[s] = (1 << s) + (1 << EnumFacing.DOWN.getIndex()) + (((TileEntityLargeTurbine) tile).getFacing().getIndex() * 100) /*+ ((TileEntityLargeTurbine) tile).getClientProgress() > 0 ? 1000 : 0*/;
            } else if ((tile = world.getTileEntity(pos.offset(EnumFacing.VALUES[s]).up())) instanceof TileEntityLargeTurbine) {
                ct[s] = (1 << s) + (1 << EnumFacing.UP.getIndex()) + (((TileEntityLargeTurbine) tile).getFacing().getIndex() * 100) /*+ ((TileEntityLargeTurbine) tile).getClientProgress() > 0 ? 1000 : 0*/;
            }
        }
        return exState.withProperty(CT, ct);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTextures(Set<ResourceLocation> textures) {
        super.getTextures(textures);
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE));
        textures.addAll(Arrays.asList(Textures.LARGE_TURBINE_ACTIVE));
    }
}
