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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {

    private final Object2ObjectMap<RecipeInput, Recipe> LOOKUP;
    private final RecipeTagMap LOOKUP_TAG;

    private final String id;
    private final B builder;

    private IRecipeInfoRenderer infoRenderer;
    @Nullable
    private Tier guiTier;
    //Data allows you to set related data to the map, e.g. which tier the gui displays.
    public RecipeMap(String categoryId, B builder, Object ...data) {
        this.id = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
        LOOKUP = new Object2ObjectLinkedOpenHashMap<>();
        LOOKUP_TAG = new RecipeTagMap();
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
        if (filterHidden) return LOOKUP_TAG.LOOKUP_TAG.values().stream().filter(r -> !r.isHidden()).collect(Collectors.toList());
        return LOOKUP_TAG.getRecipes();
    }

    void add(Recipe recipe) {
        if (LOOKUP.containsKey(new RecipeInputFlat(recipe.getInputItems(), recipe.getInputFluids()))) {
            Utils.onInvalidData("Duplicate recipe detected!: " + recipe);
            return;
        }
        ItemStack[] mergedInput;

        if (recipe.hasTags()) {
            //Verify tags are non-empty.
            boolean invalid = Arrays.stream(recipe.getTagInputs()).anyMatch(tag -> tag.tag.getEntries().isEmpty());
            if (invalid) {
                Utils.onInvalidData("Tag recipe provided with empty tag - not added (avoiding exception!)" + recipe);
                return;
            }
            List<ItemStack> stack = Arrays.stream(recipe.getTagInputs()).map(t -> new ItemStack(t.tag.getRandomElement(Ref.RNG),1)).collect(Collectors.toList());
            if (recipe.getInputItems() != null) stack.addAll(Arrays.asList(recipe.getInputItems()));
            mergedInput = stack.toArray(new ItemStack[0]);
        } else {
            mergedInput = recipe.getInputItems();
        }
        if (mergedInput != null && LOOKUP_TAG.find(new RecipeTagMap.TagMapInput(mergedInput, recipe.getInputFluids())) != null) {
            Utils.onInvalidData("Duplicate recipe detected via TAGS!: " + recipe);
            return;
        }
        if (recipe.hasTags()) {
            LOOKUP_TAG.add(recipe);
        } else LOOKUP_TAG.addDefault(recipe);
    }

    @Nullable
    public Recipe find(@Nullable MachineItemHandler itemHandler, @Nullable MachineFluidHandler fluidHandler) {
        return find(itemHandler != null ? itemHandler.getInputs() : null, fluidHandler != null ? fluidHandler.getInputs() : null);
    }

    private ItemStack[] uniqueItems(ItemStack[] input) {
        ObjectArrayList<ItemStack> list = new ObjectArrayList<ItemStack>();
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
        Recipe r = LOOKUP_TAG.find(new RecipeInput(items, fluids));

        if (r == null) {
            //otherwise, fall back to complicated lookup.
            r = LOOKUP_TAG.find(new RecipeTagMap.TagMapInput(items,fluids));
        }
        return r;
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
