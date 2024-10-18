package muramasa.antimatter.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.util.ImplLoader;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ClientPlatformHelper {
    ClientPlatformHelper INSTANCE = ImplLoader.load(ClientPlatformHelper.class);

    <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider);

    <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> renderProvider);
}
