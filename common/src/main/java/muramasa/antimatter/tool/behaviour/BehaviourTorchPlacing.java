package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;

public class BehaviourTorchPlacing implements IItemUse<IAntimatterTool> {
    public static final BehaviourTorchPlacing INSTANCE = new BehaviourTorchPlacing();

    @Override
    public String getId() {
        return "torch_placing";
    }

    @Override
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        ItemStack stack = ItemStack.EMPTY;
        if (c.getPlayer() == null) return InteractionResult.PASS;
        for (ItemStack stack1 : c.getPlayer().getInventory().items) {
            if (stack1.getItem() == Items.TORCH || stack1.getItem() == Items.SOUL_TORCH) {
                stack = stack1;
                break;
            }
        }
        if (!stack.isEmpty() || c.getPlayer().isCreative()) {
            InteractionResult resultType = tryPlace(new BlockPlaceContext(c), stack);
            if (resultType.consumesAction()) {
                if (!c.getPlayer().isCreative()) stack.shrink(1);
                return resultType;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult tryPlace(BlockPlaceContext context, ItemStack torch) {
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockState blockstate = this.getStateForPlacement(context, torch);
            if (blockstate == null) {
                return InteractionResult.FAIL;
            } else if (!this.placeBlock(context, blockstate)) {
                return InteractionResult.FAIL;
            } else {
                BlockPos blockpos = context.getClickedPos();
                Level world = context.getLevel();
                Player playerentity = context.getPlayer();
                ItemStack itemstack = context.getItemInHand();
                BlockState blockstate1 = world.getBlockState(blockpos);
                Block block = blockstate1.getBlock();
                if (block == blockstate.getBlock()) {
                    blockstate1 = this.updateBlockStateFromTag(blockpos, world, itemstack, blockstate1);
                    this.onBlockPlaced(blockpos, world, playerentity, itemstack, blockstate1);
                    block.setPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
                    if (playerentity instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerentity, blockpos, itemstack);
                    }
                }

                //TODO figure out why this used world, blockstate, and player in getSountType
                SoundType soundtype = blockstate1.getSoundType();
                world.playSound(playerentity, blockpos, Blocks.TORCH.getSoundType(blockstate1).getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
    }

    protected boolean onBlockPlaced(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
        return BlockItem.updateCustomBlockEntityTag(worldIn, player, pos, stack);
    }

    private BlockState updateBlockStateFromTag(BlockPos p_219985_1_, Level p_219985_2_, ItemStack p_219985_3_, BlockState p_219985_4_) {
        BlockState blockstate = p_219985_4_;
        CompoundTag compoundnbt = p_219985_3_.getTag();
        if (compoundnbt != null) {
            CompoundTag compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> statecontainer = p_219985_4_.getBlock().getStateDefinition();

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

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    @Nullable
    protected BlockState getStateForPlacement(BlockPlaceContext context, ItemStack torch) {
        BlockState blockstate = torch.getItem() == Items.SOUL_TORCH ? Blocks.SOUL_WALL_TORCH.getStateForPlacement(context) : Blocks.WALL_TORCH.getStateForPlacement(context);
        BlockState blockstate1 = null;
        LevelReader iworldreader = context.getLevel();
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

        return blockstate1 != null && iworldreader.isUnobstructed(blockstate1, blockpos, CollisionContext.empty()) ? blockstate1 : null;
    }
}
