package muramasa.antimatter.tool.behaviour;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.client.event.DrawHighlightEvent;


public class BehaviourConnection extends BehaviourExtendedHighlight {

    public BehaviourConnection(Function<Block, Boolean> validator) {
        super(validator);
    }

    public ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev) {
        return super.onDrawHighlight(player, ev);
    }

    @Override
    public String getId() {
        return "connection";
    }
}
