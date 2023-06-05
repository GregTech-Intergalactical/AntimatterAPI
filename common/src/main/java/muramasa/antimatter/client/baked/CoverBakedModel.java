package muramasa.antimatter.client.baked;

import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class CoverBakedModel extends GroupedBakedModel {
    public CoverBakedModel(TextureAtlasSprite p, Map<String, BakedModel> models) {
        super(p, models);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull BlockAndTintGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        EnumMap<Direction, Byte> bitmap = data.getData(AntimatterProperties.COVER_REMOVAL);
        if (bitmap == null) return super.getBlockQuads(state, side, rand, data);
        Byte f = bitmap.get(side);
        if (f == null) return Collections.emptyList();
        byte filter = f;
        return this.models.entrySet().stream().filter(t -> {new EnumMap<>(Direction.class);
            String key = t.getKey();
            if (key.isEmpty()) return true;
            Direction dir = Direction.byName(key);
            if (dir == null) throw new NullPointerException("Dir null in getBlockQuads");
            boolean ok = (filter & (1 << dir.get3DDataValue())) > 0;
            return ok;
        }).flatMap(t -> t.getValue().getQuads(state, null, rand).stream()).collect(Collectors.toList());
    }

    @Nonnull
    public static EnumMap<Direction, Byte> addCoverModelData(Direction side, ICoverHandler<?> handler, @Nonnull IModelData tileData) {;
        EnumMap<Direction, Byte> map = new EnumMap<>(Direction.class);
        if (handler == null) return map;
        byte value = (byte) 0;
        for (Direction dir : new Direction[]{Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN}) {
            Direction rotated = Utils.rotate(side, dir);
            ICover cover = handler.get(rotated);
            if (cover.isEmpty()) {
                value |= (1 << dir.get3DDataValue());
            }
        }
        map.put(side, value);
        return map;
    }
}
