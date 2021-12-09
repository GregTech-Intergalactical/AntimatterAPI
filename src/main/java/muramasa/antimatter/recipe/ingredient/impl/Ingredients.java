package muramasa.antimatter.recipe.ingredient.impl;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Stream;


public class Ingredients {

    public static final Ingredient BURNABLES = Ingredient.of(ForgeRegistries.ITEMS.getValues().stream().map(Item::getDefaultInstance).filter(t -> ForgeHooks.getBurnTime(t, null) > 0));

    public static final RecyclerIngredient RECYCLABLE = new RecyclerIngredient();

    static {
        RECYCLABLE.BLACKLIST.add(Items.COBBLESTONE);
        RECYCLABLE.BLACKLIST.add(Items.DIRT);
        RECYCLABLE.BLACKLIST.add(Items.STICK);
    }
    public static class RecyclerIngredient extends Ingredient {

        public final Set<Item> BLACKLIST = new ObjectOpenHashSet<>();
        protected RecyclerIngredient() {
            super(Stream.empty());
        }

        @Override
        public boolean test(@Nullable ItemStack p_43914_) {
            return p_43914_ != null && !BLACKLIST.contains(p_43914_.getItem());
        }
    }
}
