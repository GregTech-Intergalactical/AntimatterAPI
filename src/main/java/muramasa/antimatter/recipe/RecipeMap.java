package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {

    protected static class RecipeTagMap {
        //First, match the non-tagged items.
        private Object2ObjectMap<RecipeInput, Int2ObjectMap<Recipe>> LOOKUP_TAG;

        public RecipeTagMap() {
            this.LOOKUP_TAG = new Object2ObjectLinkedOpenHashMap<>();
        }

        public Recipe find(RecipeInput input) {
            List<ItemWrapper> tagged = input.trimAllTags();
            Int2ObjectMap<Recipe> possibleResults = LOOKUP_TAG.get(input);
            if (possibleResults != null) {
               return recursiveHash(input,tagged.stream().map(t ->{
                   //Set<ResourceLocation> rl = new HashSet<>();
                   //rl.add(new ResourceLocation("acacia_logs"));
                   return t.item.getItem().getTags();
               }).collect(Collectors.toList()), 0,0,0,(k, v) -> {
                   int hash = (int)(v ^ (v >>> 32));
                   Recipe r = possibleResults.get(hash);
                   return r;
               });
            }

            return null;
        }

        Recipe recursiveHash(RecipeInput input, java.util.List<Set<ResourceLocation>> arrayList, int element, long acc, long whichNonTagged, BiFunction<List<Set<ResourceLocation>>,Long, Recipe> func) {
            if (element > arrayList.size()) {
                return null;
            }
            Recipe ok = recursiveHash(input,arrayList, element + 1, acc, whichNonTagged | element, func);
            if (ok != null) {
                return ok;
            }
            for (int i = element; i < arrayList.size(); i++) {
                Set<ResourceLocation> tags = arrayList.get(i);
                for (ResourceLocation r : tags) {
                    ok = recursiveHash(input,arrayList, element + 1, acc + r.hashCode(),whichNonTagged, func);
                    if (ok != null) {
                        return ok;
                    }
                }
            }
            if (element >= arrayList.size() - 1) {
                return func.apply(arrayList,acc);
            } else {
                whichNonTagged = 1;
                ItemStack[] wraps = new ItemStack[Long.bitCount(whichNonTagged)];
                int count = 0;
                for (int i = 0; (whichNonTagged & (1 << i)) != 0; i++) {
                    wraps[count] = input.rootItems[i];
                }
                RecipeInput newInput = new RecipeInput(wraps, input.rootFluids);
                Int2ObjectMap<Recipe> map = this.LOOKUP_TAG.get(newInput);
                if (map != null) {
                    Recipe r = map.get((int)acc);
                    if (r != null) {
                        return r;
                    }
                }
            }
            return null;
        }

        public void add(Recipe recipe) {
            RecipeInput input = new RecipeInput(recipe.getInputItems(), recipe.getInputFluids(), recipe.getTags());
            long code = 0;
            for (TagInput wr : recipe.getTagInputs()) {
                code += wr.tag.getId().hashCode();
            }
            code = code ^ (code >>> 32);
            int finalCode = (int)code;
            LOOKUP_TAG.compute(input, (k, v) -> {
                if (v == null) {
                    v = new Int2ObjectOpenHashMap<>();
                }
                Recipe error = v.put(finalCode, recipe);
                if (error !=null) {
                    throw new RuntimeException("duplicate recipe");
                }
                return v;
            });
        }
    }

    private Object2ObjectMap<RecipeInput, Recipe> LOOKUP;
    private RecipeTagMap LOOKUP_TAG;

    private String id;
    private B builder;

    public RecipeMap(String categoryId, B builder) {
        this.id = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
        LOOKUP = new Object2ObjectLinkedOpenHashMap<>();
        LOOKUP_TAG = new RecipeTagMap();
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

    public Map<RecipeInput, Recipe> getRawMap() {
        return LOOKUP;
    }

    public Collection<Recipe> getRecipes(boolean filterHidden) {
        if (filterHidden) return LOOKUP.values().stream().filter(r -> !r.isHidden()).collect(Collectors.toList());
        return LOOKUP.values();
    }

    void add(Recipe recipe) {
        if (LOOKUP.containsKey(new RecipeInputFlat(recipe.getInputItems(), recipe.getInputFluids()))) {
            Utils.onInvalidData("Duplicate recipe detected!: " + recipe);
            return;
        }
        if (recipe.hasTags()) {
            LOOKUP_TAG.add(recipe);
        } else LOOKUP.put(new RecipeInput(recipe.getInputItems(), recipe.getInputFluids(), recipe.getTags()), recipe);
    }

    @Nullable
    public Recipe find(@Nullable MachineItemHandler itemHandler, @Nullable MachineFluidHandler fluidHandler) {
        return find(itemHandler != null ? itemHandler.getInputs() : null, fluidHandler != null ? fluidHandler.getInputs() : null);
    }

    @Nullable
    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        if (((items != null && items.length > 0) && !Utils.areItemsValid(items)) || ((fluids != null && fluids.length > 0) && !Utils.areFluidsValid(fluids))) return null;

        Recipe r = LOOKUP.get(new RecipeInput(items, fluids));

        return r == null ? LOOKUP_TAG.find(new RecipeInput(items,fluids)) : r;
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
        Int2ObjectMap<Map.Entry<?, ?>> previousHashes = new Int2ObjectOpenHashMap<>();
        System.out.println("DUMP START");
        for (RecipeMap<?> map : AntimatterAPI.all(RecipeMap.class)) {
            previousHashes.clear();
            for (Map.Entry<?, ?> e : map.getRawMap().entrySet()) {
                if (previousHashes.containsKey(e.getKey().hashCode())) {
                    System.out.println("COLLISION:");
                    System.out.println("Map: " + map.getId());
                    System.out.println("Existing Hash: " + previousHashes.get(e.getKey().hashCode()).getKey().hashCode());
                    System.out.println("Existing Recipe: ");
                    System.out.println(previousHashes.get(e.getKey().hashCode()).getValue().toString());
                    System.out.println("Duplicate Hash: " + e.getKey().hashCode());
                    System.out.println("Duplicate Recipe: ");
                    System.out.println(e.getValue().toString());
                }
                previousHashes.put(e.getKey().hashCode(), e);
            }
        }
        System.out.println("DUMP END");
    }
}
