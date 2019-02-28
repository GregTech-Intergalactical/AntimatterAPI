package muramasa.gregtech.api.cover.impl;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public class CoverPlate extends CoverTintable {

    private int rgb;

    public CoverPlate(int rgb) {
        this.rgb = rgb;
    }

    @Override
    public String getName() {
        return "cover_plate";
    }

    @Override
    public List<BakedQuad> onRender(List<BakedQuad> quads) {
        return quads;
    }

    @Override
    public int getRGB() {
        return rgb;
    }
}
