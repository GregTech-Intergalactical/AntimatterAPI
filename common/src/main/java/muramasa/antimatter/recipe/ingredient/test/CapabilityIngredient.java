package muramasa.antimatter.recipe.ingredient.test;

import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

//TODO is this even needed?
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
    public boolean test(@Nullable ItemStack stack) {
        return stack != null && stack.getCapability(cap.get()).isPresent();
    }
}
