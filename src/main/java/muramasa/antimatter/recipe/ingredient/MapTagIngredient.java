package muramasa.antimatter.recipe.ingredient;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class MapTagIngredient extends AbstractMapIngredient {

    public ResourceLocation loc;

    public MapTagIngredient(ResourceLocation tag, boolean insideMap) {
        super(insideMap);
        this.loc = tag;
    }

    public void setTag(ResourceLocation loc) {
        this.loc = loc;
        invalidate();
    }

    @Override
    protected int hash() {
        return loc.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MapTagIngredient) {
            return ((MapTagIngredient) o).loc.equals(loc);
        }
        if (o instanceof MapItemIngredient) {
            return ((MapItemIngredient) o).stack.getItem().getTags().contains(loc);
        }
        if (o instanceof MapFluidIngredient) {
            return ((MapFluidIngredient) o).stack.getFluid().getTags().contains(loc);
        }
        return false;
    }

    private static final boolean ENABLE_TAGS_LOOKUP = true;

    public static Optional<ResourceLocation> findCommonTag(Ingredient ing, ITagCollectionSupplier tags) {
        if (!ENABLE_TAGS_LOOKUP || ing.getItems().length < 2) return Optional.empty();
        Optional<Set<ResourceLocation>> l = Arrays.stream(ing.getItems()).map(t -> (Set<ResourceLocation>) new ObjectOpenHashSet<>(tags.getItems().getMatchingTags(t.getItem()))).reduce((s, b) -> {
            s.retainAll(b);
            return s;
        });
        return l.map(t -> {
            for (ResourceLocation rl : l.get()) {
                if (tags.getItems().getTagOrEmpty(rl).getValues().size() == ing.getItems().length) {
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
