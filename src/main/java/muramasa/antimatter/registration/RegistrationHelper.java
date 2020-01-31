package muramasa.antimatter.registration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.items.MaterialItem;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistrationHelper {

    public static void buildDefaultMaterialDerivedObjects(String domain) {
        buildMaterialItems(domain);
        buildOreBlocks(domain);
        buildStorageBlocks(domain);
    }

    public static Set<Material> getMaterialsForDomain(String domain, Collection<Material> materials) {
        return materials.stream().filter(m -> m.getDomain().equals(domain)).collect(Collectors.toSet());
    }

    public static Set<Material> getMaterialsForDomain(String domain) {
        return getMaterialsForDomain(domain, AntimatterAPI.all(Material.class));
    }

    public static Set<MaterialType> getValidTypesForMaterial(Material material) {
        return AntimatterAPI.all(MaterialType.class).stream().filter(t -> t.allowGeneration(material)).collect(Collectors.toSet());
    }

    public static void buildMaterialItems(String domain) {
        getMaterialsForDomain(domain).forEach(m -> getValidTypesForMaterial(m).forEach(t -> new MaterialItem(m.getDomain(), t, m)));
    }

    public static void buildOreBlocks(String domain) {
        Arrays.stream(StoneType.getAll()).forEach(s -> {
            getMaterialsForDomain(domain, MaterialType.ORE.all()).forEach(m -> {
                new BlockOre(m.getDomain(), m, s, MaterialType.ORE);
                //new BlockRock(domain, m, s);
            });
            getMaterialsForDomain(domain, MaterialType.ORE_SMALL.all()).forEach(m -> {
                new BlockOre(m.getDomain(), m, s, MaterialType.ORE_SMALL);
            });
        });
    }

    public static void buildStorageBlocks(String domain) {
        getMaterialsForDomain(domain, MaterialType.BLOCK.all()).forEach(m -> new BlockStorage(m.getDomain(), m, MaterialType.BLOCK));
        getMaterialsForDomain(domain, MaterialType.FRAME.all()).forEach(m -> new BlockStorage(m.getDomain(), m, MaterialType.FRAME));
    }
}
