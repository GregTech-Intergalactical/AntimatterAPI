package muramasa.antimatter.client.fabric;


import muramasa.antimatter.client.ClientPlatformHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientPlatformHelperImpl implements ClientPlatformHelper {

    @Override
    public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider){
        BlockEntityRendererRegistry.register(type, renderProvider);
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> renderProvider){
        EntityRendererRegistry.register(type, renderProvider);
    }
}
