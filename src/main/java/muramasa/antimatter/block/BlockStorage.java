package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockStorage extends BlockMaterialType implements IItemBlockProvider, IColorHandler {

    private static final AxisAlignedBB FRAME_COLLISION = new AxisAlignedBB(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);//new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    
    public BlockStorage(String domain, Material material, MaterialType<?> type) {
        super(domain, material, type, Block.Properties.create(net.minecraft.block.material.Material.IRON).hardnessAndResistance(8.0f).sound(SoundType.METAL));
        AntimatterAPI.register(BlockStorage.class, this);
    }

    /** Frame Placing Stuffs - Start **/
//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, EntityPlayer player, EnumHand hand, Direction facing, float hitX, float hitY, float hitZ) {
//        if (facing == Direction.UP) return false;
//        ItemStack stack = player.getHeldItem(hand);
//        if (stack.isEmpty()) return false;
//        Item item = stack.getItem();
//        if (!(item instanceof GTItemBlock)) return false;
//        GTItemBlock itemBlock = ((GTItemBlock) item);
//        Block block = itemBlock.getBlock();
//        if (isFrame(block)) {
//            BlockStorage frame = ((BlockStorage) block);
//            BlockPos playerPos = player.getPosition();
//            if (playerPos.equals(pos)) return false;
//            MutableBlockPos mutablePos = new MutableBlockPos(pos);
//            for (int i = pos.getY(); i < 256; i++) {
//                mutablePos.move(Direction.UP);
//                if (playerPos.equals(mutablePos) || player.isOnLadder()) return false;
//                else if (world.mayPlace(frame, mutablePos, false, Direction.DOWN, player) && frame.canPlaceBlockAt(world, mutablePos)) {
//                    world.setBlockState(mutablePos, frame.getDefaultState().withProperty(frame.getMaterialProp(), stack.getMetadata()));
//                    if (!player.isCreative()) stack.shrink(1);
//                    SoundType soundType = getSoundType();
//                    world.playSound(player, mutablePos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
//                    return true;
//                }
//                else if (isFrame(world.getBlockState(mutablePos).getBlock())) continue;
//                //else continue; //Uncomment this if we want frames to be place-able even if there are obstacles in the y-axis
//                else break;
//            }
//        }
//        return false;
//    }
    
    private static boolean isFrame(Block block) {
        return block instanceof BlockStorage && ((BlockStorage) block).type == MaterialType.FRAME;
    }
    /** Frame Placing Stuffs - End **/

    /** Ladder Stuffs - Start **/
//    @Override
//    public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entityIn) {
//        if (!(entityIn instanceof EntityLivingBase)) return;
//        if (type == MaterialType.BLOCK) return;
//        EntityLivingBase entity = (EntityLivingBase) entityIn;
//        entity.motionX = MathHelper.clamp(entity.motionX, -0.15, 0.15);
//        entity.motionZ = MathHelper.clamp(entity.motionZ, -0.15, 0.15);
//        entity.fallDistance = 0.0F;
//        if (entity.isSneaking() && entity instanceof EntityPlayer) {
//            if (entity.isInWater()) entity.motionY = 0.02D;
//            else entity.motionY = 0.08D;
//        } else if (entity.collidedHorizontally)entity.motionY = 0.22D + (double)(getMaterialFromState(state).getToolSpeed() / 75);
//        else entity.motionY = Math.max(entity.motionY, -0.2D);
//    }
//    
//    @Override
//    public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
//        return type == MaterialType.FRAME ? FRAME_COLLISION : super.getCollisionBoundingBox(state, world, pos);
//    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    /** Ladder Stuffs - End **/
    
//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
//        drops.clear();
//        drops.add(new ItemStack(this, 1, state.getValue(STORAGE_MATERIAL)));
//    }



    @Override
    public int getHarvestLevel(BlockState state) {
        return material.getToolQuality();
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        return type == MaterialType.FRAME;
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return type == MaterialType.FRAME ? PushReaction.DESTROY : PushReaction.NORMAL;
    }

    @Override
    public boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon) {
        return type == MaterialType.BLOCK;
    }

    //    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return type == MaterialType.BLOCK;
//    }
//
//    @Override
//    public boolean isFullBlock(BlockState state) {
//        return type == MaterialType.BLOCK;
//    }
}
