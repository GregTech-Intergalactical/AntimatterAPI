package muramasa.antimatter.mixin;

import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.conditions.ILootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockLootTables.class)
public interface BlockLootTablesAccessor {
    @Accessor("SILK_TOUCH_OR_SHEARS")
    public static ILootCondition.IBuilder getSilkTouchOrShears() {
        throw new AssertionError();
    }
}
