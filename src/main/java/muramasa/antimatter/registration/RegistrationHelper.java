package muramasa.antimatter.registration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockStorage;
import muramasa.antimatter.items.MaterialItem;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.Arrays;

public class RegistrationHelper {

    public static void buildMaterialItems(String domain, ItemGroup group) {
        AntimatterAPI.all(MaterialType.class).forEach(t -> AntimatterAPI.all(Material.class).forEach(m -> {
            if (t.allowGeneration(m)) new MaterialItem(domain, t, m, new Item.Properties().group(group));
        }));
    }

    public static void buildOreBlocks(String domain, ItemGroup group) {
        Arrays.stream(StoneType.getAll()).forEach(s -> {
            MaterialType.ORE.all().forEach(m -> {
                new BlockOre(domain, m, s, MaterialType.ORE);
                //new BlockRock(domain, m, s);
            });
            MaterialType.ORE_SMALL.all().forEach(m -> {
                new BlockOre(domain, m, s, MaterialType.ORE_SMALL);
            });
        });
    }

    public static void buildStorageBlocks(String domain, ItemGroup group) {
        MaterialType.BLOCK.all().forEach(m -> new BlockStorage(domain, m, MaterialType.BLOCK));
        MaterialType.FRAME.all().forEach(m -> new BlockStorage(domain, m, MaterialType.FRAME));
    }
}
