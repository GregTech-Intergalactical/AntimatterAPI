package muramasa.antimatter.recipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.impl.MachineFluidHandler;
import muramasa.antimatter.capability.impl.MachineItemHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {

    private HashMap<RecipeInput, Recipe> LOOKUP;
    private String id;
    private B builder;

    public RecipeMap(String categoryId, B builder) {
        this.id = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
        LOOKUP = new HashMap<>();
        AntimatterAPI.register(RecipeMap.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("jei.category." + id);
    }

    public ITextComponent getExtraString(String extraId) {
        return new TranslationTextComponent("jei.category." + this.id + "." + extraId);
    }

    public B RB() {
        builder.clear();
        return builder;
    }

    public HashMap<RecipeInput, Recipe> getRawMap() {
        return LOOKUP;
    }

    public Collection<Recipe> getRecipes(boolean filterHidden) {
        if (filterHidden) return LOOKUP.values().stream().filter(r -> !r.isHidden()).collect(Collectors.toList());
        return LOOKUP.values().stream().collect(Collectors.toList());
    }

    void add(Recipe recipe) {
        if (LOOKUP.containsKey(new RecipeInputFlat(recipe.getInputItems(), recipe.getInputFluids()))) {
            Utils.onInvalidData("Duplicate recipe detected!: " + recipe);
        }
        else LOOKUP.put(new RecipeInput(recipe.getInputItems(), recipe.getInputFluids(), recipe.getTags()), recipe);
    }

    @Nullable
    public Recipe find(@Nullable MachineItemHandler itemHandler, @Nullable MachineFluidHandler fluidHandler) {
        return find(itemHandler != null ? itemHandler.getInputs() : null, fluidHandler != null ? fluidHandler.getInputs() : null);
    }

    @Nullable
    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        if (((items != null && items.length > 0) && !Utils.areItemsValid(items)) || ((fluids != null && fluids.length > 0) && !Utils.areFluidsValid(fluids))) return null;
        return LOOKUP.get(new RecipeInput(items, fluids));
    }

    public Recipe find(long tier, @Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        Recipe r = find(items, fluids);
        return r.getPower() <= tier ? r : null;
    }

    public Recipe find(Tier tier, @Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        return find(tier.getVoltage(), items, fluids);
    }

    /** Test **/
    public static void dumpHashCollisions() {
        HashMap<Integer, Map.Entry> previousHashes = new HashMap<>();
        System.out.println("DUMP START");
        for (RecipeMap map : AntimatterAPI.all(RecipeMap.class)) {
            previousHashes.clear();
            map.getRawMap().entrySet().forEach(e -> {
                Map.Entry entry = (Map.Entry) e;
                if (previousHashes.containsKey(entry.getKey().hashCode())) {
                    System.out.println("COLLISION:");
                    System.out.println("Map: " + map.getId());
                    System.out.println("Existing Hash: " + previousHashes.get(entry.getKey().hashCode()).getKey().hashCode());
                    System.out.println("Existing Recipe: ");
                    System.out.println(previousHashes.get(entry.getKey().hashCode()).getValue().toString());
                    System.out.println("Duplicate Hash: " + entry.getKey().hashCode());
                    System.out.println("Duplicate Recipe: ");
                    System.out.println(entry.getValue().toString());
                }
                previousHashes.put(entry.getKey().hashCode(), entry);
            });
        }
        System.out.println("DUMP END");
    }
}
