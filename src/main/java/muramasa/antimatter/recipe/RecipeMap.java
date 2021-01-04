package muramasa.antimatter.recipe;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.integration.jei.renderer.RecipeInfoRenderer;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.StackIngredient;
import muramasa.antimatter.recipe.ingredient.TagIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.LazyHolder;
import muramasa.antimatter.util.Utils;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {
    //A fairly complicated and ugly class, but necessary since you cannot create ingredients on the fly.
    protected static class IngredientWrapper {

        public ItemStack source;
        public Set<ResourceLocation> tags;

        public ResourceLocation tagToHash;

        protected AntimatterIngredient ing;

        public IngredientWrapper(@Nonnull ItemStack stack) {
            this.source = stack;
            this.tags = stack.getItem().getTags();//stack.getItem().getTags().stream().map(t -> AntimatterIngredient.fromTag(new ItemTags.Wrapper(t), stack.getCount())).toArray(AntimatterIngredient[]::new);
        }

        public IngredientWrapper(AntimatterIngredient ingredient) {
            this.source = null;
            this.ing = ingredient;
        }

        public Recipe computePaths(RecipeMap.Branch choices, Function<Either<Map<RecipeFluids, Recipe>, Branch>, Recipe> callback) {
            Recipe r;
            tagToHash = null;
            Either<Map<RecipeFluids, Recipe>, RecipeMap.Branch> output = choices.NODES.get(this);
            if (output != null) {
                r = callback.apply(output);
                if (r != null) {
                    return r;
                }
            }
            if (tags != null) {
                for (ResourceLocation tag : tags) {
                    tagToHash = tag;
                    output = choices.NODES.get(this);
                    if (output != null) {
                        r = callback.apply(output);
                        if (r != null) {
                            return r;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            if (tagToHash != null) return tagToHash.hashCode();
            if (source != null) {
                return AntimatterIngredient.itemHash(source);
            } else {
                return ing.hashCode();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof IngredientWrapper)) return false;
            return testIngredient(((IngredientWrapper) o).ing) && this.count() >= ((IngredientWrapper) o).count();
        }

        public int count() {
            return source != null ? source.getCount() : ing.count;
        }

        protected boolean testIngredient(AntimatterIngredient toCompare) {
            if (tagToHash != null) {
                /* Compare with the hash. */
                return toCompare.testTag(tagToHash);
            } else if (source != null) {
                return toCompare.test(source);
            } else if (ing != null) {
                if (ing instanceof StackIngredient) {
                    return toCompare.test(((StackIngredient) ing).getStack());
                }
                if (ing instanceof TagIngredient) {
                    return toCompare.testTag(((TagIngredient)ing).getTag());
                } else {
                    for (ItemStack stack : ing.getMatchingStacks()) {
                        if (toCompare.test(stack)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }


    protected static class Branch {

        private final Object2ObjectMap<IngredientWrapper, Either<Map<RecipeFluids, Recipe>, Branch>> NODES = new Object2ObjectOpenHashMap<>();

        public Stream<Recipe> getRecipes(boolean filterHidden) {
            Stream<Recipe> stream = NODES.values().stream().flatMap(t -> t.map(
                    recipeMap -> recipeMap.values().stream(),
                    branch -> branch.getRecipes(filterHidden)
            ));
            if (filterHidden) stream = stream.filter(t -> !t.isHidden());
            return stream;
        }
    }

    private final String id;
    private final B builder;
    private IRecipeInfoRenderer infoRenderer = RecipeInfoRenderer.INSTANCE;
    @Nullable
    private GuiData GUI;
    //List of special -> Branch, where Branch represent a node, either another traversal or a recipe.
    protected final Int2ObjectMap<Branch> LOOKUP = new Int2ObjectOpenHashMap<>();
    @Nullable
    private Tier guiTier;

    //Data allows you to set related data to the map, e.g. which tier the gui displays.
    public RecipeMap(String categoryId, B builder, Object... data) {
        this.id = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
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
            if (obj instanceof GuiData) {
                this.GUI = (GuiData) obj;
            }
        }
    }
    @Nullable
    public GuiData getGui() {
        return GUI;
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

    public Collection<Recipe> getRecipes(boolean filterHidden) {
        //Collectors.toSet is very important since there are duplicate recipes but they point to the same memory location
        //so == works to remove them.
        return LOOKUP.values().stream().flatMap(t -> t.getRecipes(filterHidden)).collect(Collectors.toSet());
    }

    //Adds a recipe to a map of fluids -> recipe. This just adds the recipe and errors if one is present.
    void addRecipeToMap(Map<RecipeFluids, Recipe> map, Recipe recipe) {
        RecipeFluids input = new RecipeFluids(recipe);
        Recipe old = map.putIfAbsent(input, recipe);
        if (old != null) {
            Utils.onInvalidData("RECIPE COLLISION AT RECIPE ADD: recipe with matching fluids, specials & items exist.");
        }
    }

    /**
     * Adds a recipe to this map. If the recipe is empty or collides with another recipe it is not added.
     * @param recipe the recipe to add.
     */
    void add(Recipe recipe) {
        if (recipe == null) return;
      //  Branch map = LOOKUP.computeIfAbsent(recipe.getSpecialValue(), t -> new Branch());
        Branch map = LOOKUP.computeIfAbsent(0, t -> new Branch());
        if (!recipe.hasInputItems() || recipe.getInputItems().size() == 0) {
            //If a recipe doesn't have items it maps to the null key.
            map.NODES.compute(null, (k, v) -> {
                if (v == null) {
                    v = Either.left(new Object2ObjectOpenHashMap<>());
                } else if (v.right().isPresent()) {
                    //Shouldn't happen.
                    Utils.onInvalidData("RECIPE COLLISION (NO ITEMS, ONLY FLUIDS)!");
                    return v;
                }
                v.left().ifPresent(t -> addRecipeToMap(t, recipe));
                return v;
            });
            //inputitems are not null
        } else if(recipe.getInputItems().size() > 0 || (recipe.getInputFluids() != null && recipe.getInputFluids().length > 0)) {
            for (AntimatterIngredient ai : recipe.getInputItems()) {
                if (ai.hasNoMatchingItems()) {
                    Utils.onInvalidData("RECIPE WITH EMPTY INGREDIENT");
                    return;
                }
            }
            Recipe r = recurseItemTreeFind(new RecipeFluids(recipe.getInputFluids(), recipe.getTags()), recipe.getInputItems().stream().map(IngredientWrapper::new).toArray(IngredientWrapper[]::new), map);
            if (r != null) {
                Utils.onInvalidData("RECIPE COLLISION!");
                return;
            }
            recurseItemTreeAdd(recipe, recipe.getInputItems(), map, 0, 0);
        } else {
            Utils.onInvalidData("EMPTY RECIPE - NOT ADDED");
        }
    }

    void recurseItemTreeAdd(Recipe recipe, List<AntimatterIngredient> ingredients, @Nonnull Branch map, int index, int count) {
        if (count >= ingredients.size()) return;
        if (index >= ingredients.size()) {
            throw new RuntimeException("Index out of bounds for recurseItemTreeAdd, should not happen");
        }
        //Loop through NUMBER_OF_INGREDIENTS times.
        AntimatterIngredient current = ingredients.get(index);
        Either<Map<RecipeFluids, Recipe>, Branch> r = map.NODES.compute(new IngredientWrapper(current), (k, v) -> {
            //Reached the end, so add the recipe. Create a leaf.
            if (count == ingredients.size() - 1) {
                Map<RecipeFluids, Recipe> rec;
                //No other recipes at this level, create new map.
                if (v == null) {
                    rec = new Object2ObjectOpenHashMap<>();
                    v = Either.left(rec);
                } else if (v.left().isPresent()) {
                    rec = v.left().get();
                } else {
                    Utils.onInvalidData("COULD NOT ADD RECIPE!");
                    return null;
                }
                addRecipeToMap(rec, recipe);
                return v;
            } else if (v == null) {
                //Not at the end, create a new branch to continue.
                Branch traverse = new Branch();
                v = Either.right(traverse);
            }
            return v;
        });
        //Success. We are at the end, so we added recipe.
        if (count == ingredients.size() - 1) return;
        if (r == null) return;

        if (r.left().isPresent()) {
            Utils.onInvalidData("COLLISION DETECTED!");
            return;
        }
        //should always be present but this gives no warning.
        r.right().ifPresent(m -> recurseItemTreeAdd(recipe, ingredients, m, (index + 1) % ingredients.size(), count + 1));
    }

    /**
     * Recursively finds a recipe, top level. call this to find a recipe
     * @param input the fluids part
     * @param items the items part
     * @param map the root branch to search from.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(RecipeFluids input, IngredientWrapper[] items, @Nonnull Branch map) {
        for (int i = 0; i < items.length; i++) {
            Recipe r = recurseItemTreeFind(input, items, map, i, 0, (1L << i));
            if (r != null) return r;
        }
        return null;
    }
    /**
     * Recursively finds a recipe
     * @param input the fluids part
     * @param items the items part
     * @param map the current branch of the tree
     * @param index the index of the wrapper to get
     * @param count how deep we are in recursion, < items.length
     * @param skip bitmap of items to skip, i.e. which items are used in the recursion.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(RecipeFluids input, IngredientWrapper[] items, @Nonnull Branch map, int index, int count, long skip) {
        if (count == items.length) return null;
        IngredientWrapper wr = items[index];
        return wr.computePaths(map, b -> getRecipe(input,items,index,count,skip,b));
    }

    @Nullable
    private Recipe getRecipe(@Nonnull RecipeFluids input, @Nonnull IngredientWrapper[] items, int index, int count, long skip, @Nonnull Either<Map<RecipeFluids, Recipe>, Branch> next) {
        return next.map(recipeMap -> {
            Recipe r = recipeMap.get(input);
            return r == null ? recipeMap.get(null) : r;
        }, branch -> {
            //Here, loop over all other items that are not currently part of the chain.
            int counter = (index + 1) % items.length;
            while (counter != index/*&& !state.shouldSkip(counter)*/) {
                if (((skip & (1L << counter)) == 0)) {
                    Recipe found = recurseItemTreeFind(input, items, branch, counter, count + 1, skip | (1L << counter));
                    if (found != null) return found;
                }
                counter = (counter + 1) % items.length;
            }
            return null;
        });
    }

    @Nullable
    public Recipe find(@Nonnull LazyOptional<MachineItemHandler<?>> itemHandler, @Nonnull LazyOptional<MachineFluidHandler<?>> fluidHandler) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(new ItemStack[0]), fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
    }

    //In the case of split stacks, merge the items, 2 aluminium dust in separate stacks -> 1 stack with additive count.
    private ItemStack[] uniqueItems(ItemStack[] input) {
        ObjectArrayList<ItemStack> list = new ObjectArrayList<>();
        loop:
        for (ItemStack item : input) {
            for (ItemStack obj : list) {
                if (Utils.equals(item, obj)) {
                    obj.grow(item.getCount());
                    continue loop;
                }
            }
            //Add a copy here or it might mutate the stack.
            list.add(item.copy());
        }
        return list.toArray(new ItemStack[0]);
    }

    @Nullable
    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids, int special) {
        long current = System.nanoTime();
        //First, check if items and fluids are valid.

        if ((items == null || items.length == 0) && (fluids == null || fluids.length == 0)) return null;
        if (((items != null && items.length > 0) && !Utils.areItemsValid(items)) /*|| ((fluids != null && fluids.length > 0) && !Utils.areFluidsValid(fluids))*/)
            return null;
        if (items != null && items.length > Long.SIZE) {
            Utils.onInvalidData("ERROR! TOO LARGE INPUT IN RECIPEMAP, time to fix a real bitmap");
            return null;
        }
        //Filter out empty fluids.
        fluids = fluids == null ? null : Arrays.stream(fluids).filter(t -> !t.isEmpty() || !(t.getFluid() == Fluids.EMPTY)).toArray(FluidStack[]::new);
        //Branch rootMap = LOOKUP.get(special);
        Branch rootMap = LOOKUP.get(0);
        if (rootMap == null) return null;

        if (items == null || items.length == 0) {
            //No items, check root level only.
            Either<Map<RecipeFluids, Recipe>, Branch> r = rootMap.NODES.get(null);
            if (r != null) {
                FluidStack[] finalFluids = fluids;
                return r.left().map(t -> t.get(new RecipeFluids(finalFluids, null))).orElse(null);
            } else {
                return null;
            }
        } else {
            current = System.nanoTime() - current;
            Recipe r = recurseItemTreeFind(new RecipeFluids(fluids, null), Arrays.stream(uniqueItems(items)).map(IngredientWrapper::new).toArray(IngredientWrapper[]::new), rootMap);
            Antimatter.LOGGER.info("Time to lookup (µs): " + (current / 1000));
            return r;
        }
    }

    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        return find(items, fluids, 0);
    }

    @Nullable
    public Recipe find(long tier, LazyHolder<MachineItemHandler<?>> itemHandler, LazyHolder<MachineFluidHandler<?>> fluidHandler) {
        Recipe r = find(itemHandler.map(MachineItemHandler::getInputs).orElse(new ItemStack[0]), fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
        return r != null && r.getPower() <= tier ? r : null;
    }

    @Nullable
    public Recipe find(Tier tier, LazyHolder<MachineItemHandler<?>> itemHandler, LazyHolder<MachineFluidHandler<?>> fluidHandler) {
        return find(tier.getVoltage(), itemHandler, fluidHandler);
    }

    @Nullable
    public Recipe find(long tier, @Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        Recipe r = find(items, fluids);
        return r != null && r.getPower() <= tier ? r : null;
    }

    @Nullable
    public Recipe find(Tier tier, @Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        return find(tier.getVoltage(), items, fluids);
    }

    public void reset() {
        this.LOOKUP.clear();
    }

}
