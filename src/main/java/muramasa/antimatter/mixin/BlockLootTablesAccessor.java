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

    @Accessor("NO_SILK_TOUCH")
    public static ILootCondition.IBuilder getNoSilkTouch() {
        throw new AssertionError();
    }

    @Accessor("SILK_TOUCH")
    public static ILootCondition.IBuilder getSilkTouch() {
        throw new AssertionError();
    }

    @Accessor("SHEARS")
    public static ILootCondition.IBuilder getShears() {
        throw new AssertionError();
    }

    @Accessor("NOT_SILK_TOUCH_OR_SHEARS")
    public static ILootCondition.IBuilder getNotSilkTouchOrShears() {
        throw new AssertionError();
    }
}
