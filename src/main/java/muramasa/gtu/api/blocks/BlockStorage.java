package muramasa.gtu.api.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockStorage extends Block implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    private static Int2ObjectOpenHashMap<Tuple<Integer, Integer>> INDEX_LOOKUP = new Int2ObjectOpenHashMap<>();

    private static final AxisAlignedBB FRAME_COLLISION = new AxisAlignedBB(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);

    private int index;
    private MaterialType type;
    private Material[] materials;
    private PropertyInteger STORAGE_MATERIAL;

    public BlockStorage(int index, MaterialType type, Material... materials) {
        super(net.minecraft.block.material.Material.IRON);
        this.index = index;
        this.type = type;
        this.materials = materials;
        for (int i = 0; i < materials.length; i++) {
            INDEX_LOOKUP.put(materials[i].getHash(), new Tuple<>(index, i));
        }
        STORAGE_MATERIAL = PropertyInteger.create("storage_material", 0, materials.length - 1);

        //Hack to dynamically create a BlockState with a correctly sized material property based on the passed materials array
        BlockStateContainer blockStateContainer = createBlockState();
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, blockStateContainer, 21);
        setDefaultState(blockStateContainer.getBaseState());
        setResistance(8.0f);
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechAPI.register(BlockStorage.class, this);
    }

    @Override
    public String getId() {
        return "storage_" + type.getId() + "_" + index;
    }

    public MaterialType getType() {
        return type;
    }

    public Material[] getMaterials() {
        return materials;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return STORAGE_MATERIAL != null ? new BlockStateContainer.Builder(this).add(STORAGE_MATERIAL).build() : new BlockStateContainer(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STORAGE_MATERIAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(STORAGE_MATERIAL, meta);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(STORAGE_MATERIAL, placer.getHeldItem(hand).getMetadata());
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, state.getValue(STORAGE_MATERIAL));
    }
    
    /** Frame Placing Stuffs - Start **/
    private boolean isFrame(Block block) {
        return block instanceof BlockStorage && ((BlockStorage) block).type == MaterialType.FRAME;
    }
    /** Frame Placing Stuffs - End **/
    
    /** Ladder Stuffs - Start **/
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!(entityIn instanceof EntityLivingBase)) return;
        if (type == MaterialType.BLOCK) return;
        EntityLivingBase entity = (EntityLivingBase) entityIn;
        entity.motionX = MathHelper.clamp(entity.motionX, -0.15, 0.15);
        entity.motionZ = MathHelper.clamp(entity.motionZ, -0.15, 0.15);
        entity.fallDistance = 0.0F;
        if (entity.isSneaking() && entity instanceof EntityPlayer) {
            if (entity.isInWater()) {
                entity.motionY = 0.02D;
            } else {
                entity.motionY = 0.08D;
            }
        } else if (entity.collidedHorizontally) {
            entity.motionY = 0.2D;
        } else {
            entity.motionY = Math.max(entity.motionY, -0.07D);
        }
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (type == MaterialType.FRAME) return FRAME_COLLISION;
        else return super.getCollisionBoundingBox(state, world, pos);
    }
    /** Ladder Stuffs - End **/
    
    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        if (type == MaterialType.FRAME) return EnumPushReaction.DESTROY;
        else return EnumPushReaction.NORMAL;
    }
    
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
        drops.add(new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(STORAGE_MATERIAL)));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i < materials.length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    public IBlockState get(int i) {
        return getDefaultState().withProperty(STORAGE_MATERIAL, i);
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
        return type.getDisplayName(materials[Math.min(stack.getMetadata(), materials.length - 1)]);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return type == MaterialType.FRAME ? true : false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return type == MaterialType.BLOCK ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return type == MaterialType.BLOCK;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return type == MaterialType.BLOCK;
    }

    @Override
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        return i == 0 ? materials[state.getValue(STORAGE_MATERIAL)].getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 ? materials[stack.getMetadata()].getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        for (int i = 0; i < materials.length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(Ref.MODID + ":" + getId(), "storage_material=" + i));
        }
    }

    public static ItemStack get(Material material, MaterialType type, int count) {
        Block block = GregTechAPI.get(BlockStorage.class, "storage_" + type.getId() + "_" + INDEX_LOOKUP.get(material.getHash()).getFirst());
        if (block == null) return ItemStack.EMPTY;
        return new ItemStack(block, count, INDEX_LOOKUP.get(material.getHash()).getSecond());
    }
}
