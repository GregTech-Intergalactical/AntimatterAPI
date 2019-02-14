package muramasa.gregtech.api;

import muramasa.gregtech.api.cover.CoverBehaviour;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;

public class GregTechAPI {

    /** Cover Registry Section **/

    private static HashMap<String, CoverBehaviour> COVER_REGISTRY = new HashMap<>();

    public static CoverBehaviour CoverBehaviourNone;
    public static CoverBehaviour CoverBehaviourPlate;
    public static CoverBehaviour CoverBehaviourItem;
    public static CoverBehaviour CoverBehaviourFluid;
    public static CoverBehaviour CoverBehaviourEnergy;

    /**
     * Registers a cover behaviour. This must be done during preInit.
     * @param stack The stack used to place the cover on a machine.
     * @param behaviour The behaviour instance to be attached.
     */
    public static void registerCoverBehaviour(ItemStack stack, CoverBehaviour behaviour) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName != null) {
            COVER_REGISTRY.put(registryName.toString(), behaviour);
        }
    }

    public static CoverBehaviour getCoverBehaviour(ItemStack stack) {
        ResourceLocation registryName = stack.getItem().getRegistryName();
        if (registryName != null) {
            return COVER_REGISTRY.get(registryName.toString());
        }
        return null;
    }

    public static Collection<CoverBehaviour> getRegisteredBehaviours() {
        return COVER_REGISTRY.values();
    }
}
