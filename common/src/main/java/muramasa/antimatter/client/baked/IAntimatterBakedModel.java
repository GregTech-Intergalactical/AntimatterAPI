package muramasa.antimatter.client.baked;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface IAntimatterBakedModel extends BakedModel {
    List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

     default List<BakedQuad> getQuads(BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos){
         try {
             if (hasOnlyGeneralQuads() && side != null) return Collections.emptyList();
             return state != null ? getBlockQuads(state, side, rand, level, pos) : Collections.emptyList(); //todo figure out item quads if necessary
         } catch (Exception e) {
             e.printStackTrace();
             return Collections.emptyList();
         }
     }

    boolean hasOnlyGeneralQuads();
    @Override
    default List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction side, RandomSource rand) {
        return Collections.emptyList();
    }

    TextureAtlasSprite getParticleIcon(BlockAndTintGetter level, BlockPos pos);
}
