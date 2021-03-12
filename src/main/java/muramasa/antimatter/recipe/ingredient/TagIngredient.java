package muramasa.antimatter.recipe.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TagIngredient extends AntimatterIngredient{
    protected ITag.INamedTag<Item> tag;

    protected TagIngredient(Stream<? extends IItemList> itemLists, int count, ITag.INamedTag<Item> tag) {
        super(itemLists,count);
        this.tag = tag;
    }

    public ResourceLocation getTag() {
        return tag.getName();
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        return (p_test_1_ != null && p_test_1_.getItem().getTags().contains(tag.getName()) && p_test_1_.getCount() <= count) || super.test(p_test_1_);
    }

    @Override
    public boolean equals(Object o) {
        boolean ok = super.equals(o);
        if (ok) return true;
        if (tag == null) {
            return false;
        }
        if (o instanceof TagIngredient) {
            return ((TagIngredient)o).tag.getName().equals(this.tag.getName()) && this.count >= ((TagIngredient)o).count;
        }
        if (o instanceof Ingredient) {
            for (ItemStack stack : ((Ingredient)o).getMatchingStacks()) {
                if (stack.getItem().getTags().contains(tag.getName())) {
                    return this.count >= stack.getCount();
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return tag == null ? 0 : tag.getName().hashCode();
    }

    @Override
    public boolean testTag(ResourceLocation tag) {
        return tag.equals(this.tag.getName());
    }
}
