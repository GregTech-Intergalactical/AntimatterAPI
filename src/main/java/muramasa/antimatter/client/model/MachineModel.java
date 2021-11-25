package muramasa.antimatter.client.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.BakedMachineSide;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

public class MachineModel implements IAntimatterModel<MachineModel>{

    final Map<MachineState, IUnbakedModel[]> models;
    final ResourceLocation particle;
    public MachineModel(Map<MachineState, IUnbakedModel[]> models, ResourceLocation particle) {
        this.models = models;
        this.particle = particle;
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner,
            Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return models.values().stream().flatMap(t -> Arrays.stream(t).flatMap(i -> i.getMaterials(modelGetter, missingTextureErrors).stream())).collect(Collectors.toSet());
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery,
            Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides,
            ResourceLocation loc) {
                ImmutableMap.Builder<MachineState, BakedMachineSide[]> builder = ImmutableMap.builder();

                for (Map.Entry<MachineState, IUnbakedModel[]> pair : this.models.entrySet()) {
                    BakedMachineSide[] mod = new BakedMachineSide[6];
                    for (int i = 0; i < 6; i++) {
                        mod[i] = (BakedMachineSide) pair.getValue()[i].bake(bakery, getter, transform, loc);
                    }
                    builder.put(pair.getKey(),mod);
                }
                return new MachineBakedModel(getter.apply(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, particle)),( ImmutableMap<MachineState, BakedMachineSide[]>) builder.build());
            }
    }

