package muramasa.antimatter.worldgen.vein;

import muramasa.antimatter.material.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VeinSavedData extends SavedData {
    public final HashMap<ChunkPos, List<Material>> ores = new HashMap<>();
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
                ListTag listTag = compoundTag.getList("d", Tag.TAG_STRING);
                List<Material> materials = listTag.stream().map(tag1 -> Material.get(tag1.getAsString())).toList();
                ores.put(chunkPos, materials);
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
            entry.getValue().forEach(m -> listTag.add(StringTag.valueOf(m.getId())));
            tag.put("d", listTag);
            oilList.add(tag);
        }
        compoundTag.put("oilSpout", oilList);
        return compoundTag;
    }

    public List<Material> geOresInChunk(int chunkX, int chunkZ){
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        if (!ores.containsKey(chunkPos)){
            ores.put(chunkPos, new ArrayList<>());
            setDirty();
        }
        return ores.get(chunkPos);
    }

    public boolean addOreToChunk(int chunkX, int chunkZ, Material material){
        var list = geOresInChunk(chunkX, chunkZ);
        if (!list.contains(material)){
            list.add(material);
            setDirty();
            return true;
        }
        return false;
    }
}
