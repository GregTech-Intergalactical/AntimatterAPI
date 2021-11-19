package muramasa.antimatter.mixin;

import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkRenderCache.class)
public interface ChunkReaderAccessor {

    @Accessor
    World getLevel();
}
