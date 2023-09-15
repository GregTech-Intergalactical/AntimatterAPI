package muramasa.antimatter.fabric;

import net.minecraft.world.level.storage.loot.LootPool;

public interface LootTableExtension {
    default void addPool(LootPool pool){}
}
