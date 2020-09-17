package muramasa.antimatter.client.baked;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoveredBakedModel extends DynamicBakedModel {

    protected static Object2ObjectMap<Cover, List<BakedQuad>[]> MODEL_CACHE = new Object2ObjectOpenHashMap<>();

    public CoveredBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) return super.getModelData(world, pos, state, data);
        CoverInstance<?>[] covers = tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).map(h -> h.getAll()).orElse(CoverInstance.EMPTY_COVER_ARRAY);
        if (covers.length > 0) data.setData(AntimatterProperties.MACHINE_COVER, covers);
        return super.getModelData(world, pos, state, data);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        if (MODEL_CACHE.isEmpty()) buildCoverCache(state, rand, data);
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, data);
        CoverInstance<?>[] covers = data.getData(AntimatterProperties.MACHINE_COVER);
        if (covers == null) return quads;
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            quads.addAll(getCoverQuads(state, covers[i], i));
        }
        return quads;
    }

    public List<BakedQuad> getCoverQuads(BlockState state, CoverInstance<?> instance, int dir) {
        return MODEL_CACHE.get(instance.getCover())[dir];
    }

    public static void buildCoverCache(BlockState state, Random rand, IModelData data) {
        List<BakedQuad> coverQuads;
        List<BakedQuad>[] bakedArray;
        for (Cover c : AntimatterAPI.all(Cover.class)) {
            coverQuads = ModelUtils.getBaked(c.getModel()).getQuads(state, Direction.NORTH, rand, data);
            bakedArray = new ArrayList[Ref.DIRS.length];
            for (Direction dir : Ref.DIRS) {
                bakedArray[dir.getIndex()] = ModelUtils.trans(coverQuads, dir.toVector3f(), new Vector3f(0, 0, 0));
            }
            MODEL_CACHE.put(c, bakedArray);
        }
    }
}
