package muramasa.antimatter.proxy.forge;

import muramasa.antimatter.proxy.ClientHandler;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientHandlerImpl {
    public ClientHandlerImpl() {
        //eventBus.addListener(ClientHandlerImpl::onModelRegistry);
        new ClientHandler();

    }

    public static<T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider){
        BlockEntityRenderers.register(type, renderProvider);
    }

    public static<T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> renderProvider){
        EntityRenderers.register(type, renderProvider);
    }
}
