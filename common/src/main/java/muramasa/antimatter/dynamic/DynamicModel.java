package muramasa.antimatter.dynamic;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.model.IGeometryBakingContext;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel implements IAntimatterModel {

    protected Int2ObjectOpenHashMap<IAntimatterModel[]> modelConfigs;
    protected String staticMapId;
    protected ResourceLocation particle;

    public DynamicModel(ResourceLocation particle, Int2ObjectOpenHashMap<IAntimatterModel[]> modelConfigs, String staticMapId) {
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
    public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc) {
        return null;
    }

    @Override
    public BakedModel bakeModel(IGeometryBakingContext configuration, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        return new DynamicBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, particle)), getBakedConfigs(configuration, bakery, getter, transform, overrides, loc));
    }

    public Int2ObjectOpenHashMap<BakedModel[]> getBakedConfigs(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        Int2ObjectOpenHashMap<BakedModel[]> bakedConfigs = AntimatterModelManager.getStaticConfigMap(staticMapId);
        modelConfigs.forEach((k, v) -> {
            BakedModel[] baked = new BakedModel[v.length];
            for (int i = 0; i < baked.length; i++) {
                baked[i] = v[i].bake(owner, bakery, getter, transform, overrides, loc);
            }
            bakedConfigs.put((int) k, baked);
        });
        return bakedConfigs;
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext configuration, Function<ResourceLocation, UnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<Material> textures = new ObjectOpenHashSet<>();
        modelConfigs.values().forEach(v -> Arrays.stream(v).forEach(m -> textures.addAll(m.getMaterials(configuration, getter, errors))));
        return textures;
    }
}
