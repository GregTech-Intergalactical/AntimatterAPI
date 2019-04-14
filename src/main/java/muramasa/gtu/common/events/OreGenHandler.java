package muramasa.gtu.common.events;

import muramasa.gtu.Ref;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.EnumSet;

import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.*;

public class OreGenHandler {

    private static EnumSet<OreGenEvent.GenerateMinable.EventType> PREVENTED_TYPES = EnumSet.of(
        COAL, IRON, GOLD, DIAMOND, REDSTONE, LAPIS, QUARTZ
    );

    public OreGenHandler() {
        if (Ref.DISABLE_VANILLA_STONE_GENERATION) PREVENTED_TYPES.addAll(Arrays.asList(ANDESITE, DIORITE, GRANITE));
    }

    @SubscribeEvent
    public void onOreGenMineable(OreGenEvent.GenerateMinable e) {
        e.setResult(e.getGenerator() instanceof WorldGenMinable && PREVENTED_TYPES.contains(e.getType()) ? Event.Result.DENY : e.getResult());
    }
}
