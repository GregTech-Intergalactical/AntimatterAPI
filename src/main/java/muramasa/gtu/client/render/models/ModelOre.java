package muramasa.gtu.client.render.models;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedOre;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class ModelOre implements IModel {

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel base = ModelUtils.load("basic");
        BakedOre.STONES = new IBakedModel[StoneType.getLastInternalId()];
        for (int i = 0; i < StoneType.getLastInternalId(); i++) {
            BakedOre.STONES[i] = ModelUtils.tex(base, "0", StoneType.get(i).getTexture()).bake(state, format, bakedTextureGetter);
        }

        IModel overlay = ModelUtils.load("overlay");
        BakedOre.OVERLAYS = new Int2ObjectArrayMap<>();
        MaterialType.ORE_TYPES.values().forEach(o -> {
            IBakedModel[] textureSets = new IBakedModel[TextureSet.getLastInternalId()];
            GregTechAPI.all(TextureSet.class).forEach(t -> {
                textureSets[t.getInternalId()] = ModelUtils.tex(overlay, "0", t.getTexture(o, 0)).bake(state, format, bakedTextureGetter);
            });
            BakedOre.OVERLAYS.put(o.getInternalId(), textureSets);
        });

        return new BakedOre();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> locations = new ArrayList<>();
        StoneType.getAll().forEach(s -> locations.add(s.getTexture()));
        MaterialType.ORE_TYPES.values().forEach(o -> GregTechAPI.all(TextureSet.class).forEach(t -> locations.add(t.getTexture(o, 0))));
        return locations;
    }
}
