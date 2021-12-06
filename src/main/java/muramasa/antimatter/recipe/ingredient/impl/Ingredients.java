package muramasa.antimatter.recipe.ingredient.impl;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;


public class Ingredients {

    public static final Ingredient BURNABLES = Ingredient.of(ForgeRegistries.ITEMS.getValues().stream().map(Item::getDefaultInstance).filter(t -> ForgeHooks.getBurnTime(t, null) > 0));

}
