package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.DrawSelectionEvent;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BehaviourExtendedHighlight implements IItemHighlight<IAntimatterTool> {

    final float INDENTATION_SIDE = 0.25F;
    final double INTERACT_DISTANCE = 5;

    protected Function<Block, Boolean> validator;
    protected BiFunction<Direction, BlockEntity, Boolean> function;

    public final static BiFunction<Direction, BlockEntity, Boolean> COVER_FUNCTION = (dir, tile) -> {
        if (tile instanceof TileEntityBase) {
            TileEntityBase<?> machine = (TileEntityBase) tile;
            return machine.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, dir).map(t -> !t.get(dir).isEmpty()).orElse(false);
        }
        return false;
    };

    public final static BiFunction<Direction, BlockEntity, Boolean> PIPE_FUNCTION = (dir, tile) -> {
        if (tile instanceof TileEntityPipe) {
            return ((TileEntityPipe) tile).canConnect(dir.get3DDataValue());
        }
        if (tile instanceof TileEntityMachine) {
            Direction direction = ((TileEntityMachine) tile).getOutputFacing();
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCrouching())
                direction = ((TileEntityMachine) tile).getFacing();
            return direction != null && direction == dir;
        }
        return false;
    };


    public BehaviourExtendedHighlight(Function<Block, Boolean> validator, BiFunction<Direction, BlockEntity, Boolean> builder) {
        this.validator = validator;
        this.function = builder;
    }

    @Override
    public String getId() {
        return "extended_highlight";
    }

    @Override
    public InteractionResult onDrawHighlight(Player player, DrawSelectionEvent.HighlightBlock ev) {
        return RenderHelper.onDrawHighlight(player, ev, validator, function);
    }
}
