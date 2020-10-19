package muramasa.antimatter.client.baked;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class CoveredBakedModel extends DynamicBakedModel {

    protected static Object2ObjectMap<ResourceLocation, Map<Texture,List<BakedQuad>[]>> MODEL_CACHE = new Object2ObjectOpenHashMap<>();

    public CoveredBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) return super.getModelData(world, pos, state, data);
        tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY).ifPresent(t -> data.setData(AntimatterProperties.MACHINE_COVER,t.getCoverFunction()));//map(h -> h.getAll()).orElse(CoverInstance.EMPTY_COVER_ARRAY);
        //if (covers.length > 0) data.setData(AntimatterProperties.MACHINE_COVER, covers);
        return super.getModelData(world, pos, state, data);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        //if (MODEL_CACHE.isEmpty()) buildCoverCache(state, rand, data);
        List<BakedQuad> quads = super.getBlockQuads(state, side, rand, data);
        Function<Direction, CoverInstance> covers = data.getData(AntimatterProperties.MACHINE_COVER);
        Texture tex = data.getData(AntimatterProperties.MACHINE_TEXTURE);
        if (covers == null) return quads;
        for (int i = 0; i < Ref.DIRS.length; i++) {
            CoverInstance<?> c = covers.apply(Ref.DIRS[i]);
            if (c.isEmpty()) continue;
            quads.addAll(getCoverQuads(state, c, i, tex, data));
        }
        return quads;
    }

    public List<BakedQuad> getCoverQuads(BlockState state, CoverInstance<?> instance, int dir, Texture baseTex, IModelData data) {
        return MODEL_CACHE.compute(instance.getCover().getModel(), (k,v) -> {
            if (v == null) v = new Object2ObjectOpenHashMap<>();
            v.compute(baseTex, (k1,v1) -> {
                if (v1 == null) {
                    v1 = bakeForSingleCover(state, Ref.RNG, instance.getCover(), k1, data);
                }
                return v1;
            });
            return v;
        }).get(baseTex)[dir];
    }

    private List<BakedQuad>[] bakeForSingleCover(BlockState state, Random rand,Cover c, Texture baseTexture, IModelData data) {
        List<BakedQuad> coverQuads;
        List<BakedQuad>[] bakedArray;
        IUnbakedModel m = ModelLoader.instance().getUnbakedModel(c.getModel());
        if (m instanceof BlockModel) {
            BlockModel bm = (BlockModel) m;
            //The base texture.
            bm.textures.put("base", Either.left(ModelUtils.getBlockMaterial(baseTexture)));
            c.setTextures((name,texture) -> bm.textures.put(name, Either.left(ModelUtils.getBlockMaterial(texture))));
        }
        bakedArray = new List[Ref.DIRS.length];
        for (Direction dir : Ref.DIRS) {
            IBakedModel b = m.bakeModel(ModelLoader.instance(), ModelLoader.defaultTextureGetter(), Utils.getModelRotation(dir),c.getModel());/*new SimpleModelTransform(new TransformationMatrix(null, TransformationHelper.quatFromXYZ(dir.toVector3f(), true), null, TransformationHelper.quatFromXYZ(dir.toVector3f(), true)))/Ä,c);*/
            coverQuads = b.getQuads(state,null,rand,data);
            bakedArray[dir.getIndex()] = coverQuads;
        }
        return bakedArray;
    }



    /*public static void buildCoverCache(BlockState state, Random rand, IModelData data) {
        List<BakedQuad> coverQuads;
        List<BakedQuad>[] bakedArray;
        for (Cover c : AntimatterAPI.all(Cover.class)) {
            for (Tier tier : Tier.getStandard()) {
                Li
                MODEL_CACHE.compute(c, (k, v) -> {
                    if (v == null) v = new Object2ObjectOpenHashMap<>();
                    v.putIfAbsent(tier.getBaseTexture(), finalBakedArray);
                    return v;
                });
            }
        }
    }*/
}
