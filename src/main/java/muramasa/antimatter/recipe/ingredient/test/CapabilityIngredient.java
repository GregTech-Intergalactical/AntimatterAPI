package muramasa.antimatter.recipe.ingredient.test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CapabilityIngredient extends Ingredient {

    private final LazyValue<Capability<?>> cap;

    protected CapabilityIngredient(Stream<? extends IItemList> itemLists) {
        super(itemLists);
        cap = null;
    }

    public CapabilityIngredient(Supplier<Capability<?>> cap) {
        super(Stream.empty());
        this.cap = new LazyValue<>(cap);
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        return p_test_1_ != null && p_test_1_.getCapability(cap.get()).isPresent();
    }
}
