package muramasa.gregtech.client.render.models;

import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.client.render.bakedmodels.BakedModelBasic;
import muramasa.gregtech.client.render.overrides.ItemOverrideBasic;
import muramasa.gregtech.common.blocks.BlockBaked;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class ModelBasic extends ModelBase {

    protected BlockBaked bakedBlock;
    protected ItemOverrideBasic itemOverride;

    public ModelBasic(String name, BlockBaked bakedBlock) {
        super(name);
        this.bakedBlock = bakedBlock;
    }

    public ModelBasic(String name, BlockBaked bakedBlock, ItemOverrideBasic itemOverride) {
        this(name, bakedBlock);
        this.itemOverride = itemOverride;
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        return new BakedModelBasic(load(bakedBlock.getModel()).bake(state, format, getter), itemOverride);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> locations = new ArrayList<>();
        for (Texture texture : bakedBlock.getTextures()) {
            locations.add(texture.getLoc());
        }
        return locations;
    }
}
