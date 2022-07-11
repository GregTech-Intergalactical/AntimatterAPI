package muramasa.antimatter.mixin.fabric;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtension {
    @Shadow @Final @Mutable
    public LootPool[] pools;
    @Unique
    private ResourceLocation id;

    @Override
    public void addPool(LootPool pool) {
        List<LootPool> pools = Arrays.asList(this.pools);
        pools.add(pool);
        this.pools = pools.toArray(new LootPool[0]);
    }

    @Override
    public ResourceLocation getLootTableId() {
        return id;
    }

    @Override
    public void setLootTableId(ResourceLocation lootTableId) {
        if (this.id != null) {
            throw new IllegalStateException("Attempted to rename loot table from '" + this.id + "' to '" + lootTableId + "': this is not supported");
        } else {
            this.id = Objects.requireNonNull(lootTableId);
        }
    }
}
