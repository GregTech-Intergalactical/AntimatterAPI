package muramasa.antimatter.client.baked;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MachineBakedModel extends RotatableCoveredBakedModel {

    protected static class Key {
        ResourceLocation model;
        Texture tex;
        Direction dir;
        MachineState state;

        public Key(ResourceLocation model, Texture tex, Direction dir, MachineState state) {
            this.model = model;
            this.tex = tex;
            this.dir = dir;
            this.state = state;
        }

        @Override
        public int hashCode() {
            return tex.hashCode() + dir.hashCode() + state.hashCode() + model.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key key = (Key) o;
                return key.state == state && key.dir == dir && tex.equals(key.tex) && model.equals(key.model);
            }
            return false;
        }
    }

    protected static Object2ObjectMap<Key, WeakReference<Map<Direction, List<BakedQuad>>>> MODEL_CACHE = new Object2ObjectOpenHashMap<>();

    public MachineBakedModel(Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> bakedTuple) {
        super(bakedTuple);
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        BlockMachine bm = (BlockMachine)state.getBlock();
        List<BakedQuad> retValue = new ArrayList<>();
        if (data.hasProperty(AntimatterProperties.MACHINE_TEXTURE)) {
            Texture tex = data.getData(AntimatterProperties.MULTI_MACHINE_TEXTURE);
            if (tex != null) {
                Function<Direction, CoverInstance> covers = data.getData(AntimatterProperties.MACHINE_COVER);
                Map<Direction, List<BakedQuad>> quads = MODEL_CACHE.compute(new Key(new ResourceLocation(bm.getType().getId()), tex,state.get(BlockStateProperties.HORIZONTAL_FACING), data.getData(AntimatterProperties.MACHINE_STATE)), (k, v) -> {
                    Map<Direction, List<BakedQuad>> baked = bakeForSingleMachine(state,Ref.RNG, bm.getType(), tex, data, state.get(BlockStateProperties.HORIZONTAL_FACING), data.getData(AntimatterProperties.MACHINE_STATE));
                    return new WeakReference<>(baked);
                }).get();
                for (int i = 0; i < Ref.DIRS.length; i++) {
                    CoverInstance<?> c = covers.apply(Ref.DIRS[i]);
                    if (c.skipRender()) {
                        retValue.addAll(quads.get(Ref.DIRS[i]));
                    } else {
                        retValue.addAll(getCoverQuads(state, c, i, tex, data));
                    }
                }
                return retValue;
            }
        }
        return super.getBlockQuads(state, side, rand, data);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        return super.getModelData(world, pos, state, data);
    }

    private EnumMap<Direction, List<BakedQuad>> bakeForSingleMachine(BlockState state, Random rand, Machine<?> c, Texture baseTexture, IModelData data, Direction facing, MachineState mstate) {
        List<BakedQuad> coverQuads;
        EnumMap<Direction, List<BakedQuad>> map = new EnumMap<Direction, List<BakedQuad>>(Direction.class);
        for (Direction dir : Ref.DIRS) {
            BlockModel m = (BlockModel) ModelLoader.instance().getUnbakedModel(c.getOverlayModel(dir));
            //The base texture.
            m.textures.put("base", Either.left(ModelUtils.getBlockMaterial(baseTexture)));
            m.textures.put("overlay",Either.left(ModelUtils.getBlockMaterial(c.getOverlayTextures(mstate)[dir.getIndex()])));
            IBakedModel b = m.bakeModel(ModelLoader.instance(), ModelLoader.defaultTextureGetter(), Utils.getModelRotation(facing), new ResourceLocation(c.getId()));

            coverQuads = b.getQuads(state,null,rand,data);
            if (coverQuads.size() > 0) {
                coverQuads.forEach(t -> map.compute(t.getFace(), (k, v) -> {
                    if (v == null) v = new ArrayList<>();
                    v.add(t);
                    return v;
                }));
            }
        }
        return map;
    }

}
