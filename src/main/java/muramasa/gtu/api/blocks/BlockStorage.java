package muramasa.gtu.api.blocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.math.BlockPos.MutableBlockPos;
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

    private static Object2ObjectOpenHashMap<String, Tuple<BlockStorage, Integer>> ID_LOOKUP = new Object2ObjectOpenHashMap<>();

    private static final AxisAlignedBB FRAME_COLLISION = new AxisAlignedBB(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);//new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    private String id;
    private MaterialType type;
    private Material[] materials;
    private PropertyInteger STORAGE_MATERIAL;
    
    public BlockStorage(String id, MaterialType type, Material... materials) {
        super(net.minecraft.block.material.Material.IRON);
        if (GregTechAPI.has(BlockStorage.class, id)) throw new IllegalArgumentException("a storage block with the id " + id + " already exists");
        this.id = id;
        this.type = type;
        this.materials = materials;
        for (int i = 0; i < materials.length; i++) {
            ID_LOOKUP.put(type.getId() + "_" + materials[i].getId(), new Tuple<>(this, i));
        }
        STORAGE_MATERIAL = PropertyInteger.create("storage_material", 0, Math.max(materials.length - 1, 1));

        //Hack to dynamically create a BlockState with a correctly sized material property based on the passed materials array
        BlockStateContainer blockStateContainer = createBlockState();
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, blockStateContainer, 21);
        setDefaultState(blockStateContainer.getBaseState());

        setSoundType(type == MaterialType.FRAME ? SoundType.LADDER : SoundType.METAL);
        setResistance(8.0f);
        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_BLOCKS);
        GregTechAPI.register(BlockStorage.class, this);
    }

    @Override
    public String getId() {
        return "storage_" + type.getId() + "_" + id;
    }

    public MaterialType getType() {
        return type;
    }

    public Material[] getMaterials() {
        return materials;
    }

    public PropertyInteger getMaterialProp() {
        return STORAGE_MATERIAL;
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
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == EnumFacing.UP) return false;
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (!(item instanceof GTItemBlock)) return false;
        GTItemBlock itemBlock = ((GTItemBlock) item);
        if (isFrame(itemBlock.getBlock())) {
            BlockPos playerPos = player.getPosition();
            if (playerPos.equals(pos)) return false;
            MutableBlockPos mutablePos = new MutableBlockPos(pos);
            for (int i = pos.getY(); i < 256; i++) {
                mutablePos.move(EnumFacing.UP);
                if (playerPos.equals(mutablePos) || player.isOnLadder()) return false;
                else if (world.mayPlace(this, mutablePos, false, EnumFacing.DOWN, player) && canPlaceBlockAt(world, mutablePos)) {
                    //TODO: Fix setBlockState
                    //world.setBlockState(mutablePos, getDefaultState().withProperty(STORAGE_MATERIAL, stack.getMetadata()));
                    if (!player.isCreative()) stack.shrink(1);
                    SoundType soundType = getSoundType();
                    world.playSound(player, mutablePos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                    return true;
                }
                else continue;
            }
        }
        return false;
    }
    
    private static boolean isFrame(Block block) {
        return block instanceof BlockStorage && ((BlockStorage) block).type == MaterialType.FRAME;
    }
    /** Frame Placing Stuffs - End **/

    /** Ladder Stuffs - Start **/
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
        if (!(entityIn instanceof EntityLivingBase)) return;
        if (type == MaterialType.BLOCK) return;
        EntityLivingBase entity = (EntityLivingBase) entityIn;
        entity.motionX = MathHelper.clamp(entity.motionX, -0.15, 0.15);
        entity.motionZ = MathHelper.clamp(entity.motionZ, -0.15, 0.15);
        entity.fallDistance = 0.0F;
        if (entity.isSneaking() && entity instanceof EntityPlayer) {
            if (entity.isInWater()) entity.motionY = 0.02D;
            else entity.motionY = 0.08D;
        } else if (entity.collidedHorizontally)entity.motionY = 0.22D + (double)(getMaterialFromState(state).getToolSpeed() / 75);
        else entity.motionY = Math.max(entity.motionY, -0.2D);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (type == MaterialType.FRAME) return FRAME_COLLISION;
        else return super.getCollisionBoundingBox(state, world, pos);
    }
    /** Ladder Stuffs - End **/
    
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
        drops.add(new ItemStack(this, 1, state.getValue(STORAGE_MATERIAL)));
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
        return type == MaterialType.FRAME;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return type == MaterialType.FRAME ? EnumPushReaction.DESTROY : EnumPushReaction.NORMAL;
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

    public static Material getMaterialFromState(IBlockState state) {
        if (!(state.getBlock() instanceof BlockStorage)) return Materials.NULL;
        return ((BlockStorage) state.getBlock()).getMaterials()[state.getValue(((BlockStorage) state.getBlock()).getMaterialProp())];
    }

    public static ItemStack get(Material material, MaterialType type, int count) {
        Tuple<BlockStorage, Integer> tuple = ID_LOOKUP.get(type.getId() + "_" + material.getId());
        if (tuple == null) {
            Utils.onInvalidData("BlockStorage.get() returned null");
            return ItemStack.EMPTY;
        }
        return new ItemStack(tuple.getFirst(), count, tuple.getSecond());
    }
}
