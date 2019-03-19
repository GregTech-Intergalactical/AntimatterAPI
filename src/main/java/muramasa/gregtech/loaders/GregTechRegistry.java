package muramasa.gregtech.loaders;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.interfaces.GregTechRegistrar;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.common.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;

public class GregTechRegistry {

    /** Registrar Section **/
    private static ArrayList<GregTechRegistrar> REGISTRARS = new ArrayList<>();

    public static void addRegistrar(GregTechRegistrar registrar) {
        if (registrar.isEnabled() || Ref.ENABLE_ALL_REGISTRARS) REGISTRARS.add(registrar);
    }

    public static Collection<GregTechRegistrar> getRegistrars() {
        return REGISTRARS;
    }

    public static MaterialItem getMaterialItem(Prefix prefix, Material material) {
        return (MaterialItem) getItem(prefix.getName() + material.getName());
    }

    public static StandardItem getStandardItem(ItemType type) {
        return (StandardItem) getItem(type.getName());
    }

    public static BlockCasing getCasing(Casing type) {
        return (BlockCasing) getBlock("casing_" + type.getName());
    }

    public static BlockCoil getCoil(Coil type) {
        return (BlockCoil) getBlock("coil_" + type.getName());
    }

    public static BlockOre getOre(StoneType type, Material material) {
        if (!material.has(ItemFlag.ORE)) {
            if (Ref.ENABLE_RECIPE_DEBUG_EXCEPTIONS) {
                throw new IllegalStateException("GET ERROR - DOES NOT GENERATE: P(" + Prefix.Ore.getName() + ") M(" + material.getName() + ")");
            } else {
                System.err.println("GET ERROR - DOES NOT GENERATE: P(" + Prefix.Ore.getName() + ") M(" + material.getName() + ")");
            }
        }
        return (BlockOre) getBlock("ore_" + type.getName() + "_" + material.getName());
    }

    public static BlockStone getStone(StoneType type) {
        return (BlockStone) getBlock("stone_" + type.getName());
    }

    public static BlockStorage getStorage(Material material) {
        return (BlockStorage) getBlock("block_" + material.getName());
    }

    public static Item getItem(String path) {
        return getItem(new ResourceLocation(Ref.MODID, path));
    }

    public static Block getBlock(String path) {
        return getBlock(new ResourceLocation(Ref.MODID, path));
    }

    public static Item getItem(ResourceLocation loc) {
        return Item.getByNameOrId(loc.toString());
    }

    public static Block getBlock(ResourceLocation loc) {
        return Block.getBlockFromName(loc.toString());
    }
}
