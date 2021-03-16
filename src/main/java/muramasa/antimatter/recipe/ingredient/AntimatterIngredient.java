package muramasa.antimatter.recipe.ingredient;

import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.Ref;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AntimatterIngredient extends Ingredient {

    //The ingredient count, required number.
    public final int count;
    protected boolean nonConsume = false;
    protected boolean ignoreNbt = false;
    //Tags to be filtered out from lookup.
    protected static final Set<String> CUSTOM_TAGS = ImmutableSet.of(Ref.KEY_STACK_NO_CONSUME, Ref.KEY_STACK_IGNORE_NBT);

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists);
        this.count = count;
        //Ensure all the matching stacks have the proper count, for rendering.
        for (ItemStack stack : this.getMatchingStacks()) {
            stack.setCount(count);
        }
    }

    /**
     * Whether to not consume the item during recipe tick.
     * @return this
     */
    public AntimatterIngredient setNonConsume() {
        nonConsume = true;
        for (ItemStack stack : getMatchingStacks()) {
            stack.getOrCreateTag().putBoolean(Ref.KEY_STACK_NO_CONSUME,true);
        }
        return this;
    }

    /**
     * Whether to ignore nbt in the lookup, to allow any.
     * @return this
     */
    public AntimatterIngredient setIgnoreNbt() {
        ignoreNbt = true;
        for (ItemStack stack : getMatchingStacks()) {
            stack.getOrCreateTag().putBoolean(Ref.KEY_STACK_IGNORE_NBT,true);
        }
        return this;
    }

    public boolean noConsume() {
        return nonConsume;
    }
    public boolean ignoreNbt() {
        return ignoreNbt;
    }

    /**
     * See if this AMIngredient has the tag.
     * @param tag the tag.
     * @return whether it contains it.
     */
    public abstract boolean testTag(ResourceLocation tag);

    /**
     * Hashes an itemstack.
     * @param item the stack.
     * @return a hash.
     */
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

    //Creates a single antimatteringredient from a single stack.
    public static LazyValue<AntimatterIngredient> fromStack(LazyValue<ItemStack> provider, Consumer<AntimatterIngredient> builder) {
        return new LazyValue<>(() -> {
            ItemStack stack = provider.getValue();
            StackIngredient ing = new StackIngredient(Stream.of(new SingleItemList(stack)), stack.getCount());
            builder.accept(ing);
            return ing;
        });
    }
    public static LazyValue<AntimatterIngredient> fromStack(LazyValue<ItemStack> provider) {
        return fromStack(provider, a -> {});
    }
    public static LazyValue<AntimatterIngredient> fromItem(int count, IItemProvider provider) {
        return fromItem(count,provider, a -> {});
    }
    public static LazyValue<AntimatterIngredient> fromItem(int count, IItemProvider provider, Consumer<AntimatterIngredient> builder) {
        return fromStack(new LazyValue<>(() -> new ItemStack(provider, count)), builder);
    }
    public static LazyValue<AntimatterIngredient> fromStacks(int count, LazyValue<ItemStack>... stacks) {
        return fromStacks(count, a -> {}, stacks);
    }
    public static LazyValue<AntimatterIngredient> fromStacks(int count, Consumer<AntimatterIngredient> builder, LazyValue<ItemStack>... stacks) {
        if (stacks == null || stacks.length == 0) throw new RuntimeException("Invalid input to AntimatterIngredient fromStacks");
        return new LazyValue<>(() -> {
            StackListIngredient stk = new StackListIngredient(Arrays.stream(stacks).map(t -> new SingleItemList(t.getValue())), count);
            builder.accept(stk);
            return stk;
        });
    }
    public static LazyValue<AntimatterIngredient> fromTag(ITag.INamedTag<Item> tagIn, int count, Consumer<AntimatterIngredient> builder) {
        return new LazyValue<>(() -> {
            TagIngredient tag = new TagIngredient(Stream.of(new TagList(tagIn)), count,tagIn);
            builder.accept(tag);
            return tag;
        });
    }
    public static LazyValue<AntimatterIngredient> fromTag(ITag.INamedTag<Item> tagIn, int count) {
        return fromTag(tagIn, count, a -> {});
    }


    /** UTILITY FUNCTIONS **/

    public static LazyValue<AntimatterIngredient> of(ITag.INamedTag<Item> tagIn, int count) {
        return fromTag(tagIn,count);
    }
    public static LazyValue<AntimatterIngredient> of(IItemProvider item, int count) {
        return fromStack(new LazyValue<>(() -> new ItemStack(item, count)));
    }
    public static LazyValue<AntimatterIngredient> of(ItemStack stack) {
        return fromStack(new LazyValue<>(() -> stack));
    }

    public static LazyValue<AntimatterIngredient> of(LazyValue<ItemStack> item) {
        return fromStack(item);
    }

    public static LazyValue<AntimatterIngredient> of(int count, LazyValue<ItemStack>... stacks) {
        return fromStacks(count, stacks);
    }
    //In case it is safe.
    public static LazyValue<AntimatterIngredient> of(int count, ItemStack... stacks) {
        return fromStacks(count, Arrays.stream(stacks).map(t -> new LazyValue<>(() -> t)).toArray(LazyValue[]::new));
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
        if (Utils.hasIgnoreNbtTag(stackA) || Utils.hasIgnoreNbtTag(stackB)) return stackA.getItem() == stackB.getItem();
        if (stackA.getItem() != stackB.getItem()) {
            return false;
        } else if (stackA.getTag() == null && stackB.getTag() != null) {
            return false;
        } else {
            return (stackA.getTag() == null || filterTags(stackA.getTag()).equals(filterTags(stackB.getTag()))) && stackA.areCapsCompatible(stackB);
        }
    }
}
