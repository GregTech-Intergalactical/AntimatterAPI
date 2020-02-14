package muramasa.antimatter.registration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.items.MaterialItem;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.BlockRock;
import muramasa.antimatter.ore.StoneType;

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
        return getAllMaterialTypes().stream().filter(t -> t.allowGeneration(material)).collect(Collectors.toSet());
    }

    public static Collection<MaterialType> getAllMaterialTypes() {
        return AntimatterAPI.all(MaterialType.class);
    }

    public static void buildMaterialItems(String domain) {
        AntimatterAPI.all(MaterialType.class).forEach(t -> getMaterialsForDomain(domain).forEach(m -> {
            if (t.allowGeneration(m)) new MaterialItem(m.getDomain(), t, m);
        }));
    }

    public static void buildOreBlocks(String domain) {
        AntimatterAPI.all(StoneType.class).forEach(s -> {
            getMaterialsForDomain(domain, MaterialType.ORE.all()).forEach(m -> {
                new BlockOre(m.getDomain(), m, s, MaterialType.ORE);
            });
            getMaterialsForDomain(domain, MaterialType.ORE_SMALL.all()).forEach(m -> {
                new BlockOre(m.getDomain(), m, s, MaterialType.ORE_SMALL);
            });
            getMaterialsForDomain(domain, MaterialType.ROCK.all()).forEach(m -> {
                new BlockRock(domain, m, s);
            });
        });
        getMaterialsForDomain(domain, MaterialType.ORE_STONE.all()).forEach(m -> {
            new BlockOreStone(m.getDomain(), m);
        });
    }

    public static void buildStorageBlocks(String domain) {
        getMaterialsForDomain(domain, MaterialType.BLOCK.all()).forEach(m -> new BlockStorage(m.getDomain(), m, MaterialType.BLOCK));
        getMaterialsForDomain(domain, MaterialType.FRAME.all()).forEach(m -> new BlockStorage(m.getDomain(), m, MaterialType.FRAME));
    }
}
