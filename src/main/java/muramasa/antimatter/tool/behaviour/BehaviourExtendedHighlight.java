package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemHighlight;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.client.event.DrawHighlightEvent;

import java.util.function.Function;

public class BehaviourExtendedHighlight implements IItemHighlight<IAntimatterTool> {

    final float INDENTATION_SIDE = 0.25F;
    final double INTERACT_DISTANCE = 5;

    protected Function<Block, Boolean> validator;

    public BehaviourExtendedHighlight(Function<Block, Boolean> validator) {
        this.validator = validator;
    }

    @Override
    public String getId() {
        return "extended_highlight";
    }

    @Override
    public ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        return RenderHelper.onDrawHighlight(player, ev, validator);
    }
}
