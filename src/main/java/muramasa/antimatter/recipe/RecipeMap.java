package muramasa.antimatter.recipe;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.integration.jei.renderer.InfoRenderers;
import muramasa.antimatter.integration.jei.renderer.InternalInfoRenderers;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.StackIngredient;
import muramasa.antimatter.recipe.ingredient.StackListIngredient;
import muramasa.antimatter.recipe.ingredient.TagIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.LazyHolder;
import muramasa.antimatter.util.Utils;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
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
        public final int id;

        public IngredientWrapper(int id, @Nonnull ItemStack stack) {
            this.source = stack;
            this.id = id;
            this.tags = stack.getItem().getTags();
        }

        public IngredientWrapper(int id, AntimatterIngredient ingredient) {
            this.source = null;
            this.id = id;
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

    public static class Proxy {
        public final IRecipeType loc;
        public final BiFunction<IRecipe, RecipeBuilder, Recipe> handler;

        public Proxy(IRecipeType loc, BiFunction<IRecipe, RecipeBuilder, Recipe> handler) {
            this.loc = loc;
            this.handler = handler;
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
    private LazyValue<IRecipeInfoRenderer> infoRenderer;
    @Nullable
    private GuiData GUI;
    //Root branch.
    protected final Branch LOOKUP = new Branch();
    @Nullable
    private Tier guiTier;

    private int ingredientCounter = 0;
    private static final RecipeFluids EMPTY_FLUIDS = new RecipeFluids(new FluidStack[0], Collections.emptySet());

    private final List<Recipe> RECIPES_TO_COMPILE = new ObjectArrayList<>();
    private Proxy PROXY;

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
    @OnlyIn(Dist.CLIENT)
    public IRecipeInfoRenderer getInfoRenderer() {
        return infoRenderer.getValue();
    }

    int getNextIngredientCount() {
        return ingredientCounter++;
    }

    private void initMap(Object[] data) {
        for (Object obj : data) {
            if (obj instanceof Tier) {
                guiTier = (Tier) obj;
            }
            if (!FMLEnvironment.dist.isDedicatedServer() && obj instanceof LazyValue) {
                this.infoRenderer = (LazyValue) obj;
            }
            if (obj instanceof GuiData) {
                this.GUI = (GuiData) obj;
            }
            if (obj instanceof Proxy) {
                this.PROXY = (Proxy) obj;
            }
        }
        if (!FMLEnvironment.dist.isDedicatedServer() && infoRenderer == null) {
            infoRenderer = InfoRenderers.DEFAULT_RENDERER;
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

    /**
     * Gets the set of recipes.
     * @param filterHidden whether or not to filter hidden recipes.
     * @return collection of recipes.
     */
    public Collection<Recipe> getRecipes(boolean filterHidden) {
        //Collectors.toSet is very important since there are duplicate recipes but they point to the same memory location
        //so == works to remove them.
        return LOOKUP.getRecipes(filterHidden).collect(Collectors.toSet());
    }

    public int uncompiledSize() {
        return RECIPES_TO_COMPILE.size();
    }
    //Adds a recipe to a map of fluids -> recipe. This just adds the recipe and errors if one is present.
    void addRecipeToMap(Map<RecipeFluids, Recipe> map, Recipe recipe) {
        RecipeFluids input = new RecipeFluids(recipe);
        Recipe old = map.putIfAbsent(input, recipe);
        if (old != null) {
            Utils.onInvalidData("RECIPE COLLISION AT RECIPE ADD: recipe with matching fluids, specials & items exist.");
        }
    }

    void add(Recipe recipe) {
        RECIPES_TO_COMPILE.add(recipe);
    }

    /**
     * Adds a recipe to this map. If the recipe is empty or collides with another recipe it is not added.
     * @param recipe the recipe to add.
     */
    void compileRecipe(Recipe recipe) {
        if (recipe == null) return;
        this.ingredientCounter = 0;
        Branch map = LOOKUP;
        if (!recipe.hasInputItems() || recipe.getInputItems().size() == 0) {
            //If a recipe doesn't have items it maps to the null key.
            if (!recipe.hasInputFluids()) return;
            FluidStack[] fluids = recipe.getInputFluids();
            fluids = fluids == null ? null : Arrays.stream(fluids).filter(t -> !t.isEmpty() || !(t.getFluid() == Fluids.EMPTY)).toArray(FluidStack[]::new);
            if (fluids == null || fluids.length == 0 || fluids[0].isEmpty()) return;
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
        //Default path
        } else if(recipe.getInputItems().size() > 0 || (recipe.getInputFluids() != null && recipe.getInputFluids().length > 0)) {
            //Any items are invalid?
            List<AntimatterIngredient> ing = recipe.compileInput();
            for (AntimatterIngredient ai : ing) {
                if (ai.getMatchingStacks().length == 0) {
                    //Utils.onInvalidData("RECIPE WITH EMPTY INGREDIENT");
                    return;
                }
                if (ai.getMatchingStacks().length == 1) {
                    ItemStack stack = ai.getMatchingStacks()[0];
                    //If the tag is empty.
                    if (stack.getItem() == Items.BARRIER) {
                        //Utils.onInvalidData("RECIPE WITH EMPTY INGREDIENT");
                        return;
                    }
                }
            }
            if (recipe.hasOutputItems()) {
                for (ItemStack stack : recipe.getOutputItems()) {
                    if (stack.isEmpty()) {
                        Utils.onInvalidData("RECIPE WITH EMPTY OUTPUT ITEM");
                        return;
                    }
                }
            }
            Recipe r = recurseItemTreeFind(new RecipeFluids(recipe.getInputFluids(), recipe.getTags()), ing.stream().map(t -> new IngredientWrapper(getNextIngredientCount(), t)).toArray(IngredientWrapper[]::new), map);
            if (r != null) {
                Utils.onInvalidData("RECIPE COLLISION! (Map: " + this.id + ")");
                return;
            }
            recurseItemTreeAdd(recipe, ing, map, 0, 0);
        } else {
            Utils.onInvalidData("EMPTY RECIPE - NOT ADDED");
        }
    }

    /**
     * Adds a recipe to the map. (recursive part)
     * @param recipe the recipe to add.
     * @param ingredients list of input ingredients.
     * @param map the current place in the recursion.
     * @param index where in the ingredients list we are.
     * @param count how many added already.
     */
    boolean recurseItemTreeAdd(Recipe recipe, List<AntimatterIngredient> ingredients, @Nonnull Branch map, int index, int count) {
        if (count >= ingredients.size()) return true;
        if (index >= ingredients.size()) {
            throw new RuntimeException("Index out of bounds for recurseItemTreeAdd, should not happen");
        }
        //Loop through NUMBER_OF_INGREDIENTS times.
        AntimatterIngredient current = ingredients.get(index);
        Either<Map<RecipeFluids, Recipe>, Branch> r;
        if (current instanceof StackListIngredient) {
            //have to build a path for each stack.
            int id = getNextIngredientCount();
            List<IngredientWrapper> wrappers = new ObjectArrayList<>();
            for (ItemStack stack : current.getMatchingStacks()) {
                AntimatterIngredient ing = AntimatterIngredient.of(stack).getValue();
                IngredientWrapper wr = new IngredientWrapper(id, ing);
                r = map.NODES.compute(wr, (k,v) -> callback(k,v, recipe, ingredients,count));
                wrappers.add(wr);
                if (count == ingredients.size() - 1) continue;
                if (r.left().isPresent()) {
                    Utils.onInvalidData("COLLISION DETECTED!");
                    wrappers.forEach(map.NODES::remove);
                    return false;
                }
                //should always be present but this gives no warning.
                if (r.right().map(m -> !recurseItemTreeAdd(recipe, ingredients, m, (index + 1) % ingredients.size(), count + 1)).orElse(false)) {
                    wrappers.forEach(map.NODES::remove);
                    return false;
                }
            }
            if (count == ingredients.size() - 1) return true;
        } else {
            IngredientWrapper wr = new IngredientWrapper(getNextIngredientCount(), current);
            r = map.NODES.compute(wr, (k,v) -> callback(k,v,recipe,ingredients,count));
            //Success. We are at the end, so we added recipe.
            if (count == ingredients.size() - 1) return true;
            if (r == null) {
                map.NODES.remove(wr);
                return false;
            }
            if (r.left().isPresent()) {
                Utils.onInvalidData("COLLISION DETECTED!");
                map.NODES.remove(wr);
                return false;
            }
            //should always be present but this gives no warning.
            if (r.right().map(m -> !recurseItemTreeAdd(recipe, ingredients, m, (index + 1) % ingredients.size(), count + 1)).orElse(false)) {
                map.NODES.remove(wr);
                return false;
            }
        }
        return true;
    }

    protected Either<Map<RecipeFluids, Recipe>, Branch> callback(IngredientWrapper k, Either<Map<RecipeFluids, Recipe>, Branch> v, Recipe recipe, List<AntimatterIngredient> ingredients, int count) {
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
                Utils.onInvalidData("Recipe collision, unable to add leaf. This means that a recipe is a subset of another.");
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
            Set<Integer> visited = new ObjectOpenHashSet<>();
            Recipe r = recurseItemTreeFind(input, items, map, i, 0, (1L << i), visited);
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
    Recipe recurseItemTreeFind(RecipeFluids input, IngredientWrapper[] items, @Nonnull Branch map, int index, int count, long skip, Set<Integer> visited) {
        if (count == items.length) return null;
        IngredientWrapper wr = items[index];
        if (visited.contains(wr.id)) return null;
        visited.add(index);
        Recipe r =  wr.computePaths(map, b -> getRecipe(input,items,index,count,skip,b, visited));
        if (r == null) visited.remove(index);
        return r;
    }

    /**
     * Just a callback if the next branch finds a nopde.
     * @param input fluids.
     * @param items list of item inputs.
     * @param index where we are in recursion.
     * @param count how many added.
     * @param skip which items to skip(bitmap)
     * @param next the next branch.
     * @return a possible recipe if found.
     */
    @Nullable
    private Recipe getRecipe(@Nonnull RecipeFluids input, @Nonnull IngredientWrapper[] items, int index, int count, long skip, @Nonnull Either<Map<RecipeFluids, Recipe>, Branch> next, Set<Integer> visited) {
        return next.map(recipeMap -> {
            Recipe r = recipeMap.get(input);
            return r == null ? recipeMap.get(EMPTY_FLUIDS) : r;
        }, branch -> {
            //Here, loop over all other items that are not currently part of the chain.
            //If we are at max index, loop back to 0.
            int counter = (index + 1) % items.length;
            while (counter != index) {
                //Have we already used this ingredient? If so, skip this one.
                if (((skip & (1L << counter)) == 0) || visited.contains(items[counter].id)) {
                    Recipe found = recurseItemTreeFind(input, items, branch, counter, count + 1, skip | (1L << counter), visited);
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
    public static ItemStack[] uniqueItems(ItemStack[] input) {
        List<ItemStack> list = new ObjectArrayList<>();
        loop:
        for (ItemStack item : input) {
            for (ItemStack obj : list) {
                if (item.equals(obj, false)) {
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
    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        long current = System.nanoTime();
        //First, check if items and fluids are valid.
        if ((items == null || items.length == 0) && (fluids == null || fluids.length == 0)) return null;
        if (((items != null && items.length > 0) && !Utils.areItemsValid(items)) /*|| ((fluids != null && fluids.length > 0) && !Utils.areFluidsValid(fluids))*/)
            return null;
        if (items != null && items.length > Long.SIZE) {
            Utils.onInvalidData("ERROR! TOO LARGE INPUT IN RECIPEMAP, time to fix a real bitmap. Probably never get this!");
            return null;
        }
        //Filter out empty fluids.
        fluids = fluids == null ? null : Arrays.stream(fluids).filter(t -> !t.isEmpty() || !(t.getFluid() == Fluids.EMPTY)).toArray(FluidStack[]::new);
        //Branch rootMap = LOOKUP.get(special);
        Branch rootMap = LOOKUP;
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
            ingredientCounter = 0;
            Recipe r = recurseItemTreeFind(new RecipeFluids(fluids, null), Arrays.stream(uniqueItems(items)).map(t -> new IngredientWrapper(getNextIngredientCount(), t)).toArray(IngredientWrapper[]::new), rootMap);
            Antimatter.LOGGER.info("Time to lookup (µs): " + (current / 1000));
            return r;
        }
    }

    @Nullable
    public Recipe find(LazyHolder<MachineItemHandler<?>> itemHandler, LazyHolder<MachineFluidHandler<?>> fluidHandler) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(new ItemStack[0]), fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
    }

    protected void reset() {
        this.LOOKUP.NODES.clear();
    }

    public void compile(RecipeManager reg) {
        reset();
        RECIPES_TO_COMPILE.forEach(this::compileRecipe);
        if (PROXY != null) {
            reset();
            List<IRecipe<?>> recipes = reg.getRecipesForType(PROXY.loc);
            recipes.stream().forEach(recipe -> {
                Recipe r = PROXY.handler.apply(recipe, RB());
                if (r != null) compileRecipe(r);
            });
        }
    }
}
