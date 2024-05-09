package muramasa.antimatter.worldgen.vein;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VeinSavedData extends SavedData {
    public final HashMap<ChunkPos, Map<Material, LongList>> ores = new HashMap<>();
    private final ServerLevel serverLevel;
    public VeinSavedData(ServerLevel serverLevel){
        this.serverLevel = serverLevel;
    }

    public VeinSavedData(ServerLevel serverLevel, CompoundTag nbt) {
        this(serverLevel);
        var list = nbt.getList("veinInfo", Tag.TAG_COMPOUND);
        for (Tag tag : list) {
            if (tag instanceof CompoundTag compoundTag) {
                var chunkPos = new ChunkPos(compoundTag.getLong("p"));
                ListTag listTag = compoundTag.getList("d", Tag.TAG_COMPOUND);
                Map<Material, LongList> materialMap = new Object2ObjectOpenHashMap<>();
                listTag.forEach(t ->{
                    if (t instanceof CompoundTag compoundTag1){
                        Material mat = Material.get(compoundTag1.getString("material"));
                        ListTag positions = compoundTag1.getList("positions", Tag.TAG_LONG);
                        LongList longList = new LongArrayList();
                        positions.forEach(l -> longList.add(((LongTag)l).getAsLong()));
                        materialMap.put(mat, longList);
                    }
                });
                ores.put(chunkPos, materialMap);
            }
        }
    }

    public static VeinSavedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(tag -> new VeinSavedData(serverLevel, tag), () -> new VeinSavedData(serverLevel), "antimatter_ore_veins");
    }
    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        var oilList = new ListTag();
        for (var entry : ores.entrySet()) {
            var tag = new CompoundTag();
            tag.putLong("p", entry.getKey().toLong());
            ListTag listTag = new ListTag();
            entry.getValue().forEach((m, l) -> {
                ListTag positions = new ListTag();
                l.forEach(p -> positions.add(LongTag.valueOf(p)));
                CompoundTag data = new CompoundTag();
                data.putString("material", m.getId());
                data.put("positions", positions);
                listTag.add(data);
            });
            tag.put("d", listTag);
            oilList.add(tag);
        }
        compoundTag.put("veinInfo", oilList);
        return compoundTag;
    }

    public Map<Material, LongList> geOresInChunk(int chunkX, int chunkZ){
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        if (!ores.containsKey(chunkPos)){
            ores.put(chunkPos, new Object2ObjectOpenHashMap<>());
            setDirty();
        }
        return ores.get(chunkPos);
    }

    public Map<Material, LongList> getOresInChunkAtY(int chunkX, int chunkZ, int y){
        Map<Material, LongList> map = geOresInChunk(chunkX, chunkZ);
        Map<Material, LongList> map2 = new Object2ObjectOpenHashMap<>();
        map.forEach((k, v) -> {
            v.forEach(l -> {
                BlockPos pos = BlockPos.of(l);
                if (pos.getY() == y){
                    map2.computeIfAbsent(k, k1 -> new LongArrayList()).add(l);
                }
            });
        });
        return map2;
    }

    public boolean addOreToChunk(int chunkX, int chunkZ, Material material, BlockPos pos){
        var map = geOresInChunk(chunkX, chunkZ);
        if (!map.containsKey(material) || !map.get(material).contains(pos.asLong())){
            map.computeIfAbsent(material, l -> new LongArrayList()).add(pos.asLong());
            setDirty();
            return true;
        }
        return false;
    }
}
