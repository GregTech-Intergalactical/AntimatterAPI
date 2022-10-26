package muramasa.antimatter.worldgen.vein;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldGenVeinVariant {

    public final int weight;
    public final float oreChance;
    public final float smallOreChance;
    public final float markerOreChance;
    public final float surfaceStoneChance;
    public final List<WorldGenVeinVariantMaterial> materials;

    private WorldGenVeinVariant(int weight, float oreChance, float smallOreChance, float markerOreChance, float surfaceStoneChance, List<WorldGenVeinVariantMaterial> materials) {
        this.weight = weight;
        this.oreChance = oreChance;
        this.smallOreChance = smallOreChance;
        this.markerOreChance = markerOreChance;
        this.surfaceStoneChance = surfaceStoneChance;
        this.materials = materials;
    }

    static List<WorldGenVeinVariant> getFlat(int weight, float oreChance, float smallOreChance, float markerOreChance, float surfaceStoneChance, List<WorldGenVeinVariantMaterial> materials) {
        return IntStream.range(0, weight).mapToObj(i -> {
            List<WorldGenVeinVariantMaterial> flatMaterials = new ArrayList<>();
            for (WorldGenVeinVariantMaterial material : materials) {
                flatMaterials.addAll(WorldGenVeinVariantMaterial.getFlat(material.weight, material.material, material.minY, material.maxY));
            }
            return new WorldGenVeinVariant(
                    1,
                    oreChance,
                    smallOreChance,
                    markerOreChance,
                    surfaceStoneChance,
                    flatMaterials
            );
        }).collect(Collectors.toList());
    }

}
