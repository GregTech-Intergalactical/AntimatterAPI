package muramasa.antimatter.behaviour;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.DrawSelectionEvent.HighlightBlock;

public interface IItemHighlight<T> extends IBehaviour<T> {

    InteractionResult onDrawHighlight(Player player, HighlightBlock ev);
}