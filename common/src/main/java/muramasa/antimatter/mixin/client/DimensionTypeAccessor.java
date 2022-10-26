package muramasa.antimatter.mixin.client;

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor
    static DimensionType getDEFAULT_OVERWORLD() {
        throw new AssertionError();
    }
}
