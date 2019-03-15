package muramasa.gregtech.loaders;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.api.enums.StoneType;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.common.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class GregTechRegistry {

    public static BlockCasing getCasing(Casing type) {
        return (BlockCasing) getBlock("casing_" + type.getName());
    }

    public static BlockCoil getCoil(Coil type) {
        return (BlockCoil) getBlock("coil_" + type.getName());
    }

    public static BlockOre getOre(StoneType type, Material material) {
        return (BlockOre) getBlock("ore_" + type.getName() + "_" + material.getName());
    }

    public static BlockStone getStone(StoneType type) {
        return (BlockStone) getBlock("stone_" + type.getName());
    }

    public static BlockStorage getStorage(Material material) {
        return (BlockStorage) getBlock("block_" + material.getName());
    }

    public static Block getBlock(String path) {
        return getBlock(new ResourceLocation(Ref.MODID, path));
    }

    public static Block getBlock(ResourceLocation loc) {
        return Block.getBlockFromName(loc.toString());
    }
}
