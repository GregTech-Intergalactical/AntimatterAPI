package muramasa.antimatter.recipe.ingredient;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class MapTagIngredient extends AbstractMapIngredient {
    public final ResourceLocation loc;

    public MapTagIngredient(ResourceLocation tag) {
        this.loc = tag;
    }

    @Override
    protected int hash() {
        return loc.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MapTagIngredient) {
            return ((MapTagIngredient)o).loc.equals(loc);
        }
        if (o instanceof MapItemIngredient) {
            return ((MapItemIngredient)o).stack.getItem().getTags().contains(loc);
        }
        if (o instanceof MapFluidIngredient) {
            return ((MapFluidIngredient)o).stack.getFluid().getTags().contains(loc);
        }
        return false;
    }

    private static final boolean ENABLE_TAGS_LOOKUP = true;
    public static Optional<ResourceLocation> findCommonTag(Ingredient ing, Function<Item, Collection<ResourceLocation>> tagGetter) {
         if (!ENABLE_TAGS_LOOKUP || ing.getMatchingStacks().length < 2) return Optional.empty();
         Optional<Set<ResourceLocation>> l = Arrays.stream(ing.getMatchingStacks()).map(t -> (Set<ResourceLocation>) new ObjectOpenHashSet<>(tagGetter.apply(t.getItem()))).reduce((s, b) -> {
             s.retainAll(b);
             return s;
         });
         return l.map(t -> {
             for (ResourceLocation rl : l.get()) {
                 if (TagCollectionManager.getManager().getItemTags().get(rl).getAllElements().size() == ing.getMatchingStacks().length) {
                     return rl;
                 }
             }
             return null;
         });
    }

    @Override
    public String toString() {
        return loc.toString();
    }
}
