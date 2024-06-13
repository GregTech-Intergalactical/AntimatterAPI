package muramasa.antimatter.tool.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IBasicAntimatterTool;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BehaviourVanillaShovel implements IItemUse<IBasicAntimatterTool> {

    public static final BehaviourVanillaShovel INSTANCE = new BehaviourVanillaShovel();

    //Adding this to allow new path blocks to be dynamically added via integration. Vanilla contains only one block and the campfires.
    private static final Object2ObjectOpenHashMap<Block, Block> FLATTENING_MAP = new Object2ObjectOpenHashMap<>();

    static {
        new ImmutableMap.Builder<Block, Block>().put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH).put(Blocks.CAMPFIRE, Blocks.CAMPFIRE).put(Blocks.SOUL_CAMPFIRE, Blocks.SOUL_CAMPFIRE)
                .build().forEach(BehaviourVanillaShovel::addStrippedBlock);
    }

    @Override
    public String getId() {
        return "vanilla_shovel";
    }

    @Override
    public InteractionResult onItemUse(IBasicAntimatterTool instance, UseOnContext c) {
        if (c.getClickedFace() == Direction.DOWN) return InteractionResult.PASS;
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        BlockState changedState = null;
        // Campfire putout
        if (state.getBlock() instanceof CampfireBlock) {
            if (state.getValue(CampfireBlock.LIT)){
                changedState = getFireModifiedState(state, state.setValue(CampfireBlock.LIT, false), c, "shovel_dig");
                if (changedState != null) {
                    c.getLevel().levelEvent(c.getPlayer(), 1009, c.getClickedPos(), 0);
                }
            }
        } else if (state != null) {
            changedState = getToolModifiedState(state, c, "shovel_flatten");
            if (changedState != null) {
                SoundEvent soundEvent = instance.getAntimatterToolType().getUseSound() == null ? SoundEvents.SHOVEL_FLATTEN : instance.getAntimatterToolType().getUseSound();
                c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        if (changedState != null) {
            c.getLevel().setBlock(c.getClickedPos(), changedState, 11);
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, UseOnContext context, String action) {
        BlockState eventState = AntimatterPlatformUtils.onToolUse(originalState, context, action);
        if (eventState != originalState) return eventState;
        Block flattened = FLATTENING_MAP.get(originalState.getBlock());
        if (flattened == null) return null;
        BlockState state = flattened.defaultBlockState();
        for (Property property : originalState.getProperties()) {
            if (state.hasProperty(property)){
                state = state.setValue(property, originalState.getValue(property));
            }
        }
        return state;
    }

    private BlockState getFireModifiedState(BlockState originalState, BlockState changedState, UseOnContext context, String action) {
        BlockState eventState = AntimatterPlatformUtils.onToolUse(originalState, context, action);
        return eventState != originalState ? eventState : changedState;
    }

    public static void addStrippedBlock(Block from, Block to) { FLATTENING_MAP.put(from, to); }
}
