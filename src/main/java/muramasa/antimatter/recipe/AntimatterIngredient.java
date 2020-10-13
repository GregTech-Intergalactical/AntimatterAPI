package muramasa.antimatter.recipe;

import muramasa.antimatter.util.Utils;
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

    protected static AntimatterIngredient TAG_HASHER = new AntimatterIngredient(Stream.empty(),0);
    @Nullable
    //Not actually used in real recipes but used in lookup to avoid allocations.
    protected ItemStack item;
    public int count;
    @Nullable
    protected ItemTags.Wrapper tag;
    @Nullable
    protected Set<AntimatterIngredient> multipleItems;
    public int whichStackToHash = 0;

    protected boolean nonConsume = false;

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists);
        this.count = count;
        tag = null;
        //Ensure all the matching stacks have the proper count, for rendering.
        for (ItemStack stack : this.getMatchingStacks()) {
            stack.setCount(count);
        }
    }
    @Nullable
    public ResourceLocation getTagResource() {
        return tag == null ? null : tag.getId();
    }

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count, Tag<Item> tag) {
        this(itemLists,count);
        this.tag = new ItemTags.Wrapper(tag.getId());
    }

    public static AntimatterIngredient getHashable(ResourceLocation rl, int count) {
        TAG_HASHER.tag = new ItemTags.Wrapper(rl);
        TAG_HASHER.count = count;
        return TAG_HASHER;
    }

    public static AntimatterIngredient getHashable(ItemStack rl) {
        TAG_HASHER.tag = null;
        TAG_HASHER.item = rl;
        TAG_HASHER.count = rl.getCount();
        return TAG_HASHER;
    }

    private boolean checkCount(AntimatterIngredient other) {
        return this.count >= other.count;
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
            if (this.item != null) {
                if (ai.item != null) {
                    return Utils.equals(this.item,ai.item);
                } else {
                    return ing.test(this.item);
                }
            }
        }
        for (ItemStack stack : this.getMatchingStacks()) {
            if (ing.test(stack)) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        //Hash == tag if present.
        if (this.tag != null) return this.tag.getId().hashCode();
        if (this.item != null) return itemHash(this.item);
        if (this.getMatchingStacks().length == 0) {
            return Integer.hashCode(0);
        }
        //For single stack, just return the item.
        if (this.getMatchingStacks().length == 1) return itemHash(item != null ? item : this.getMatchingStacks()[0]);
        //TODO: Support multiple item inputs. This is not done.
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
        if (tag != null) return p_test_1_.getItem().getTags().contains(tag.getId()) && p_test_1_.getCount() >= count;
        return super.test(p_test_1_) && p_test_1_.getCount() >= count;
    }
    //Creates a single antimatteringredient from a single stack.
    public static AntimatterIngredient fromStack(ItemStack stack) {
        int count = stack.getCount() == 0 ? 1 : stack.getCount();
        AntimatterIngredient ing = new AntimatterIngredient(Stream.of(new SingleItemList(stack)), count);
        if (stack.getCount() == 0) ing.nonConsume = true;
        ing.tag = null;
        return ing;
    }
    //Convert a list of stacks into a list of ingredients, 1:1.
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


}
