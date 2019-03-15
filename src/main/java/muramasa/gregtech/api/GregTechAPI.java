package muramasa.gregtech.api;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;

public class GregTechAPI {

    /** Item Registry Section **/
    public static void addItemReplacement(Prefix prefix, Material material, ItemStack stack) {
        prefix.addReplacement(material, stack);
    }

    /** Block Registry Section **/
    public static void addCasing(String name) {
        new Casing(name);
    }

    public static void addCoil(String name, int heatingCapacity) {
        new Coil(name, heatingCapacity);
    }

    /** Cover Registry Section **/
    private static HashMap<String, Cover> COVER_REGISTRY = new HashMap<>();
    private static HashMap<String, Cover> COVER_CATALYST_REGISTRY = new HashMap<>();

    public static Cover CoverNone;
    public static Cover CoverPlate;
    public static Cover CoverItem;
    public static Cover CoverFluid;
    public static Cover CoverEnergy;
    public static Cover CoverScreen;

    /**
     * Registers a cover behaviour. This must be done during preInit.
     * @param cover The behaviour instance to be attached.
     */
    public static void registerCover(Cover cover) {
        cover.onRegister();
        COVER_REGISTRY.put(cover.getName(), cover);
    }

    public static void registerCoverCatalyst(ItemStack stack, Cover cover) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName != null) COVER_CATALYST_REGISTRY.put(registryName.toString(), cover);
    }

    public static Cover getCover(String name) {
        return COVER_REGISTRY.get(name);
    }

    public static Cover getCoverFromCatalyst(ItemStack stack) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName == null) return null;
        return COVER_CATALYST_REGISTRY.get(registryName.toString());
    }

    public static Collection<Cover> getRegisteredCovers() {
        return COVER_REGISTRY.values();
    }

    /** Attempts to place a cover on a tile at a given side **/
    public static boolean placeCover(TileEntity tile, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
        if (coverHandler == null) return false;
        Cover cover = GregTechAPI.getCoverFromCatalyst(stack);
        if (cover == null) return false;
        if (coverHandler.set(Utils.getInteractSide(side, hitX, hitY, hitZ), cover.getNewInstance(stack))) {
            stack.shrink(1);
            return true;
        }
        return false;
    }
}
