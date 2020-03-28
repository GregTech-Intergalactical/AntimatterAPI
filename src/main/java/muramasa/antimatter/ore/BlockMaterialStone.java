package muramasa.antimatter.ore;

import muramasa.antimatter.block.BlockBasic;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public abstract class BlockMaterialStone extends BlockBasic implements IAntimatterObject, IItemBlockProvider, IColorHandler, IModelProvider {

    protected Material material;
    protected StoneType stoneType;

    public BlockMaterialStone(String domain, String id, Material material, StoneType stoneType, Block.Properties properties) {
        super(domain, id, properties);
        this.material = material;
        this.stoneType = stoneType;
    }

    public Material getMaterial() {
        return material;
    }

    public StoneType getStoneType() {
        return stoneType;
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (world == null || pos == null || i != 1 || state.isAir(world, pos)) return -1;
        return ((BlockMaterialStone) world.getBlockState(pos).getBlock()).getMaterial().getRGB();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 1 && block != null ? ((BlockMaterialStone) block).getMaterial().getRGB() : -1;
    }
}
