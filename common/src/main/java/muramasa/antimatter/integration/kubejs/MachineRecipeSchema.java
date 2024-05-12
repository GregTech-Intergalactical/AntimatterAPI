package muramasa.antimatter.integration.kubejs;

import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.world.item.crafting.Ingredient;

public interface MachineRecipeSchema {
    StringComponent MAP_COMPONENT = new StringComponent("Unknown recipe map", s -> {
        RecipeMap<?> rMap = AntimatterAPI.get(RecipeMap.class, s);
        return rMap != null;
    });
    RecipeKey<String> MAP = MAP_COMPONENT.key("map");
    RecipeKey<InputItem[]> INPUT_ITEMS = ItemComponents.INPUT_ARRAY.key("inputItems");
    RecipeKey<OutputItem[]> OUTPUT_ITEMS = ItemComponents.OUTPUT_ARRAY.key("outputItems");
    RecipeKey<InputFluid[]> INPUT_FLUIDS = FluidComponents.INPUT_ARRAY.key("inputFluids");
    RecipeKey<OutputFluid[]> OUTPUT_FLUIDS = FluidComponents.OUTPUT_ARRAY.key("outputFluids");
    RecipeKey<Integer> DURATION = NumberComponent.INT.key("duration");
    RecipeKey<Long> POWER = NumberComponent.LONG.key("eu");
    RecipeKey<Integer> AMPS = NumberComponent.INT.key("amps").optional(1);
    RecipeKey<Integer> SPECIAL = NumberComponent.INT.key("special").optional(0);
    RecipeKey<Boolean> HIDDEN = BooleanComponent.BOOLEAN.key("hidden").optional(false);
    RecipeKey<Boolean> FAKE = BooleanComponent.BOOLEAN.key("fake").optional(false);
    RecipeKey<Integer[]> OUTPUT_CHANCES = NumberComponent.INT.asArray().key("outputChances");
    RecipeKey<Integer[]> INPUT_CHANCES = NumberComponent.INT.asArray().key("inputChances");

    RecipeSchema SCHEMA = new RecipeSchema(KubeJSRecipe.class, KubeJSRecipe::new, MAP, INPUT_ITEMS, OUTPUT_ITEMS, INPUT_FLUIDS, OUTPUT_FLUIDS, DURATION, POWER, AMPS, SPECIAL, HIDDEN, FAKE, OUTPUT_CHANCES, INPUT_CHANCES);
}
