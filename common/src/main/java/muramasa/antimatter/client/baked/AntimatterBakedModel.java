package muramasa.antimatter.client.baked;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import java.util.Objects;

public abstract class AntimatterBakedModel<T> implements IAntimatterBakedModel {

    protected TextureAtlasSprite particle;
    protected boolean onlyGeneralQuads = false; //If the model only has "general quads", like pipes

    public AntimatterBakedModel(TextureAtlasSprite p) {
        this.particle = Objects.requireNonNull(p, "Missing particle texture in AntimatterBakedModel");
    }


    public void onlyGeneralQuads() {
        this.onlyGeneralQuads = true;
    }

    @Override
    public boolean hasOnlyGeneralQuads() {
        return onlyGeneralQuads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(BlockAndTintGetter level, BlockPos pos) {
        return getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }
}
