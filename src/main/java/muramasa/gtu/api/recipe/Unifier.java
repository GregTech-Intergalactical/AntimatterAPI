package muramasa.gtu.api.recipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.items.MaterialItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Unifier {

    private static ArrayList<String> MOD_PRIORITY = new ArrayList<>(); //TODO config
    private static Set<ItemStackWrapper> UNIFICATION_BLACKLIST = new HashSet<>();

    public static boolean USE_MOD_PRIORITY = true;

    static {
        MOD_PRIORITY.add(Ref.MODID);
        MOD_PRIORITY.add("ic2");
        MOD_PRIORITY.add(Ref.MOD_TF);

        if (MOD_PRIORITY.size() == 0 || MOD_PRIORITY.get(0).equals(Ref.MODID)) USE_MOD_PRIORITY = false;
    }

    public static ItemStack get(ItemStack stack) {
        if (!USE_MOD_PRIORITY || !(stack.getItem() instanceof MaterialItem)) return stack;
        String dict = ((MaterialItem) stack.getItem()).getPrefix().oreName(((MaterialItem) stack.getItem()).getMaterial());
        NonNullList<ItemStack> matchingStacks = OreDictionary.getOres(dict);
        if (matchingStacks.size() == 0) return stack;
        for (int i = 0; i < MOD_PRIORITY.size(); i++) {
            for (int j = 0; j < matchingStacks.size(); j++) {
                if (matchingStacks.get(j).getItem().getRegistryName() == null) continue;
                if (matchingStacks.get(j).getItem().getRegistryName().getResourceDomain().equals(MOD_PRIORITY.get(i)) &&
                    !UNIFICATION_BLACKLIST.contains(new ItemStackWrapper(matchingStacks.get(j)))) {
                    return matchingStacks.get(j).copy();
                }
            }
        }
        return stack;
    }
}
