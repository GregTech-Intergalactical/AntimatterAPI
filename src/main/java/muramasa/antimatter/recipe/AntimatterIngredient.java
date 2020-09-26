package muramasa.antimatter.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

public class AntimatterIngredient extends Ingredient {

    private final int count;
    @Nullable
    protected ItemTags.Wrapper tag;

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists);
        this.count = count;
        tag = null;
    }

    protected AntimatterIngredient(Stream<? extends IItemList> itemLists, int count, Tag<Item> tag) {
        this(itemLists,count);
        this.tag = new ItemTags.Wrapper(tag.getId());
    }


    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null) return false;
        return super.test(p_test_1_) && p_test_1_.getCount() >= count;
    }

    public static Ingredient fromStack(ItemStack stack) {
       AntimatterIngredient ing = new AntimatterIngredient(Stream.of(new SingleItemList(stack)), stack.getCount());
       ing.tag = null;
       return ing;
    }

    public static Ingredient fromTag(Tag<Item> tagIn, int count) {
        return new AntimatterIngredient(Stream.of(new TagList(tagIn)), count,tagIn);
    }

    public static Ingredient fromItemListStream(Stream<? extends Ingredient.IItemList> stream, int count) {
        Ingredient ingredient = new AntimatterIngredient(stream, count);
        if (ingredient.hasNoMatchingItems()) return EMPTY;
        return ingredient;
    }
}
