package muramasa.antimatter.behaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;

public interface IItemHighlight <T> extends IBehaviour<T> {
    ActionResultType onDrawHighlight(PlayerEntity player, DrawHighlightEvent ev);
}