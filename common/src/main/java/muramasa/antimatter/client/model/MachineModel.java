package muramasa.antimatter.client.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MachineModel implements IAntimatterModel{

    final Map<MachineState, UnbakedModel[]> models;
    final ResourceLocation particle;
    public MachineModel(Map<MachineState, UnbakedModel[]> models, ResourceLocation particle) {
        this.models = models;
        this.particle = particle;
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return models.values().stream().flatMap(t -> Arrays.stream(t).flatMap(i -> i.getMaterials(modelGetter, missingTextureErrors).stream())).collect(Collectors.toSet());
    }

    @Override
    public BakedModel bakeModel(ModelBakery bakery,
            Function<Material, TextureAtlasSprite> getter, ModelState transform,
            ResourceLocation loc) {
                ImmutableMap.Builder<MachineState, BakedModel[]> builder = ImmutableMap.builder();

                for (Map.Entry<MachineState, UnbakedModel[]> pair : this.models.entrySet()) {
                    BakedModel[] mod = new BakedModel[6];
                    for (int i = 0; i < 6; i++) {
                        mod[i] = pair.getValue()[i].bake(bakery, getter, transform, loc);
                    }
                    builder.put(pair.getKey(),mod);
                }
                return new MachineBakedModel(getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, particle)), builder.build());
            }
    }

