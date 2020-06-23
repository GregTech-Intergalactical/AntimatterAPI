package muramasa.antimatter.behaviour;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.client.event.DrawHighlightEvent;

public interface IItemHighlight <T> extends IBehaviour<T> {

    ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev);
}