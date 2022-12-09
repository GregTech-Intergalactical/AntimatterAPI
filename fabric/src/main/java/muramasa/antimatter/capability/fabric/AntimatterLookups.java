package muramasa.antimatter.capability.fabric;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class AntimatterLookups {
    public static BlockApiLookup<ICoverHandler, Direction> COVER_HANDLER_SIDED = BlockApiLookup.get(new ResourceLocation(Ref.ID, "covers"), ICoverHandler.class, Direction.class);
}
