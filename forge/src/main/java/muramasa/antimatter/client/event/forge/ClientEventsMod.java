package muramasa.antimatter.client.event.forge;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.client.model.loader.IAntimatterModelLoader;
import muramasa.antimatter.client.model.loader.forge.ModelLoaderWrapper;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.registration.RegistrationEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ref.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsMod {
    @SubscribeEvent
    public static void onTextureStitch(final TextureStitchEvent.Pre event) {
        AntimatterTextureStitcher.onTextureStitch(event.getAtlas(), event::addSprite);
    }

    @SubscribeEvent
    public static void onBlockColorHandler(RegisterColorHandlersEvent.Block e) {
        ClientHandler.onBlockColorHandler(e.getBlockColors());
    }

    @SubscribeEvent
    public static void onItemColorHandler(RegisterColorHandlersEvent.Item e) {
        ClientHandler.onItemColorHandler(e.getItemColors());
    }

    @SubscribeEvent
    public static void preResourceRegistration(ModelEvent.RegisterGeometryLoaders ev) {
        AntimatterAPI.onRegistration(RegistrationEvent.CLIENT_DATA_INIT);
        AntimatterAPI.all(IAntimatterModelLoader.class).forEach(l -> ev.register(l.getLoc().toString(), new ModelLoaderWrapper(l)));
    }
}
