package muramasa.antimatter.tools.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tools.base.MaterialTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.eventbus.api.Event;

public class BehaviourBlockTilling implements IItemUse<MaterialTool> {

    public static final Object2ObjectOpenHashMap<BlockState, BlockState> TILLING_MAP = new Object2ObjectOpenHashMap<>();

    static {
        ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND, Blocks.GRASS_PATH, Blocks.FARMLAND, Blocks.DIRT, Blocks.FARMLAND, Blocks.COARSE_DIRT, Blocks.DIRT)
                .forEach(BehaviourBlockTilling::addStrippedBlock);
    }

    @Override
    public String getId() {
        return "block_tilling";
    }

    @Override
    public ActionResultType onItemUse(MaterialTool instance, ItemUseContext c) {
        World world = c.getWorld();
        BlockPos pos = c.getPos();
        if (c.getFace() != Direction.DOWN && world.isAirBlock(pos.up())) {
            BlockState blockstate = TILLING_MAP.get(world.getBlockState(pos));
            if (blockstate == null) return ActionResultType.PASS;
            UseHoeEvent event = new UseHoeEvent(c);
            MinecraftForge.EVENT_BUS.post(event);
            PlayerEntity player = c.getPlayer();
            if (event.getResult() != Event.Result.ALLOW) return ActionResultType.PASS;
            c.getItem().damageItem(instance.getType().getUseDurability(), player, (p) -> p.sendBreakAnimation(c.getHand()));
            world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockState(pos, blockstate, 11);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public static void addStrippedBlock(Block from, Block to) {
        addStrippedState(from.getDefaultState(), to.getDefaultState());
    }

    public static void addStrippedState(BlockState from, BlockState to) {
        TILLING_MAP.put(from, to);
    }
}
