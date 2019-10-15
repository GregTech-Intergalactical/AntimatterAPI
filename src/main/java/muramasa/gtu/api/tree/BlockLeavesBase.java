//package muramasa.gtu.api.tree;
//
//import muramasa.gtu.Ref;
//import muramasa.gtu.api.GregTechAPI;
//import muramasa.gtu.api.registration.IModelOverride;
//import net.minecraft.block.BlockLeaves;
//import net.minecraft.block.BlockPlanks;
//import net.minecraft.block.LeavesBlock;
//import net.minecraft.block.state.BlockStateContainer;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.renderer.block.model.ModelResourceLocation;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.init.Items;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.BlockRenderLayer;
//import net.minecraft.util.Direction;
//import net.minecraft.util.Hand;
//import net.minecraft.util.NonNullList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.IBlockReader;
//import net.minecraft.world.World;
//import net.minecraftforge.client.model.ModelLoader;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Random;
//
//public class BlockLeavesBase extends LeavesBlock implements IModelOverride {
//
//    protected String registryName;
//    protected BlockSaplingBase sapling;
//
//    public BlockLeavesBase(String registryName, BlockSaplingBase sapling) {
//        this.registryName = registryName;
//        this.sapling = sapling;
//        setUnlocalizedName(registryName);
//        setRegistryName(registryName);
//        setDefaultState(getDefaultState().withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true));
//        GregTechAPI.register(this);
//    }
//
//    @Override
//    public BlockState getStateForPlacement(World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, Hand hand) {
//        return super.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, meta, placer, hand).withProperty(DECAYABLE, false);
//    }
//
//    protected void dropApple(World worldIn, BlockPos pos, BlockState state, int chance) {}
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        return this.getDefaultState().withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
//    }
//
//    @Override
//    public int getMetaFromState(BlockState state) {
//        int i = 0;
//
//        if (!state.getValue(DECAYABLE)) {
//            i = 4;
//        }
//
//        if (state.getValue(CHECK_DECAY)) {
//            i |= 8;
//        }
//
//        return i;
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
//    }
//
//    @Override
//    public BlockPlanks.EnumType getWoodType(int meta) {
//        return BlockPlanks.EnumType.SPRUCE;
//    }
//
//    @Override
//    public Item getItemDropped(BlockState state, Random rand, int fortune) {
//        return Item.getItemFromBlock(sapling);
//    }
//
//    @Override
//    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
//        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
//            spawnAsEntity(worldIn, pos, new ItemStack(this));
//        } else {
//            super.harvestBlock(worldIn, player, pos, state, te, stack);
//        }
//    }
//
//    @Nonnull
//    @Override
//    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockReader world, BlockPos pos, int fortune) {
//        return NonNullList.withSize(1, new ItemStack(this));
//    }
//
//    @Override
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.CUTOUT_MIPPED;
//    }
//
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public void onModelRegistration() {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + registryName));
//    }
//}
