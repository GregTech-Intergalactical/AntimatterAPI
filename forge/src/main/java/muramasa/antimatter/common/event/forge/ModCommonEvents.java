package muramasa.antimatter.common.event.forge;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Ref.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent e) {
        AntimatterConfig.onModConfigEvent(e.getConfig());
    }
}
