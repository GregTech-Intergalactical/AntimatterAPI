package muramasa.gtu.common.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.registration.IHasItemBlock;
import muramasa.gtu.api.registration.IHasModelOverride;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStorage extends Block implements IHasItemBlock, IHasModelOverride {

    private Material material;

    public BlockStorage(Material material) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName("block_" + material.getName());
        setRegistryName("block_" + material.getName());
        setCreativeTab(Ref.TAB_BLOCKS);
        this.material = material;
    }

    public Material getMaterial() {
        return material;
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

    @Override
    public String getItemStackDisplayName(Block block, ItemStack stack) {
        return Prefix.Block.getDisplayName(((BlockStorage) block).getMaterial());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        String set = material.getSet().getName();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":material_set_block/" + set, set + "=" + Prefix.Block.getName()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":material_set_block/" + set, set + "=" + Prefix.Block.getName())));
    }
}
