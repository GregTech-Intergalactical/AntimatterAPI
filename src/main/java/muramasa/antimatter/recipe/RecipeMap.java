package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.integration.jei.renderer.RecipeInfoRenderer;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {

    private final Object2ObjectMap<RecipeInput, Recipe> LOOKUP;
    //private final RecipeTagMap LOOKUP_TAG;

    private final String id;
    private final B builder;

    private IRecipeInfoRenderer infoRenderer;

    //First, match the non-tagged items.
    public final Object2ObjectMap<RecipeInput, Recipe> LOOKUP_TAG = new Object2ObjectOpenHashMap<>();

    protected final Object2ObjectMap<ItemWrapper, List<Ingredient>> itemLookup = new Object2ObjectLinkedOpenHashMap<>();
    //Tags present, others are ignored.
    private final Set<ResourceLocation> tagsPresent = new ObjectOpenHashSet<>();
    //Set of items present.
    private final Set<AntimatterIngredient> itemsPresent = new ObjectOpenHashSet<>();

    @Nullable
    private Tier guiTier;
    //Data allows you to set related data to the map, e.g. which tier the gui displays.
    public RecipeMap(String categoryId, B builder, Object ...data) {
        this.id = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
        LOOKUP = new Object2ObjectLinkedOpenHashMap<>();
        initMap(data);
        AntimatterAPI.register(RecipeMap.class, this);
    }
    @Nullable
    public Tier getGuiTier() {
        return guiTier;
    }

    @Nonnull
    public IRecipeInfoRenderer getInfoRenderer() {
        return infoRenderer;
    }

    private void initMap(Object[] data) {
        for (Object obj : data) {
            if (obj instanceof Tier) {
                guiTier = (Tier) obj;
            }
            if (obj instanceof IRecipeInfoRenderer) {
                this.infoRenderer = (IRecipeInfoRenderer) obj;
            }
        }
        if (this.infoRenderer == null) {
            //Default.
            this.infoRenderer = RecipeInfoRenderer.INSTANCE;
        }
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
        if (filterHidden) return LOOKUP_TAG.values().stream().filter(r -> !r.isHidden()).collect(Collectors.toList());
        return LOOKUP_TAG.values();
    }

    void add(Recipe recipe) {
        RecipeInput input = new RecipeInput(recipe.getInputItems(), recipe.getInputFluids(), recipe.getTags());
        /*
        Sanity check, no input item can match the tag inputs, or it is undefined behaviour. Tags.length > 0 here.
         */
        if (recipe.hasInputItems()) {
            for (AntimatterIngredient inputItem : input.items) {
                if (recipe.getTaggedInput().anyMatch(t -> Arrays.stream(inputItem.getMatchingStacks()).anyMatch(t))) {
                    Utils.onInvalidData("INVALID RECIPE! Item added that is also a part of the tags of the recipe.");
                    return;
                }
            }
        }
        long code = recipe.getTagHash();
        recipe.getTaggedInput().forEach(t -> tagsPresent.add(t.tag.getId()));
        if (recipe.hasInputItems()) {
            recipe.getStandardInput().forEach(t -> Arrays.stream(t.getMatchingStacks()).forEach(stack -> itemsPresent.add(AntimatterIngredient.fromStack(stack))));
        }
        input.accumulatedTagHash = code;
        if (LOOKUP_TAG.get(input) != null) {
            Utils.onInvalidData("INVALID RECIPE! Other recipe matched");
        } else {
            LOOKUP_TAG.put(input, recipe);
        }
    }

    @Nullable
    public Recipe find(@Nullable MachineItemHandler<?> itemHandler, @Nullable MachineFluidHandler<?> fluidHandler) {
        return find(itemHandler != null ? itemHandler.getInputs() : null, fluidHandler != null ? fluidHandler.getInputs() : null);
    }

    private ItemStack[] uniqueItems(ItemStack[] input) {
        ObjectArrayList<ItemStack> list = new ObjectArrayList<>();
        loop : for (ItemStack item : input) {
           for (ItemStack obj : list) {
               if (Utils.equals(item,obj)) {
                   obj.grow(item.getCount());
                   continue loop;
               }
           }
           //Add a copy here
           list.add(item.copy());
        }
        return list.toArray(new ItemStack[0]);
    }

    @Nullable
    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        //some checks.
        if ((items == null || items.length == 0) && (fluids == null || fluids.length == 0)) return null;
        if (((items != null && items.length > 0) && !Utils.areItemsValid(items)) || ((fluids != null && fluids.length > 0) && !Utils.areFluidsValid(fluids))) return null;
        //merge stacks here, just combine identical stacks into one larger to simplify lookup. Stacks are copied here in case a change is made.
        items = uniqueItems(items);
        //easy, flat input lookup. recipes without tags are detected here. If a recipe has exactly the input as above the recipe is found here.
        RecipeInput input = new RecipeInput(items,fluids,Collections.emptySet(), this.tagsPresent, this.itemsPresent);

        return find(input);
    }

    public Recipe find(long tier, @Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        Recipe r = find(items, fluids);
        return r.getPower() <= tier ? r : null;
    }

    public Recipe find(Tier tier, @Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        return find(tier.getVoltage(), items, fluids);
    }

    protected Recipe find(RecipeInput input) {
        return recursiveHash(input,input.allInputTags, 0,0,0);
    }

    /**
     * Recursively finds a recipe.
     * @param input the input recipe.
     * @param arrayList all the items tags.
     * @param element current element in the list.
     * @param acc accumulated hash.
     * @param whichNonTagged bitmap of items to ignore tags at current level.
     * @return a found recipe.
     */
    private Recipe recursiveHash(RecipeInput input, List<Set<ResourceLocation>> arrayList, int element, long acc, long whichNonTagged) {
        if (element > arrayList.size()) {
            return null;
        }
        for (ListIterator<Set<ResourceLocation>> it = arrayList.listIterator(element); it.hasNext(); ) {
            Set<ResourceLocation> tags = it.next();
            for (ResourceLocation r : tags) {
                if (!this.tagsPresent.contains(r)) {
                    continue;
                }
                Recipe ok = recursiveHash(input,arrayList, element + 1, acc + r.hashCode(),whichNonTagged);
                if (ok != null) {
                    return ok;
                }
            }
        }
        Recipe ok = recursiveHash(input,arrayList, element + 1, acc, whichNonTagged | (1 << element));
        if (ok != null) {
            return ok;
        }
        input.rehash(whichNonTagged);
        input.accumulatedTagHash = acc;
        return LOOKUP_TAG.get(input);
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
