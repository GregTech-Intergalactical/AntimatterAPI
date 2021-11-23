package muramasa.antimatter.client.tesr;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.fluid.Fluid;

public class Caches {
    public static class LiquidCache {
        public final float percentage;
        public final Fluid fluid;
        public final IBakedModel model;

        public LiquidCache(float percentage, Fluid fluid, IBakedModel model) {
            this.percentage = percentage;
            this.fluid = fluid;
            this.model = model;
        }
    }
}
