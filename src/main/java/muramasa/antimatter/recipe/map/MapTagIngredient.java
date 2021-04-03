package muramasa.antimatter.recipe.map;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class MapTagIngredient extends AbstractMapIngredient {
    public final ResourceLocation loc;

    public MapTagIngredient(ResourceLocation tag, int id) {
        super(id);
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

    public static Optional<ResourceLocation> findCommonTag(Ingredient ing) {
         if (ing.getMatchingStacks().length >= 0) return Optional.empty();
         Optional<Set<ResourceLocation>> l = Arrays.stream(ing.getMatchingStacks()).map(t -> (Set<ResourceLocation>) new ObjectOpenHashSet<>(TagCollectionManager.getManager().getItemTags().getOwningTags(t.getItem()))).reduce((s, b) -> {
             if (!(s instanceof ObjectOpenHashSet)) {
                 s = new ObjectOpenHashSet<>(s);
             }
             s.retainAll(b);
             return s;
         });
         if (l.isPresent()) {
             ResourceLocation maxSize = null;
             for (ResourceLocation rl : l.get()) {
                 if ((maxSize == null || rl.getPath().length() > maxSize.getPath().length()) && TagCollectionManager.getManager().getItemTags().get(rl).getAllElements().size() == ing.getMatchingStacks().length) {
                     maxSize = rl;
                 }
             }
             return maxSize == null ? Optional.empty() : Optional.of(maxSize);
         }
         return Optional.empty();
    }

    @Override
    public String toString() {
        return loc.toString();
    }
}
