package muramasa.antimatter.client.baked;

import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CoverBakedModel extends GroupedBakedModel {
    public CoverBakedModel(TextureAtlasSprite p, Map<String, IBakedModel> models) {
        super(p, models);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        EnumMap<Direction, Byte> bitmap = data.getData(AntimatterProperties.COVER_REMOVAL);
        if (bitmap == null) return super.getBlockQuads(state, side, rand, data);
        Byte f = bitmap.get(side);
        if (f == null) return Collections.emptyList();
        byte filter = f;
        return this.models.entrySet().stream().filter(t -> {
            String key = t.getKey();
            if (key.isEmpty()) return true;
            Direction dir = Direction.byName(key);
            if (dir == null) throw new NullPointerException("Dir null in getBlockQuads");
            boolean ok = (filter & (1 << dir.get3DDataValue())) > 0;
            return ok;
        }).flatMap(t -> t.getValue().getQuads(state, null, rand).stream()).collect(Collectors.toList());
    }

    @Nonnull
    public static IModelData addCoverModelData(Direction side, ICoverHandler<?> handler, @Nonnull IModelData tileData) {
        if (handler == null) return tileData;
        EnumMap<Direction, Byte> map = tileData.getData(AntimatterProperties.COVER_REMOVAL);
        if (map == null) {
            map = new EnumMap<>(Direction.class);
        }
        byte value = (byte) 0;
        for (Direction dir : new Direction[]{Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN}) {
            Direction rotated = Utils.rotate(side, dir);
            ICover cover = handler.get(rotated);
            if (cover.isEmpty()) {
                value |= (1 << dir.get3DDataValue());
            }
        }
        map.put(side, value);

        tileData.setData(AntimatterProperties.COVER_REMOVAL, map);
        return tileData;
    }
}
