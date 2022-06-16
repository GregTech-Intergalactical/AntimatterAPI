package muramasa.antimatter.client.event.forge;

import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.proxy.ClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
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
    public static void onBlockColorHandler(ColorHandlerEvent.Block e) {
        ClientHandler.onBlockColorHandler(e.getBlockColors());
    }

    @SubscribeEvent
    public static void onItemColorHandler(ColorHandlerEvent.Item e) {
        ClientHandler.onItemColorHandler(e.getItemColors());
    }

    @SubscribeEvent
    public static void preResourceRegistration(ParticleFactoryRegisterEvent ev) {
        ClientHandler.preResourceRegistration();
    }
}
