package muramasa.antimatter.recipe.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TagIngredient extends AntimatterIngredient{
    protected Tag<Item> tag;

    protected TagIngredient(Stream<? extends IItemList> itemLists, int count, Tag<Item> tag) {
        super(itemLists,count);
        this.tag = tag;
    }

    public ResourceLocation getTag() {
        return tag.getId();
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        return (p_test_1_ != null && p_test_1_.getItem().getTags().contains(tag.getId()) && p_test_1_.getCount() <= count) || super.test(p_test_1_);
    }

    @Override
    public boolean equals(Object o) {
        boolean ok = super.equals(o);
        if (ok) return true;
        if (tag == null) {
            return false;
        }
        if (o instanceof TagIngredient) {
            return ((TagIngredient)o).tag.getId().equals(this.tag.getId()) && this.count >= ((TagIngredient)o).count;
        }
        if (o instanceof Ingredient) {
            for (ItemStack stack : ((Ingredient)o).getMatchingStacks()) {
                if (stack.getItem().getTags().contains(tag.getId())) {
                    return this.count >= stack.getCount();
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return tag == null ? 0 : tag.getId().hashCode();
    }

    @Override
    public boolean testTag(ResourceLocation tag) {
        return tag.equals(this.tag.getId());
    }
}
