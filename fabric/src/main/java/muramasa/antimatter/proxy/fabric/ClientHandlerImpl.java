package muramasa.antimatter.proxy.fabric;


import io.github.fabricators_of_create.porting_lib.model.IModelLoader;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import muramasa.antimatter.client.AntimatterModelLoader;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientHandlerImpl {
    public static void registerLoader(ResourceLocation location, AntimatterModelLoader<?> loader){
        ModelLoaderRegistry.registerLoader(location, (IModelLoader<?>) loader);
    }

    public static<T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> type, BlockEntityRendererProvider<T> renderProvider){
        BlockEntityRendererRegistry.register(type, renderProvider);
    }

    public static<T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> renderProvider){
        EntityRendererRegistry.register(type, renderProvider);
    }
}
