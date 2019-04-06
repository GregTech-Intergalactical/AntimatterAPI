package muramasa.gtu.common.events;

import muramasa.gtu.Ref;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.EnumSet;

public class OreGenHandler {

    private static EnumSet<OreGenEvent.GenerateMinable.EventType> PREVENTED_TYPES = EnumSet.of(
        OreGenEvent.GenerateMinable.EventType.COAL, OreGenEvent.GenerateMinable.EventType.IRON, OreGenEvent.GenerateMinable.EventType.GOLD,
        OreGenEvent.GenerateMinable.EventType.DIAMOND, OreGenEvent.GenerateMinable.EventType.REDSTONE, OreGenEvent.GenerateMinable.EventType.LAPIS,
        OreGenEvent.GenerateMinable.EventType.QUARTZ
    );

    public OreGenHandler() {
        if (Ref.DISABLE_VANILLA_STONE_GENERATION) {
            PREVENTED_TYPES.add(OreGenEvent.GenerateMinable.EventType.ANDESITE);
            PREVENTED_TYPES.add(OreGenEvent.GenerateMinable.EventType.DIORITE);
            PREVENTED_TYPES.add(OreGenEvent.GenerateMinable.EventType.GRANITE);
        }
    }

    @SubscribeEvent
    public void onOreGenMineable(OreGenEvent.GenerateMinable e) {
        if (e.getGenerator() instanceof WorldGenMinable && PREVENTED_TYPES.contains(e.getType())) {
            e.setResult(Event.Result.DENY);
        }
    }
}
