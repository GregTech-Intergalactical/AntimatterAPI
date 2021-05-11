package muramasa.antimatter.recipe.ingredient;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Set;

public class MapItemStackIngredient extends AbstractMapIngredient {

    public final ItemStack stack;
    private final CompoundNBT tag;

    public MapItemStackIngredient(ItemStack stack, boolean insideMap) {
        super(insideMap);
        this.stack = stack;
        this.tag = filterTags(stack.getTag());
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof MapTagIngredient) {
            return stack.getItem().getTags().contains(((MapTagIngredient)o).loc);
        }
        if (o instanceof MapItemIngredient) {
            return ((MapItemIngredient)o).stack.equals(this.stack.getItem());
        }
        if (o instanceof MapItemStackIngredient) {
            MapItemStackIngredient s = (MapItemStackIngredient) o;
            return compareStacks(stack, s.stack, tag, s.tag);
        }
        return false;
    }

    private static boolean compareStacks(ItemStack a, ItemStack b, CompoundNBT aTag, CompoundNBT bTag) {
        if (a.getItem() != b.getItem()) return false;
        if (aTag.isEmpty() != bTag.isEmpty()) return false;
        if (!aTag.equals(bTag)) return false;
        return a.areCapsCompatible(b);
    }
    protected static final Set<String> CUSTOM_TAGS = ImmutableSet.of(Ref.KEY_STACK_NO_CONSUME, Ref.KEY_STACK_IGNORE_NBT);

    /**
     * Filters out tags that are static, not used for lookup.
     * @param nbt Compound to filter.
     * @return copied, filtered compound.
     */
    protected static CompoundNBT filterTags(CompoundNBT nbt) {
        if (nbt == null) return new CompoundNBT();
        CompoundNBT newNbt = nbt.copy();
        CUSTOM_TAGS.forEach(newNbt::remove);
        return newNbt;
    }

    @Override
    protected int hash() {
        boolean nbt = stack.hasTag();
        long tempHash = 1;

        tempHash = 31 * tempHash + stack.getItem().getRegistryName().hashCode();
        if (nbt && stack.getTag() != null) {
            CompoundNBT newNbt = filterTags(stack.getTag());
            if (!newNbt.isEmpty()) tempHash = 31 * tempHash + newNbt.hashCode();
        }
        return (int) (tempHash ^ (tempHash >>> 32));
    }

}
