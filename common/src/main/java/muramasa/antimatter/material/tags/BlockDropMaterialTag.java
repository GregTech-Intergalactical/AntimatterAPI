package muramasa.antimatter.material.tags;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Map;
import java.util.function.Function;

public class BlockDropMaterialTag<T extends Block> extends MaterialTag {
    private final Map<Material, Function<T, LootTable.Builder>> mapping = new Object2ObjectArrayMap<>();

    public BlockDropMaterialTag(String id) {
        super(id);
    }

    public BlockDropMaterialTag<T> add(Material mat, Function<T, LootTable.Builder> map) {
        if (!mat.enabled) return this;
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, Function<T, LootTable.Builder>> getAll() {
        return mapping;
    }

    public Function<T, LootTable.Builder> getBuilderFunction(Material mat){
        return mapping.get(mat);
    }
}
