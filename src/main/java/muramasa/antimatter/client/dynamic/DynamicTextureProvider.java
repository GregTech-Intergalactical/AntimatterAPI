package muramasa.antimatter.client.dynamic;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class DynamicTextureProvider<T extends IDynamicModelProvider, U> {
    //Data required to build the textures.
    public class ModelData {
        public BlockModel model;
        public Direction dir;
        public IModelData data;
        public T source;
        public U key;

        public ModelData(BlockModel model, Direction dir, IModelData data, T source, U key) {
            this.model = model;
            this.dir = dir;
            this.data = data;
            this.source = source;
            this.key = key;
        }
    }

    //Data required to assemble the model.
    public class BuilderData {
        public Random rand;
        @Nullable
        public BlockState state;
        public IModelData data;
        public IUnbakedModel sourceModel;
        public T source;
        public U key;
        public Direction currentDir;
        public String type;

        public BuilderData(String type, Random r, BlockState s, IModelData d, T t, U u, IUnbakedModel sourceModel, Direction dir) {
            rand = r;
            state = s;
            data = d;
            source = t;
            this.sourceModel = sourceModel;
            this.key = u;
            this.currentDir = dir;
            this.type = type;
        }
    }

    //The weak-reference backed hashmap.
    protected Object2ObjectMap<String, WeakHashMap<U, List<BakedQuad>[]>> MODEL_CACHE = new Object2ObjectOpenHashMap<>();

    private final Consumer<ModelData> texturer;
    private final Function<BuilderData, List<BakedQuad>> builder;

    public DynamicTextureProvider(@Nonnull Function<BuilderData, List<BakedQuad>> builder, @Nonnull Consumer<ModelData> textureBuilder) {
        this.texturer = textureBuilder;
        this.builder = builder;
    }

    public List<BakedQuad>[] getQuads(String type, BlockState state, T t, U key, IModelData data) {
        return MODEL_CACHE.compute(t.getId(), (k, v) -> {
            if (v == null) v = new WeakHashMap<>();
            v.computeIfAbsent(key, (k1) -> bakeQuads(type, state, t, key, data));
            return v;
        }).get(key);
    }

    private List<BakedQuad>[] bakeQuads(String type, BlockState state, T c, U key, IModelData data) {
        List<BakedQuad>[] bakedArray = new List[Ref.DIRS.length];
        for (Direction dir : Ref.DIRS) {
            IUnbakedModel m = ModelLoader.instance().getModel(c.getModel(type, dir, dirFromState(state, dir)));
            if (m instanceof BlockModel) {
                BlockModel bm = (BlockModel) m;
                texturer.accept(new ModelData(bm, dir, data, c, key));
            }
            bakedArray[dir.get3DDataValue()] = builder.apply(new BuilderData(type, Ref.RNG, state, data, c, key, m, dir));
        }
        return bakedArray;
    }

    private static Direction dirFromState(BlockState state, Direction bypass) {
        if (state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return bypass;
    }
}
