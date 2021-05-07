package muramasa.antimatter.recipe.ingredient;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.Ref;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Set;

public class MapItemIngredient extends AbstractMapIngredient {

    public ItemStack stack;

    public MapItemIngredient(ItemStack stack, boolean insideMap) {
        super(insideMap);
        this.stack = stack;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof MapTagIngredient) {
            return stack.getItem().getTags().contains(((MapTagIngredient)o).loc);
        }
        if (o instanceof MapItemIngredient) {
            return compareStacks(((MapItemIngredient)o).stack, stack);
        }
        return false;
    }

    private static boolean compareStacks(ItemStack stackA, ItemStack stackB) {
        if (Utils.hasIgnoreNbtTag(stackA) || Utils.hasIgnoreNbtTag(stackB)) return stackA.getItem() == stackB.getItem();
        if (stackA.getItem() != stackB.getItem()) {
            return false;
        } else if (stackA.getTag() == null && stackB.getTag() != null) {
            return false;
        } else {
            return (stackA.getTag() == null || filterTags(stackA.getTag()).equals(filterTags(stackB.getTag()))) && stackA.areCapsCompatible(stackB);
        }
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

    @Override
    public String toString() {
        return stack.toString();
    }
}
