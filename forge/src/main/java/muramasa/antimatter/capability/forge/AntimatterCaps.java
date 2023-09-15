package muramasa.antimatter.capability.forge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ref.ID)
public class AntimatterCaps {
    public static final BiMap<Class<?>, Capability<?>> CAP_MAP = HashBiMap.create();
    public static final Capability<ICoverHandler<?>> COVERABLE_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent ev) {
        ev.register(ICoverHandler.class);
    }
}
