package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BehaviourTorchPlacing implements IItemUse<IAntimatterTool> {
    public static final BehaviourTorchPlacing INSTANCE = new BehaviourTorchPlacing();

    @Override
    public String getId() {
        return "torch_placing";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        ItemStack stack = ItemStack.EMPTY;
        if (c.getPlayer() == null) return ActionResultType.PASS;
        for (ItemStack stack1 : c.getPlayer().inventory.items) {
            if (stack1.getItem() == Items.TORCH || stack1.getItem() == Items.SOUL_TORCH) {
                stack = stack1;
                break;
            }
        }
        if (!stack.isEmpty() || c.getPlayer().isCreative()) {
            ActionResultType resultType = tryPlace(new BlockItemUseContext(c), stack);
            if (resultType.consumesAction()) {
                if (!c.getPlayer().isCreative()) stack.shrink(1);
                return resultType;
            }
        }
        return ActionResultType.PASS;
    }

    public ActionResultType tryPlace(BlockItemUseContext context, ItemStack torch) {
        if (!context.canPlace()) {
            return ActionResultType.FAIL;
        } else {
            BlockState blockstate = this.getStateForPlacement(context, torch);
            if (blockstate == null) {
                return ActionResultType.FAIL;
            } else if (!this.placeBlock(context, blockstate)) {
                return ActionResultType.FAIL;
            } else {
                BlockPos blockpos = context.getClickedPos();
                World world = context.getLevel();
                PlayerEntity playerentity = context.getPlayer();
                ItemStack itemstack = context.getItemInHand();
                BlockState blockstate1 = world.getBlockState(blockpos);
                Block block = blockstate1.getBlock();
                if (block == blockstate.getBlock()) {
                    blockstate1 = this.updateBlockStateFromTag(blockpos, world, itemstack, blockstate1);
                    this.onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1);
                    block.setPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
                    if (playerentity instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerentity, blockpos, itemstack);
                    }
                }

                SoundType soundtype = blockstate1.getSoundType(world, blockpos, context.getPlayer());
                world.playSound(playerentity, blockpos, Blocks.TORCH.getSoundType(blockstate1, world, blockpos, context.getPlayer()).getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                return ActionResultType.sidedSuccess(world.isClientSide);
            }
        }
    }

    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        return BlockItem.updateCustomBlockEntityTag(worldIn, player, pos, stack);
    }

    private BlockState updateBlockStateFromTag(BlockPos p_219985_1_, World p_219985_2_, ItemStack p_219985_3_, BlockState p_219985_4_) {
        BlockState blockstate = p_219985_4_;
        CompoundNBT compoundnbt = p_219985_3_.getTag();
        if (compoundnbt != null) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
            StateContainer<Block, BlockState> statecontainer = p_219985_4_.getBlock().getStateDefinition();

            for (String s : compoundnbt1.getAllKeys()) {
                Property<?> property = statecontainer.getProperty(s);
                if (property != null) {
                    String s1 = compoundnbt1.get(s).getAsString();
                    blockstate = updateState(blockstate, property, s1);
                }
            }
        }

        if (blockstate != p_219985_4_) {
            p_219985_2_.setBlock(p_219985_1_, blockstate, 2);
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState p_219988_0_, Property<T> p_219988_1_, String p_219988_2_) {
        return p_219988_1_.getValue(p_219988_2_).map((p_219986_2_) -> {
            return p_219988_0_.setValue(p_219988_1_, p_219986_2_);
        }).orElse(p_219988_0_);
    }

    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    @Nullable
    protected BlockState getStateForPlacement(BlockItemUseContext context, ItemStack torch) {
        BlockState blockstate = torch.getItem() == Items.SOUL_TORCH ? Blocks.SOUL_WALL_TORCH.getStateForPlacement(context) : Blocks.WALL_TORCH.getStateForPlacement(context);
        BlockState blockstate1 = null;
        IWorldReader iworldreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction != Direction.UP) {
                BlockState blockstate2 = direction == Direction.DOWN ? (torch.getItem() == Items.SOUL_TORCH ? Blocks.SOUL_TORCH.getStateForPlacement(context) : Blocks.TORCH.getStateForPlacement(context)) : blockstate;
                if (blockstate2 != null && blockstate2.canSurvive(iworldreader, blockpos)) {
                    blockstate1 = blockstate2;
                    break;
                }
            }
        }

        return blockstate1 != null && iworldreader.isUnobstructed(blockstate1, blockpos, ISelectionContext.empty()) ? blockstate1 : null;
    }
}
