package muramasa.antimatter.recipe.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

import java.util.stream.Stream;

public class StackListIngredient extends AntimatterIngredient{
    protected ItemStack[] stacks;

    protected StackListIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists, count);
    }
}
