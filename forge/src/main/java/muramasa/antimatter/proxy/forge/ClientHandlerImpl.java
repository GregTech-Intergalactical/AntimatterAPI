package muramasa.antimatter.proxy.forge;

import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.AntimatterTextureStitcher;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.proxy.ClientHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientHandlerImpl {
    public ClientHandlerImpl() {
        /* Client event listeners. */
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ClientHandlerImpl::onItemColorHandler);
        eventBus.addListener(ClientHandlerImpl::onBlockColorHandler);
        //eventBus.addListener(ClientHandlerImpl::onModelRegistry);
        eventBus.addListener(AntimatterTextureStitcher::onTextureStitch);
        AntimatterTextureStitcher.addStitcher(event -> AntimatterAPI.all(CoverFactory.class).forEach(cover -> {
            if (cover == ICover.emptyFactory)
                return;
            for (ResourceLocation r : cover.getTextures()) {
                event.accept(r);
            }
        }));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandlerImpl::preResourceRegistration);

    }

    public static void preResourceRegistration(ParticleFactoryRegisterEvent ev) {
        ClientHandler.preResourceRegistration();
    }
    public static void registerLoader(ResourceLocation location, AntimatterModelLoader<?> loader){
        ModelLoaderRegistry.registerLoader(location, loader);
    }
    public static void setup(FMLClientSetupEvent e){
        ClientHandler.setup();
    }

    public static void onItemColorHandler(ColorHandlerEvent.Item e) {
        ClientHandler.onItemColorHandler(e.getItemColors());
    }

    public static void onBlockColorHandler(ColorHandlerEvent.Block e) {
        ClientHandler.onBlockColorHandler(e.getBlockColors());
    }
}
