package muramasa.antimatter.dynamic;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.IAntimatterModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel implements IAntimatterModel<DynamicModel> {

    protected Int2ObjectOpenHashMap<IModelGeometry<?>[]> modelConfigs;
    protected String staticMapId;
    protected ResourceLocation particle;

    public DynamicModel(ResourceLocation particle, Int2ObjectOpenHashMap<IModelGeometry<?>[]> modelConfigs, String staticMapId) {
        this.modelConfigs = modelConfigs;
        this.staticMapId = staticMapId;
        this.particle = particle;
    }

    public DynamicModel(DynamicModel copy) {
        this.modelConfigs = copy.modelConfigs;
        this.staticMapId = copy.staticMapId;
        this.particle = copy.particle;
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return new DynamicBakedModel(getter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, particle)), getBakedConfigs(owner, bakery, getter, transform, overrides, loc));
    }

    public Int2ObjectOpenHashMap<IBakedModel[]> getBakedConfigs(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        Int2ObjectOpenHashMap<IBakedModel[]> bakedConfigs = AntimatterModelManager.getStaticConfigMap(staticMapId);
        modelConfigs.forEach((k, v) -> {
            IBakedModel[] baked = new IBakedModel[v.length];
            for (int i = 0; i < baked.length; i++) {
                baked[i] = v[i].bake(owner, bakery, getter, transform, overrides, loc);
            }
            bakedConfigs.put((int) k, baked);
        });
        return bakedConfigs;
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<RenderMaterial> textures = new ObjectOpenHashSet<>();
        modelConfigs.values().forEach(v -> Arrays.stream(v).forEach(m -> textures.addAll(m.getTextures(owner, getter, errors))));
        return textures;
    }
}
