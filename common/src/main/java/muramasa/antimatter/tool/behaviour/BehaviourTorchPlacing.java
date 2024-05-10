package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.IBasicAntimatterTool;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class BehaviourTorchPlacing implements IItemUse<IBasicAntimatterTool> {
    public static final BehaviourTorchPlacing INSTANCE = new BehaviourTorchPlacing();

    @Override
    public String getId() {
        return "torch_placing";
    }

    @Override
    public InteractionResult onItemUse(IBasicAntimatterTool instance, UseOnContext c) {
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

    public static InteractionResult tryPlace(BlockPlaceContext context, ItemStack torch) {
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockState blockstate = getStateForPlacement(context, torch);
            if (blockstate == null) {
                return InteractionResult.FAIL;
            } else if (!placeBlock(context, blockstate)) {
                return InteractionResult.FAIL;
            } else {
                BlockPos blockpos = context.getClickedPos();
                Level level = context.getLevel();
                Player player = context.getPlayer();
                BlockState blockstate2 = level.getBlockState(blockpos);
                Block block = blockstate2.getBlock();
                if (block == blockstate.getBlock()) {
                    blockstate2 = updateBlockStateFromTag(blockpos, level, torch, blockstate2);
                    BlockItem.updateCustomBlockEntityTag(level, player, blockpos, torch);
                    block.setPlacedBy(level, blockpos, blockstate2, player, torch);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos, torch);
                    }
                }

                //TODO figure out why this used world, blockstate, and player in getSountType
                level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                SoundType soundtype = blockstate2.getSoundType();
                level.playSound(player, blockpos, Blocks.TORCH.getSoundType(blockstate2).getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
    }

    private static BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null) {
            CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateDefinition = blockState.getBlock().getStateDefinition();

            for (String string : compoundTag2.getAllKeys()) {
                Property<?> property = stateDefinition.getProperty(string);
                if (property != null) {
                    String string2 = compoundTag2.get(string).getAsString();
                    blockState = updateState(blockState, property, string2);
                }
            }
        }

        if (blockState != state) {
            level.setBlock(pos, blockState, 2);
        }

        return blockState;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map((p_219986_2_) -> {
            return state.setValue(property, p_219986_2_);
        }).orElse(state);
    }

    protected static boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 11);
    }

    @Nullable
    protected static BlockState getStateForPlacement(BlockPlaceContext context, ItemStack torch) {
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
