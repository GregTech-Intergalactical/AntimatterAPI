package muramasa.antimatter.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AntimatterIngredient extends Ingredient {

    public final int count;
    @Nullable
    protected ItemTags.Wrapper tag;

    @Nullable
    protected Set<AntimatterIngredient> multipleItems;

    public int whichStackToHash = 0;

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists);
        this.count = count;
        tag = null;
    }
    @Nullable
    public ResourceLocation getTagResource() {
        return tag == null ? null : tag.getId();
    }

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count, Tag<Item> tag) {
        this(itemLists,count);
        this.tag = new ItemTags.Wrapper(tag.getId());
    }

    private boolean checkCount(AntimatterIngredient other) {
        return other.count >= this.count;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ingredient)) return false;
        Ingredient ing = (Ingredient) o;
        if (ing instanceof AntimatterIngredient) {
            AntimatterIngredient ai = (AntimatterIngredient) ing;
            if (this.tag != null) {
                if (ai.tag != null) return this.tag.getId().equals(ai.tag.getId()) && checkCount(ai);
                else return false;
            }
        }
        for (ItemStack stack : this.getMatchingStacks()) {
            if (ing.test(stack)) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.tag != null) return this.tag.getId().hashCode();
        if (this.getMatchingStacks().length == 1) return itemHash(this.getMatchingStacks()[0]);
        //TODO: Support multiple item inputs.
        return itemHash(this.getMatchingStacks()[whichStackToHash]);
    }

    private int itemHash(@Nonnull ItemStack item) {
        boolean nbt = item.hasTag();
        long tempHash = 1;
        tempHash = 31 * tempHash + item.getItem().getRegistryName().toString().hashCode();
        if (nbt) tempHash = 31 * tempHash + item.getTag().hashCode();
        return (int) (tempHash ^ (tempHash >>> 32));
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null) return false;
        return super.test(p_test_1_) && p_test_1_.getCount() >= count;
    }

    public static AntimatterIngredient fromStack(ItemStack stack) {
       AntimatterIngredient ing = new AntimatterIngredient(Stream.of(new SingleItemList(stack)), stack.getCount());
       ing.tag = null;
       return ing;
    }

    public static List<AntimatterIngredient> fromStacksList(ItemStack... stacks) {
        return Arrays.stream(stacks).map(AntimatterIngredient::fromStack).collect(Collectors.toList());
    }

    public static AntimatterIngredient fromStacks(ItemStack... stacks) {
        if (stacks == null || stacks.length == 0) throw new RuntimeException("Invalid input to AntimatterIngredient fromStacks");
        AntimatterIngredient ing = new AntimatterIngredient(Arrays.stream(stacks).map(SingleItemList::new), stacks[0].getCount());
        ing.multipleItems = Arrays.stream(stacks).map(AntimatterIngredient::fromStack).collect(Collectors.toSet());
        return ing;
    }

    public static AntimatterIngredient fromTag(Tag<Item> tagIn, int count) {
        return new AntimatterIngredient(Stream.of(new TagList(tagIn)), count,tagIn);
    }

    public static AntimatterIngredient fromItemListStream(Stream<? extends Ingredient.IItemList> stream, int count) {
        AntimatterIngredient ingredient = new AntimatterIngredient(stream, count);
        if (ingredient.hasNoMatchingItems()) return null;
        return ingredient;
    }
}
