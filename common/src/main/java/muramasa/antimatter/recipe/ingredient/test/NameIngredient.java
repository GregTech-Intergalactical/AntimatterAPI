package muramasa.antimatter.recipe.ingredient.test;

import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

public class NameIngredient extends Ingredient {

    private final String test;

    protected NameIngredient(Stream<? extends Value> itemLists) {
        super(itemLists);
        this.test = null;
    }

    public NameIngredient(String nameTest) {
        super(Stream.empty());
        this.test = nameTest;
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        return p_test_1_ != null && Objects.requireNonNull(AntimatterPlatformUtils.getIdFromItem(p_test_1_.getItem())).getPath().contains(test);
    }
}
