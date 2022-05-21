package muramasa.antimatter.recipe.map;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.integration.jei.renderer.InfoRenderers;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeUtil;
import muramasa.antimatter.recipe.ingredient.*;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeMap<B extends RecipeBuilder> implements ISharedAntimatterObject, IRecipeMap {

    private static final ItemStack[] EMPTY_ITEM = new ItemStack[0];
    private static final FluidStack[] EMPTY_FLUID = new FluidStack[0];

    private final ResourceLocation loc;
    private final B builder;
    private final Branch LOOKUP = new Branch();
    private final List<Recipe> RECIPES_TO_COMPILE = new ObjectArrayList<>();
    private final Set<AbstractMapIngredient> ROOT = new ObjectOpenHashSet<>();
    private final List<AbstractMapIngredient> ROOT_SPECIAL = new ObjectArrayList<>();

    @Nullable
    private GuiData GUI;
    @Nullable
    private Tier guiTier;
    @Nullable
    private Proxy PROXY;

    @Nullable
    private Object icon;

    @Environment(EnvType.CLIENT)
    private IRecipeInfoRenderer infoRenderer;

    // Data allows you to set related data to the map, e.g. which tier the gui
    // displays.
    public RecipeMap(String domain, String categoryId, B builder) {
        this.loc = new ResourceLocation(domain, categoryId);
        this.builder = builder;
        this.builder.setMap(this);
        AntimatterAPI.register(IRecipeMap.class, this);
    }

    // In the case of split stacks, merge the items, 2 aluminium dust in separate
    // stacks -> 1 stack with additive count.
    public static ItemStack[] uniqueItems(ItemStack[] input) {
        List<ItemStack> list = new ObjectArrayList<>(input.length);
        loop: for (ItemStack item : input) {
            for (ItemStack obj : list) {
                if (ItemStack.matches(item, obj)) {
                    obj.grow(item.getCount());
                    continue loop;
                }
            }
            // Add a copy here or it might mutate the stack.
            list.add(item.copy());
        }
        return list.toArray(new ItemStack[0]);
    }

    @Nullable
    public Tier getGuiTier() {
        return guiTier;
    }

    // Object can be an IDrawable or an ItemStack or an IItemProvider.
    public RecipeMap<B> setIcon(Object object) {
        this.icon = object;
        return this;
    }

    @Nullable
    public Object getIcon() {
        return this.icon;
    }

    @Nonnull
    @Environment(EnvType.CLIENT)
    public IRecipeInfoRenderer getInfoRenderer() {
        if (infoRenderer == null)
            return InfoRenderers.DEFAULT_RENDERER;
        return infoRenderer;
    }

    @Environment(EnvType.CLIENT)
    public void setInfoRenderer(IRecipeInfoRenderer renderer) {
        this.infoRenderer = renderer;
    }

    public RecipeMap<B> setGuiTier(Tier tier) {
        this.guiTier = tier;
        return this;
    }

    /**
     * Sets the gui data and overrides the data in JEI.
     *
     * @param gui the guidata.
     * @return this
     */
    public RecipeMap<B> setGuiData(GuiData gui) {
        this.GUI = gui;
        AntimatterAPI.registerJEICategory(this, this.GUI);
        return this;
    }

    /**
     * Sets the gui data and overrides the data in JEI.
     *
     * @param gui     the guidata.
     * @param machine the machine.
     * @return this
     */
    public RecipeMap<B> setGuiData(GuiData gui, Machine<?> machine) {
        this.GUI = gui;
        AntimatterAPI.registerJEICategory(this, this.GUI, machine, true);
        return this;
    }

    /**
     * Sets a proxy for this recipe map that is used to build recipes from other
     * maps.
     *
     * @param proxy the proxy.
     * @return this.
     */
    public RecipeMap<B> setProxy(Proxy proxy) {
        this.PROXY = proxy;
        return this;
    }

    @Nullable
    public GuiData getGui() {
        return GUI;
    }

    @Override
    public String getId() {
        return loc.getPath();
    }

    public B RB() {
        builder.clear();
        return builder;
    }

    /**
     * Gets the set of recipes.
     *
     * @param filterHidden whether or not to filter hidden recipes.
     * @return collection of recipes.
     */
    public Collection<Recipe> getRecipes(boolean filterHidden) {
        // Collectors.toSet is very important since there are duplicate recipes but they
        // point to the same memory location
        // so == works to remove them.
        // Or maybe not, I'm not sure but let's make a set anyways
        return LOOKUP.getRecipes(filterHidden).sorted(Comparator.comparingLong(Recipe::getPower))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void add(Recipe recipe) {
        RECIPES_TO_COMPILE.add(recipe);
    }

    /**
     * Adds a recipe to this map. If the recipe is empty it is not added. If it
     * collides with another recipe it is added to the collection and the first
     * recipe that matches a predicate is returned upon find.
     *
     * @param recipe the recipe to add.
     */
    public void compileRecipe(Recipe recipe) {
        if (recipe == null)
            return;

        if (recipe.hasOutputItems()) {
            for (ItemStack stack : recipe.getOutputItems()) {
                if (stack.isEmpty()) {
                    Utils.onInvalidData("RECIPE WITH EMPTY OUTPUT ITEM");
                    return;
                }
            }
        }
        boolean flag = false;
        if (recipe.hasInputItems()) {
            for (Ingredient inputItem : recipe.getInputItems()) {
                if (isIngredientSpecial(inputItem)) {
                    flag = true;
                    continue;
                }
                if (inputItem.isEmpty() || (inputItem.getItems().length == 1
                        && inputItem.getItems()[0].getItem() == Items.BARRIER)) {
                    Utils.onInvalidData("RECIPE WITH EMPTY INPUT (MAP): " + this.loc.getPath());
                    return;
                }
            }
        }
        if (flag) {
            recipe.sortInputItems();
        }
        List<List<AbstractMapIngredient>> items = fromRecipe(recipe, true);

        // Recipe r = recurseItemTreeFind(items, map, rr -> true);
        // if (r != null) {
        // Antimatter.LOGGER.warn("Recipe collision, adding both but only first is
        // available.");
        // }
        if (recurseItemTreeAdd(recipe, items, LOOKUP, 0, 0)) {
            items.forEach(t -> t.forEach(ing -> {
                if (!ing.isSpecial())
                    ROOT.add(ing);
                else
                    ROOT_SPECIAL.add(ing);
            }));
        }
    }

    protected void buildFromFluids(List<List<AbstractMapIngredient>> builder, List<FluidIngredient> ingredients,
                                   boolean insideMap) {
        for (FluidIngredient t : ingredients) {
            List<AbstractMapIngredient> inner = new ObjectArrayList<>(t.getStacks().length);
            for (FluidStack stack : t.getStacks()) {
                inner.add(new MapFluidIngredient(stack, insideMap));
            }
            builder.add(inner);
        }
    }

    protected void buildFromFluidStacks(List<List<AbstractMapIngredient>> builder, List<FluidStack> ingredients,
                                        boolean insideMap) {
        for (FluidStack t : ingredients) {
            builder.add(Collections.singletonList(new MapFluidIngredient(t, insideMap)));
        }
    }

    protected List<List<AbstractMapIngredient>> fromRecipe(Recipe r, boolean insideMap) {
        List<List<AbstractMapIngredient>> list = new ObjectArrayList<>(
                (r.hasInputItems() ? r.getInputItems().size() : 0)
                        + (r.hasInputFluids() ? r.getInputFluids().size() : 0));
        if (r.hasInputItems()) {
            buildFromItems(list, r.getInputItems(), insideMap);
        }
        if (r.hasInputFluids()) {
            buildFromFluids(list, r.getInputFluids(), insideMap);
        }
        return list;
    }

    protected void buildFromItems(List<List<AbstractMapIngredient>> list, List<Ingredient> ingredients,
             boolean insideMap) {
        for (Ingredient t : ingredients) {
            if (!isIngredientSpecial(t)) {
                Optional<ResourceLocation> rl = Optional.empty();//MapTagIngredient.findCommonTag(t, tags);
                if (rl.isPresent()) {
                    list.add(Collections.singletonList(new MapTagIngredient(rl.get(), insideMap)));
                } else {
                    List<AbstractMapIngredient> inner = new ObjectArrayList<>(t.getItems().length);
                    for (ItemStack stack : t.getItems()) {
                        if (t instanceof RecipeIngredient && ((RecipeIngredient)t).ignoreNbt()) {
                            inner.add(new MapItemIngredient(stack.getItem(), insideMap));
                        } else {
                            inner.add(new MapItemStackIngredient(stack, insideMap));
                        }
                    }
                    list.add(inner);
                }
            } else {
                list.add(Collections.singletonList(new SpecialIngredientWrapper(t)));
            }
        }
    }

    protected void buildFromItemStacks(List<List<AbstractMapIngredient>> list, ItemStack[] ingredients) {
        for (ItemStack t : ingredients) {
            int c = (int) t.getItem().builtInRegistryHolder().tags().count();
            List<AbstractMapIngredient> ls = new ObjectArrayList<>(2 + c);
            ls.add(new MapItemIngredient(t.getItem(), false));
            ls.add(new MapItemStackIngredient(t, false));
            /*for (ResourceLocation rl : t.getItem().getTags()) {
                ls.add(new MapTagIngredient(rl, false));
            }*/
            list.add(ls);
        }
    }

    /**
     * Adds a recipe to the map. (recursive part)
     *
     * @param recipe      the recipe to add.
     * @param ingredients list of input ingredients.
     * @param map         the current place in the recursion.
     * @param index       where in the ingredients list we are.
     * @param count       how many added already.
     */
    boolean recurseItemTreeAdd(@Nonnull Recipe recipe, @Nonnull List<List<AbstractMapIngredient>> ingredients,
            @Nonnull Branch map, int index, int count) {
        if (count >= ingredients.size())
            return true;
        if (index >= ingredients.size()) {
            throw new RuntimeException("Index out of bounds for recurseItemTreeAdd, should not happen");
        }
        // Loop through NUMBER_OF_INGREDIENTS times.
        List<AbstractMapIngredient> current = ingredients.get(index);
        Either<List<Recipe>, Branch> r;
        for (AbstractMapIngredient obj : current) {
            if (!obj.isSpecial()) {
                // Either add the recipe or create a branch.
                r = map.NODES.compute(obj, (k, v) -> {
                    if (count == ingredients.size() - 1) {
                        if (v == null) {
                            v = Either.left(new ObjectArrayList<>());
                        }
                        v.ifLeft(list -> list.add(recipe));
                        return v;
                    } else if (v == null) {
                        Branch traverse = new Branch();
                        v = Either.right(traverse);
                    }
                    return v;
                });
                // At the end, return.
                if (count == ingredients.size() - 1)
                    continue;
                // If left was present before. Shouldn't be needed?
                /*
                 * if (r.left().isPresent()) { Utils.onInvalidData("COLLISION DETECTED!");
                 * current.forEach(map.NODES::remove); return false; }
                 */
                // should always be present but this gives no warning.
                if (r.right().map(
                        m -> !recurseItemTreeAdd(recipe, ingredients, m, (index + 1) % ingredients.size(), count + 1))
                        .orElse(false)) {
                    current.forEach(map.NODES::remove);
                    return false;
                }
            } else {
                if (count == ingredients.size() - 1) {
                    map.SPECIAL_NODES.add(new Tuple<>(obj, Either.left(recipe)));
                } else {
                    Branch branch = new Branch();
                    boolean ok = recurseItemTreeAdd(recipe, ingredients, branch, (index + 1) % ingredients.size(),
                            count + 1);
                    if (!ok) {
                        current.forEach(map.NODES::remove);
                        return false;
                    } else {
                        map.SPECIAL_NODES.add(new Tuple<>(obj, Either.right(branch)));
                    }
                }
            }
        }
        return true;
    }

    /**
     * Recursively finds a recipe, top level. call this to find a recipe
     *
     * @param items the items part
     * @param map   the root branch to search from.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(@Nonnull List<List<AbstractMapIngredient>> items, @Nonnull Branch map,
            @Nonnull Predicate<Recipe> canHandle) {
        // Try each ingredient as a starting point, adding it to the skiplist.
        for (int i = 0; i < items.size(); i++) {
            Recipe r = recurseItemTreeFind(items, map, canHandle, i, 0, (1L << i));
            if (r != null)
                return r;
        }
        return null;
    }

    /**
     * Recursively finds a recipe
     *
     * @param items     the items part
     * @param map       the current branch of the tree
     * @param canHandle predicate to test found recipe.
     * @param index     the index of the wrapper to get
     * @param count     how deep we are in recursion, < items.length
     * @param skip      bitmap of items to skip, i.e. which items are used in the
     *                  recursion.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(@Nonnull List<List<AbstractMapIngredient>> items, @Nonnull Branch map,
            @Nonnull Predicate<Recipe> canHandle, int index, int count, long skip) {
        if (count == items.size())
            return null;
        List<AbstractMapIngredient> wr = items.get(index);
        // Iterate over current level of nodes.
        for (AbstractMapIngredient t : wr) {
            Either<List<Recipe>, Branch> result = map.NODES.get(t);
            if (result != null) {
                // Either return recipe or continue branch.
                Recipe r = result.map(left -> {
                    for (Recipe recipe : left) {
                        if (canHandle.test(recipe)) {
                            return recipe;
                        }
                    }
                    return null;
                }, right -> callback(items, right, canHandle, index, count, skip));
                if (r != null)
                    return r;
            }
            if (map.SPECIAL_NODES.size() > 0) {
                // Iterate over special nodes.
                for (Tuple<AbstractMapIngredient, Either<Recipe, Branch>> tuple : map.SPECIAL_NODES) {
                    AbstractMapIngredient special = tuple.getA();
                    if (special.equals(t)) {
                        return tuple.getB().map(r -> canHandle.test(r) ? r : null,
                                branch -> callback(items, branch, canHandle, index, count, skip));
                    }
                }
            }
        }
        return null;
    }

    private Recipe callback(@Nonnull List<List<AbstractMapIngredient>> items, @Nonnull Branch map,
            Predicate<Recipe> canHandle, int index, int count, long skip) {
        // We loop around items.size() if we reach the end.
        int counter = (index + 1) % items.size();
        while (counter != index) {
            // Have we already used this ingredient? If so, skip this one.
            if (((skip & (1L << counter)) == 0)) {
                // Recursive call.
                Recipe found = recurseItemTreeFind(items, map, canHandle, counter, count + 1, skip | (1L << counter));
                if (found != null)
                    return found;
            }
            counter = (counter + 1) % items.size();
        }
        return null;
    }

    @Nullable
    @Override
    public Recipe find(@Nonnull ItemStack[] items, @Nonnull FluidStack[] fluids, Tier tier, @Nonnull Predicate<Recipe> canHandle) {
        // First, check if items and fluids are valid.
        if (items.length + fluids.length > Long.SIZE) {
            Utils.onInvalidData(
                    "ERROR! TOO LARGE INPUT IN RECIPEMAP, time to fix a real bitmap. Probably never get this!");
            return null;
        }
        if (items.length == 0 && fluids.length == 0)
            return null;
        // Filter out empty fluids.

        // Build input.
        List<List<AbstractMapIngredient>> list = new ObjectArrayList<>(items.length + fluids.length);
        if (items.length > 0) {
            buildFromItemStacks(list, uniqueItems(items));
        }
        if (fluids.length > 0) {
            List<FluidStack> stack = new ObjectArrayList<>(fluids.length);
            for (FluidStack f : fluids) {
                if (!f.isEmpty())
                    stack.add(f);
            }
            if (stack.size() > 0)
                buildFromFluidStacks(list, stack, false);
        }
        if (list.size() == 0)
            return null;
        // Find recipe.
        // long current = System.nanoTime();
        Recipe r = recurseItemTreeFind(list, LOOKUP, canHandle);
        // Antimatter.LOGGER.info("Time to lookup (µs): " + ((System.nanoTime() -
        // current) / 1000));

        return r;
    }

    public void reset() {
        this.RECIPES_TO_COMPILE.clear();
    }

    public void resetCompiled() {
        this.getRecipes(false).forEach(Recipe::invalidate);
        this.LOOKUP.clear();
        this.ROOT.clear();
        this.ROOT_SPECIAL.clear();
    }

    public void compile(RecipeManager reg) {
        resetCompiled();
        if (RECIPES_TO_COMPILE.size() > 0) {
            // Recipes with special ingredients have to be compiled first as you cannot
            // verify that a special recipe collides with a regular, but the opposite works.
            List<Recipe> regular = new ObjectArrayList<>(RECIPES_TO_COMPILE.size());
            List<Recipe> special = new ObjectArrayList<>();
            for (Recipe recipe : RECIPES_TO_COMPILE) {
                if (recipe.hasSpecialIngredients()) {
                    special.add(recipe);
                } else {
                    regular.add(recipe);
                }
            }
            special.forEach(this::compileRecipe);
            regular.forEach(this::compileRecipe);
        }
        if (PROXY != null) {
            List<net.minecraft.world.item.crafting.Recipe<?>> recipes = (List<net.minecraft.world.item.crafting.Recipe<?>>) reg.getAllRecipesFor(PROXY.loc);
            recipes.forEach(recipe -> {
                Recipe r = PROXY.handler.apply(recipe, RB());
                if (r != null)
                    compileRecipe(r);
            });
        }

        this.LOOKUP.finish();
    }

    public boolean acceptsItem(ItemStack item) {
        MapItemStackIngredient i = new MapItemStackIngredient(item, false);
        MapItemIngredient j = new MapItemIngredient(item.getItem(), false);
        if (ROOT.contains(i))
            return true;
        if (ROOT.contains(j))
            return true;
        MapTagIngredient tag = new MapTagIngredient(null, false);
        return item.getItem().builtInRegistryHolder().tags().anyMatch(t -> {
            tag.setTag(t.location());
            if (ROOT.contains(tag)) {
                return true;
            }
            return ROOT_SPECIAL.contains(tag);
        });
    }

    public boolean acceptsFluid(FluidStack fluid) {
        MapFluidIngredient i = new MapFluidIngredient(fluid, false);
        if (ROOT.contains(i))
            return true;
        MapTagIngredient tag = new MapTagIngredient(null, false);
        return fluid.getFluid().builtInRegistryHolder().tags().anyMatch(t -> {
            tag.setTag(t.location());
            return ROOT.contains(tag);
        });
    }

    /**
     * Whether or not an ingredient is hashable.
     *
     * @param i ingredient
     * @return if it can be hashed.
     */
    public static boolean isIngredientSpecial(Ingredient i) {
        Class<? extends Ingredient> clazz = i.getClass();
        if (clazz == RecipeIngredient.class) return false;
        return /* i.getMatchingStacks().length == 0 && */(clazz != Ingredient.class && !RecipeUtil.isNBTIngredient(clazz)
        && !RecipeUtil.isCompoundIngredient(clazz));
    }

    /**
     * Static classes
     **/

    public static class Proxy {
        public final RecipeType loc;
        public final BiFunction<net.minecraft.world.item.crafting.Recipe<?>, RecipeBuilder, Recipe> handler;

        public Proxy(RecipeType<?> loc, BiFunction<net.minecraft.world.item.crafting.Recipe<?>, RecipeBuilder, Recipe> handler) {
            this.loc = loc;
            this.handler = handler;
        }
    }

    protected static class Branch {

        private Map<AbstractMapIngredient, Either<List<Recipe>, Branch>> NODES = new Object2ObjectOpenHashMap<>();

        private final List<Tuple<AbstractMapIngredient, Either<Recipe, Branch>>> SPECIAL_NODES = new ObjectArrayList<>();

        public Stream<Recipe> getRecipes(boolean filterHidden) {
            Stream<Recipe> stream = NODES.values().stream()
                    .flatMap(t -> t.map(Collection::stream, branch -> branch.getRecipes(filterHidden)));
            if (SPECIAL_NODES.size() > 0) {
                stream = Stream.concat(stream, SPECIAL_NODES.stream()
                        .flatMap(t -> t.getB().map(Stream::of, branch -> branch.getRecipes(filterHidden))));
            }
            if (filterHidden)
                stream = stream.filter(t -> !t.isHidden());
            return stream;
        }

        public void clear() {
            NODES = new Object2ObjectOpenHashMap<>();
            SPECIAL_NODES.clear();
        }

        public void finish() {
            // NODES.forEach((k,v) -> v.ifRight(Branch::finish));
            // this.NODES = ImmutableMap.<AbstractMapIngredient, Either<List<Recipe>,
            // Branch>>builder().putAll(NODES).build();
        }
    }
}
