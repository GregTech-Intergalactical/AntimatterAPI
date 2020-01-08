package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.baked.BakedDynamic;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;

public interface IDynamicModelBaker {

    BakedDynamic get(Int2ObjectOpenHashMap<IBakedModel> baked, IBakedModel defaultModel, ResourceLocation particle);
}
