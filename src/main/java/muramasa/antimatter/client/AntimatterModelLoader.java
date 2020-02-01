package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.client.model.LoaderModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.IModelLoader;

import java.util.HashMap;
import java.util.function.Function;

public class AntimatterModelLoader implements IModelLoader<LoaderModel> {

    private static HashMap<String, Tuple<Block, Function<ModelBuilder, AntimatterModel>>> LOOKUP = new HashMap<>();

    protected static String getBlockId(Block block) {
        return block.getRegistryName().getNamespace() + ":block/" + block.getRegistryName().getPath();
    }

    public static void put(Block block, Function<ModelBuilder, AntimatterModel> builder) {
        LOOKUP.put(getBlockId(block), new Tuple<>(block, builder));
    }

    public static Tuple<Block, Function<ModelBuilder, AntimatterModel>> get(String id) {
        return LOOKUP.get(id);
    }

    public static Tuple<Block, Function<ModelBuilder, AntimatterModel>> get(Block block) {
        return get(getBlockId(block));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

@Override
public LoaderModel read(JsonDeserializationContext context, JsonObject json) {
    BlockModel model = context.deserialize(json.get("base"), BlockModel.class);
    return new LoaderModel(model);
}
}
