package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.vein.old.WorldGenVeinVariantMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class StoneLayerOre {

    private final Material material;
    private StoneType stoneType;
    private BlockState oreState, oreSmallState;
    private final int chance;
    private final int minY;
    private final int maxY;

    public StoneLayerOre(Material material, int chance, int minY, int maxY) {
        this.material = material;
        this.chance = bind(1, Ref.U, chance);
        this.minY = minY;
        this.maxY = maxY;
    }

    public StoneLayerOre setStatesByStoneType(StoneType stoneType) {
        this.oreState = AntimatterMaterialTypes.ORE.get().get(material, stoneType).asState();
        this.oreSmallState = AntimatterMaterialTypes.ORE_SMALL.get().get(material, stoneType).asState();
        this.stoneType = stoneType;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public StoneType getStoneType() {
        return stoneType;
    }

    public BlockState getOreState() {
        return oreState;
    }

    public BlockState getOreSmallState() {
        return oreSmallState;
    }

    public int getChance() {
        return chance;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public boolean canPlace(BlockPos pos, Random rand) {
        return pos.getY() >= minY && pos.getY() <= maxY && rand.nextInt(Ref.U) < chance;
    }

    public static int bind(int min, int max, int boundValue) {
        return min > max ? Math.max(max, Math.min(min, boundValue)) : Math.max(min, Math.min(max, boundValue));
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("chance", chance);
        json.addProperty("material", material.getId());
        if (minY > Integer.MIN_VALUE) {
            json.addProperty("minY", minY);
        }
        if (maxY < Integer.MAX_VALUE) {
            json.addProperty("maxY", maxY);
        }
        if (stoneType != null){
            json.addProperty("stoneType", stoneType.getId());
        }
        return json;
    }

    public static StoneLayerOre fromJson(JsonObject json){
        StoneType stoneType = null;
        if (json.has("stoneType")){
            stoneType = StoneType.get(json.get("stoneType").getAsString());
            if (stoneType == null) throw new IllegalStateException("stone type: " + json.get("stoneType").getAsString() + " does not exist!");
        }
        StoneLayerOre  stoneLayerOre = new StoneLayerOre(
                Material.get(json.get("material").getAsString()),
                json.get("chance").getAsInt(),
                json.has("minY") ? json.get("minY").getAsInt() : Integer.MIN_VALUE,
                json.has("maxY") ? json.get("maxY").getAsInt() : Integer.MAX_VALUE);
        if (stoneType != null){
            stoneLayerOre.setStatesByStoneType(stoneType);
        }
        return stoneLayerOre;
    }
}
