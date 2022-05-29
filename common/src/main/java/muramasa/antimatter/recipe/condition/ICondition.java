package muramasa.antimatter.recipe.condition;

import net.minecraft.resources.ResourceLocation;

public interface ICondition {
    ResourceLocation getID();

    boolean test();
}
