package muramasa.antimatter.recipe.ingredient.test;

import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CapabilityIngredient extends Ingredient {

    private final LazyLoadedValue<Capability<?>> cap;

    protected CapabilityIngredient(Stream<? extends Value> itemLists) {
        super(itemLists);
        cap = null;
    }

    public CapabilityIngredient(Supplier<Capability<?>> cap) {
        super(Stream.empty());
        this.cap = new LazyLoadedValue<>(cap);
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        return p_test_1_ != null && p_test_1_.getCapability(cap.get()).isPresent();
    }
}
