package muramasa.antimatter.mixin;

import net.minecraft.world.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor
    static DimensionType getDEFAULT_OVERWORLD() {
        throw new AssertionError();
    }
}
