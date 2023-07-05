package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BehaviourTreeFelling implements IBlockDestroyed<IAntimatterTool> {

    public static final BehaviourTreeFelling INSTANCE = new BehaviourTreeFelling();
    public static final Tree NO_TREE = new Tree(Collections.emptyList());

    @Override
    public String getId() {
        return "tree_felling";
    }

    @Override
    public boolean onBlockDestroyed(IAntimatterTool instance, ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!AntimatterConfig.GAMEPLAY.AXE_TIMBER) return true;
        if (entity instanceof Player && !world.isClientSide) {
            Player player = (Player) entity;
            if (Utils.isToolEffective(instance, stack, state) && !player.isCrouching()) { // Only when player isn't shifting/crouching this ability activates
                if (state.is(BlockTags.LOGS)) {
                    Utils.treeLogging(instance, stack, pos, player, world);
                }
            }
        }
        return true;
    }

    /**
     * Finds a tree at the given pos. Block at the position should be air
     *
     * @param reader
     * @param pos
     * @return null if not found or not fully cut
     */
    @Nonnull
    public static Tree findTree(@Nullable BlockGetter reader, BlockPos pos) {
        if (reader == null)
            return NO_TREE;

        List<BlockPos> logs = new ArrayList<>();
        List<BlockPos> leaves = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> frontier = new LinkedList<>();

        // Bamboo, Sugar Cane, Cactus
        BlockState stateAbove = reader.getBlockState(pos.above());
        if (isVerticalPlant(stateAbove)) {
            logs.add(pos.above());
            for (int i = 1; i < 256; i++) {
                BlockPos current = pos.above(i);
                if (!isVerticalPlant(reader.getBlockState(current)))
                    break;
                logs.add(current);
            }
            Collections.reverse(logs);
            return new Tree(logs);
        }

        // Chorus
        if (isChorus(stateAbove)) {
            frontier.add(pos.above());
            while (!frontier.isEmpty()) {
                BlockPos current = frontier.remove(0);
                visited.add(current);
                logs.add(current);
                for (Direction direction : Direction.values()) {
                    BlockPos offset = current.relative(direction);
                    if (visited.contains(offset))
                        continue;
                    if (!isChorus(reader.getBlockState(offset)))
                        continue;
                    frontier.add(offset);
                }
            }
            Collections.reverse(logs);
            return new Tree(logs);
        }

        // Regular Tree
        if (!validateCut(reader, pos))
            return NO_TREE;

        visited.add(pos);
        BlockPos.betweenClosedStream(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))
                .forEach(p -> frontier.add(new BlockPos(p)));

        // Find all logs
        while (!frontier.isEmpty()) {
            BlockPos currentPos = frontier.remove(0);
            if (visited.contains(currentPos))
                continue;
            visited.add(currentPos);

            if (!isLog(reader.getBlockState(currentPos)))
                continue;
            logs.add(currentPos);
            forNeighbours(currentPos, visited, true, p -> frontier.add(new BlockPos(p)));
        }

        // Find all leaves
        visited.clear();
        visited.addAll(logs);
        frontier.addAll(logs);

        while (!frontier.isEmpty()) {
            BlockPos prevPos = frontier.remove(0);
            if (!logs.contains(prevPos) && visited.contains(prevPos))
                continue;

            visited.add(prevPos);
            BlockState prevState = reader.getBlockState(prevPos);
            int prevLeafDistance = isLeaf(prevState) ? getLeafDistance(prevState) : 0;

            forNeighbours(prevPos, visited, false, currentPos -> {
                BlockState state = reader.getBlockState(currentPos);
                BlockPos subtract = currentPos.subtract(pos);
                BlockPos currentPosImmutable = currentPos.immutable();

                int horizontalDistance = Math.max(Math.abs(subtract.getX()), Math.abs(subtract.getZ()));
                if (horizontalDistance <= nonDecayingLeafDistance(state)) {
                    leaves.add(currentPosImmutable);
                    frontier.add(currentPosImmutable);
                    return;
                }

                if (isLeaf(state) && getLeafDistance(state) > prevLeafDistance) {
                    leaves.add(currentPosImmutable);
                    frontier.add(currentPosImmutable);
                }

            });
        }

        return new Tree(logs);
    }

    private static int getLeafDistance(BlockState state) {
        IntegerProperty distanceProperty = LeavesBlock.DISTANCE;
        for (Property<?> property : state.getValues()
                .keySet())
            if (property instanceof IntegerProperty ip && property.getName()
                    .equals("distance"))
                distanceProperty = ip;
        return state.getValue(distanceProperty);
    }

    public static boolean isChorus(BlockState stateAbove) {
        return stateAbove.getBlock() instanceof ChorusPlantBlock || stateAbove.getBlock() instanceof ChorusFlowerBlock;
    }

    public static boolean isVerticalPlant(BlockState stateAbove) {
        Block block = stateAbove.getBlock();
        if (block instanceof BambooBlock)
            return true;
        if (block instanceof CactusBlock)
            return true;
        if (block instanceof SugarCaneBlock)
            return true;
        if (block instanceof KelpPlantBlock)
            return true;
        return block instanceof KelpBlock;
    }

    /**
     * Checks whether a tree was fully cut by seeing whether the layer above the cut
     * is not supported by any more logs.
     *
     * @param reader
     * @param pos
     * @return
     */
    private static boolean validateCut(BlockGetter reader, BlockPos pos) {
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> frontier = new LinkedList<>();
        frontier.add(pos);
        frontier.add(pos.above());
        int posY = pos.getY();

        while (!frontier.isEmpty()) {
            BlockPos currentPos = frontier.remove(0);
            visited.add(currentPos);
            boolean lowerLayer = currentPos.getY() == posY;

            if (!isLog(reader.getBlockState(currentPos)))
                continue;
            if (!lowerLayer && !pos.equals(currentPos.below()) && isLog(reader.getBlockState(currentPos.below())))
                return false;

            for (Direction direction : Direction.values()) {
                if (direction == Direction.DOWN)
                    continue;
                if (direction == Direction.UP && !lowerLayer)
                    continue;
                BlockPos offset = currentPos.relative(direction);
                if (visited.contains(offset))
                    continue;
                frontier.add(offset);
            }

        }

        return true;
    }

    private static void forNeighbours(BlockPos pos, Set<BlockPos> visited, boolean up, Consumer<BlockPos> acceptor) {
        BlockPos.betweenClosedStream(pos.offset(-1, up ? 0 : -1, -1), pos.offset(1, 1, 1))
                .filter(((Predicate<BlockPos>) visited::contains).negate())
                .forEach(acceptor);
    }

    public static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS)
                || state.is(Blocks.MUSHROOM_STEM);
    }

    private static int nonDecayingLeafDistance(BlockState state) {
        if (state.is(Blocks.RED_MUSHROOM_BLOCK))
            return 2;
        if (state.is(Blocks.BROWN_MUSHROOM_BLOCK))
            return 3;
        if (state.is(BlockTags.WART_BLOCKS) || state.is(Blocks.WEEPING_VINES) || state.is(Blocks.WEEPING_VINES_PLANT))
            return 3;
        return -1;
    }

    private static boolean isLeaf(BlockState state) {
        for (Property<?> property : state.getValues()
                .keySet())
            if (property instanceof IntegerProperty && property.getName()
                    .equals("distance"))
                return true;
        return false;
    }

    public static class Tree {
        private final List<BlockPos> logs;

        public Tree(List<BlockPos> logs) {
            this.logs = logs;
        }

        public List<BlockPos> getLogs() {
            return logs;
        }
    }
}
