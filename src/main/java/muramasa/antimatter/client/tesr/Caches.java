package muramasa.antimatter.client.tesr;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;

public class Caches {
    public static class LiquidCache {
        public final float percentage;
        public final Fluid fluid;
        public final IBakedModel model;
        public final float height;
        public final Direction side;

        public LiquidCache(float percentage, Fluid fluid, IBakedModel model, float height, Direction side) {
            this.percentage = percentage;
            this.fluid = fluid;
            this.model = model;
            this.height = height;
            this.side = side;
        }
    }
}
