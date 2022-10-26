package muramasa.antimatter.client.tesr;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;

public class Caches {
    public static class LiquidCache {
        public final float percentage;
        public final Fluid fluid;
        public final BakedModel model;
        public final float height;
        public final Direction side;

        public LiquidCache(float percentage, Fluid fluid, BakedModel model, float height, Direction side) {
            this.percentage = percentage;
            this.fluid = fluid;
            this.model = model;
            this.height = height;
            this.side = side;
        }
    }
}
