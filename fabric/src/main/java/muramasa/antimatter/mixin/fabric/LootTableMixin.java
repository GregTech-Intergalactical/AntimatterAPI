package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.fabric.LootTableExtension;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtension {
    @Shadow @Final @Mutable
    public LootPool[] pools;

    @Override
    public void addPool(LootPool pool) {
        List<LootPool> pools = new ArrayList<>(Arrays.asList(this.pools));
        pools.add(pool);
        this.pools = pools.toArray(new LootPool[0]);
    }
}
