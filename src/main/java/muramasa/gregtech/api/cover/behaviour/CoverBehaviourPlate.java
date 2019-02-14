package muramasa.gregtech.api.cover.behaviour;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

public class CoverBehaviourPlate extends CoverBehaviourTintable {

    private int rgb;

    public CoverBehaviourPlate(int rgb) {
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
