package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class Unifier {

    private static Set<ItemWrapper> UNIFICATION_BLACKLIST = new ObjectOpenHashSet<>();

    private static Object2ObjectOpenHashMap<Fluid, Fluid> FLUID_DICT = new Object2ObjectOpenHashMap<>();

    static {
        if (Configs.RECIPE.MOD_PRIORITY.length == 0 || Configs.RECIPE.MOD_PRIORITY[0].equals(Ref.ID)) {
            Configs.RECIPE.ENABLE_RECIPE_UNIFICATION = false;
        }
    }

    public static ItemStack get(ItemStack stack) {
        //TODO
//        if (!Configs.RECIPE.ENABLE_RECIPE_UNIFICATION || !(stack.getItem() instanceof MaterialItem)) return stack;
//        String dict = ((MaterialItem) stack.getItem()).getType().oreName(((MaterialItem) stack.getItem()).getMaterial());
//        NonNullList<ItemStack> matchingStacks = OreDictionary.getOres(dict);
//        if (matchingStacks.size() == 0) return stack;
//        for (int i = 0; i < Configs.RECIPE.MOD_PRIORITY.length; i++) {
//            for (int j = 0; j < matchingStacks.size(); j++) {
//                if (matchingStacks.get(j).getItem().getRegistryName() == null) continue;
//                if (matchingStacks.get(j).getItem().getRegistryName().getResourceDomain().equals(Configs.RECIPE.MOD_PRIORITY[i]) &&
//                    !UNIFICATION_BLACKLIST.contains(new ItemWrapper(matchingStacks.get(j)))) {
//                    return matchingStacks.get(j).copy();
//                }
//            }
//        }
        return stack;
    }

    public static Fluid get(Fluid fluid) {
        Fluid replacement = FLUID_DICT.get(fluid);
        return replacement != null ? replacement : fluid;
    }
}
