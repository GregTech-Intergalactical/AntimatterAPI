package muramasa.antimatter.worldgen;

import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class StoneLayerOre {

    private Material material;
    private StoneType stoneType;
    private BlockState oreState, oreSmallState;
    private int chance, minY, maxY;

    public StoneLayerOre(Material material, int chance, int minY, int maxY) {
        this.material = material;
        this.chance = bind(1, Ref.U, chance);
        this.minY = minY;
        this.maxY = maxY;
    }

    public StoneLayerOre setStatesByStoneType(StoneType stoneType) {
        this.oreState = MaterialType.ORE.get().get(material, stoneType).asState();
        this.oreSmallState = MaterialType.ORE_SMALL.get().get(material, stoneType).asState();
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
}
