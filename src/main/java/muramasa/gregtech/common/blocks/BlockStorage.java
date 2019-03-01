package muramasa.gregtech.common.blocks;

import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.client.render.StateMapperRedirect;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;

public class BlockStorage extends Block {

    private static LinkedHashMap<String, BlockStorage> BLOCK_LOOKUP = new LinkedHashMap<>();

    private Material material;

    public BlockStorage(Material material) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName("block_" + material.getName());
        setRegistryName("block_" + material.getName());
        setCreativeTab(Ref.TAB_BLOCKS);
        this.material = material;
        BLOCK_LOOKUP.put(material.getName(), this);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    //TODO
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return 1.0f + (getHarvestLevel(blockState) * 1.0f);
    }

    //TODO
    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        String set = material.getSet().getName();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":material_set_block/" + set, set + "=" + Prefix.Block.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":material_set_block/" + set, set + "=" + Prefix.Block.getName())));
    }

    public Material getMaterial() {
        return material;
    }

    public static BlockStorage get(Material material) {
        return BLOCK_LOOKUP.get(material.getName());
    }

    public static Collection<BlockStorage> getAll() {
        return BLOCK_LOOKUP.values();
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
            if (tintIndex == 0) {
                BlockStorage block = (BlockStorage) state.getBlock();
                return block.getMaterial().getRGB();
            }
            return -1;
        }
    }
}
