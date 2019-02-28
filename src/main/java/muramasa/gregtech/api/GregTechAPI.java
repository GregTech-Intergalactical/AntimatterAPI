package muramasa.gregtech.api;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.enums.Coil;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.materials.Prefix;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;

public class GregTechAPI {

    /** Item Registry Section **/
    public static void addItemReplacement(Prefix prefix, Material material, ItemStack stack) {
        prefix.addItemReplacement(material, stack);
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

    public static Cover CoverBehaviourNone;
    public static Cover CoverBehaviourPlate;
    public static Cover CoverBehaviourItem;
    public static Cover CoverBehaviourFluid;
    public static Cover CoverBehaviourEnergy;

    /**
     * Registers a cover behaviour. This must be done during preInit.
     * @param stack The stack used to place the cover on a machine.
     * @param cover The behaviour instance to be attached.
     */
    public static void registerCover(ItemStack stack, Cover cover) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName != null) {
            COVER_REGISTRY.put(registryName.toString(), cover);
        }
    }

    public static Cover getCover(ItemStack stack) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName != null) {
            return COVER_REGISTRY.get(registryName.toString());
        }
        return null;
    }

    public static Collection<Cover> getRegisteredCovers() {
        return COVER_REGISTRY.values();
    }
}
