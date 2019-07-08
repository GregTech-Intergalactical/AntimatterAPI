package muramasa.gtu.client.render.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.materials.TextureSet;
import muramasa.gtu.api.ore.OreType;
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
        BakedOre.STONES = new Object2ObjectOpenHashMap<>();
        StoneType.getAllActive().forEach(s -> {
            BakedOre.STONES.put(s.getId(), ModelUtils.tex(base, "0", s.getTexture()).bake(state, format, bakedTextureGetter));
        });

        IModel overlay = ModelUtils.load("overlay");
        BakedOre.OVERLAYS = new IBakedModel[OreType.VALUES.size()][TextureSet.getLastInternalId()];
        OreType.VALUES.forEach(o -> {
            IBakedModel[] textureSets = new IBakedModel[TextureSet.getLastInternalId()];
            GregTechAPI.all(TextureSet.class).forEach(s -> {
                textureSets[s.getInternalId()] = ModelUtils.tex(overlay, "0", s.getTexture(o.getType(), 0)).bake(state, format, bakedTextureGetter);
            });
            BakedOre.OVERLAYS[o.ordinal()] = textureSets;
        });

        return new BakedOre();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> locations = new ArrayList<>();
        StoneType.getAllActive().forEach(s -> locations.add(s.getTexture()));
        OreType.VALUES.forEach(o -> GregTechAPI.all(TextureSet.class).forEach(t -> locations.add(t.getTexture(o.getType(), 0))));
        return locations;
    }
}
