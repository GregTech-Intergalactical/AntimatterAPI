package muramasa.gtu.api.blocks;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.registration.*;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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

public class BlockStorage extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private Material material;

    public BlockStorage(Material material) {
        super(net.minecraft.block.material.Material.IRON);
        this.material = material;
        setUnlocalizedName("block_" + getId());
        setRegistryName("block_" + getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechAPI.register(BlockStorage.class, this);
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String getId() {
        return material.getId();
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
    public String getDisplayName(ItemStack stack) {
        return Prefix.Block.getDisplayName(material);
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int i) {
        return i == 0 ? getMaterial().getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? ((BlockStorage) block).getMaterial().getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        String set = material.getSet().getId();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":material_set_block/" + set, set + "=" + Prefix.Block.getId()));
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ModelResourceLocation(Ref.MODID + ":material_set_block/" + set, set + "=" + Prefix.Block.getId())));
    }
}
