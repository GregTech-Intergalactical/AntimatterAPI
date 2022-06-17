package muramasa.antimatter.proxy.forge;

import muramasa.antimatter.client.AntimatterModelLoader;
import muramasa.antimatter.client.event.forge.ClientEventsMod;
import muramasa.antimatter.proxy.ClientHandler;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientHandlerImpl {
    public ClientHandlerImpl() {
        //eventBus.addListener(ClientHandlerImpl::onModelRegistry);
        new ClientHandler();

    }

    public static void registerLoader(ResourceLocation location, AntimatterModelLoader<?> loader){
        ModelLoaderRegistry.registerLoader(location, loader);
    }
    public static void setup(FMLClientSetupEvent e){
        ClientHandler.setup();
    }

    public static<T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider){
        BlockEntityRenderers.register(type, renderProvider);
    }
}