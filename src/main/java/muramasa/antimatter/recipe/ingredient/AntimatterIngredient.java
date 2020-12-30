package muramasa.antimatter.recipe.ingredient;

import muramasa.antimatter.Ref;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AntimatterIngredient extends Ingredient {
    public int count;
    protected boolean nonConsume = false;

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists);
        this.count = count;
        //Ensure all the matching stacks have the proper count, for rendering.
        for (ItemStack stack : this.getMatchingStacks()) {
            stack.setCount(count);
        }
    }

    public AntimatterIngredient setNonConsume() {
        nonConsume = true;
        for (ItemStack stack : getMatchingStacks()) {
            stack.getOrCreateTag().putBoolean(Ref.KEY_STACK_NO_CONSUME,true);
        }
        return this;
    }

    public boolean noConsume() {
        return nonConsume;
    }

    public abstract boolean testTag(ResourceLocation tag);

    public static int itemHash(ItemStack item) {
        if (item == null) return itemHash(ItemStack.EMPTY);
        boolean nbt = item.hasTag();
        long tempHash = 1;

        tempHash = 31 * tempHash + item.getItem().getRegistryName().toString().hashCode();
        if (nbt && item.getTag() != null) {
            CompoundNBT newNbt = filterTags(item.getTag());
            if (!newNbt.isEmpty()) tempHash = 31 * tempHash + newNbt.hashCode();
        }
        return (int) (tempHash ^ (tempHash >>> 32));
    }

    protected static CompoundNBT filterTags(CompoundNBT nbt) {
        if (nbt == null) return new CompoundNBT();
        CompoundNBT newNbt = nbt.copy();
        newNbt.remove(Ref.KEY_STACK_NO_CONSUME);
        return newNbt;
    }

    //Creates a single antimatteringredient from a single stack.
    public static AntimatterIngredient fromStack(ItemStack stack) {
        int count = stack.getCount() == 0 ? 1 : stack.getCount();
        StackIngredient ing = new StackIngredient(Stream.of(new SingleItemList(stack)), count);
        if (stack.getCount() == 0) ing.nonConsume = true;
        return ing;
    }
    //Convert a list of stacks into a list of ingredients, 1:1.
    public static List<AntimatterIngredient> fromStacksList(ItemStack... stacks) {
        return fromStacksList(Arrays.asList(stacks));
    }

    public static List<AntimatterIngredient> fromStacksList(List<ItemStack> stacks) {
        return stacks.stream().map(AntimatterIngredient::fromStack).collect(Collectors.toList());
    }
    /*
    public static AntimatterIngredient fromStacks(ItemStack... stacks) {
        if (stacks == null || stacks.length == 0) throw new RuntimeException("Invalid input to AntimatterIngredient fromStacks");
        AntimatterIngredient ing = new AntimatterIngredient(Arrays.stream(stacks).map(SingleItemList::new), stacks[0].getCount());
        ing.multipleItems = Arrays.stream(stacks).map(AntimatterIngredient::fromStack).collect(Collectors.toSet());
        return ing;
    }*/

    public static AntimatterIngredient fromTag(Tag<Item> tagIn, int count) {
        return new TagIngredient(Stream.of(new TagList(tagIn)), count,tagIn);
    }

    //UTILITY FUNCTIONS
    public static AntimatterIngredient of(Tag<Item> tagIn, int count) {
        return fromTag(tagIn,count);
    }

    public static AntimatterIngredient of(ItemStack stack) {
        return fromStack(stack);
    }
    public static AntimatterIngredient of(Item item, int count) {
        return fromStack(new ItemStack(item,count));
    }
    //DEFAULT BEHAVIOUR: 1-1, not MANY-1. Use specific method for that.
    public static List<AntimatterIngredient> of(ItemStack... stack) {
        return fromStacksList(stack);
    }

    @Override
    public String toString() {
        return Arrays.stream(getMatchingStacks()).map(t -> t.getItem().toString()).collect(Collectors.joining());
    }

    //Compares items, ignoring count.
    public static boolean compareItems(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else {
            return !stackA.isEmpty() && !stackB.isEmpty() && compareStacks(stackA, stackB);
        }
    }

    private static boolean compareStacks(ItemStack stackA, ItemStack stackB) {
        if (stackA.getItem() != stackB.getItem()) {
            return false;
        } else if (stackA.getTag() == null && stackB.getTag() != null) {
            return false;
        } else {
            return (stackA.getTag() == null || filterTags(stackA.getTag()).equals(filterTags(stackB.getTag()))) && stackA.areCapsCompatible(stackB);
        }
    }
}
