package muramasa.antimatter.mixin;

import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockLoot.class)
public interface BlockLootTablesAccessor {
    @Accessor(value = "HAS_SHEARS_OR_SILK_TOUCH")
    public static LootItemCondition.Builder getSilkTouchOrShears() {
        throw new AssertionError();
    }

    @Accessor(value = "HAS_NO_SILK_TOUCH")
    public static LootItemCondition.Builder getNoSilkTouch() {
        throw new AssertionError();
    }

    @Accessor(value = "HAS_SILK_TOUCH")
    public static LootItemCondition.Builder getSilkTouch() {
        throw new AssertionError();
    }

    @Accessor(value = "HAS_SHEARS")
    public static LootItemCondition.Builder getShears() {
        throw new AssertionError();
    }

    @Accessor(value = "HAS_NO_SHEARS_OR_SILK_TOUCH")
    public static LootItemCondition.Builder getNotSilkTouchOrShears() {
        throw new AssertionError();
    }
}
