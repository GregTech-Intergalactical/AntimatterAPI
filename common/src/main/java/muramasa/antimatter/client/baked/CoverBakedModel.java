package muramasa.antimatter.client.baked;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class CoverBakedModel extends GroupedBakedModel {
    public CoverBakedModel(TextureAtlasSprite p, Map<String, BakedModel> models) {
        super(p, models);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, BlockPos pos) {
        return getBlockQuads(state, side, rand, level, pos, null);
    }

    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, BlockPos pos, Predicate<Map.Entry<String, BakedModel>> predicate) {
        if (predicate == null) return Collections.emptyList();
        List<BakedQuad> quads = new ArrayList<>();
        for (Map.Entry<String, BakedModel> t : this.models.entrySet()) {
            if (predicate.test(t)){
                quads.addAll(t.getValue().getQuads(state, null, rand));
            }
        }
        return quads;
        /*return this.models.entrySet().stream().filter(t -> {

        }).flatMap(t -> t.getValue().getQuads(state, null, rand).stream()).collect(Collectors.toList());*/
    }

    @NotNull
    public static EnumMap<Direction, Byte> addCoverModelData(Direction side, ICoverHandler<?> handler) {;
        EnumMap<Direction, Byte> map = new EnumMap<>(Direction.class);
        if (handler == null) return map;
        byte value = (byte) 0;
        for (Direction dir : new Direction[]{Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN}) {
            Direction rotated = Utils.rotate(side, dir);
            ICover cover = handler.get(rotated);
            if (cover.isEmpty()) {
                byte coverByte = (byte) (1 << dir.get3DDataValue());
                value |= coverByte;
            }
        }
        map.put(side, value);
        return map;
    }
}
