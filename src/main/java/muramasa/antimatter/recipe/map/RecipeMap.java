package muramasa.antimatter.recipe.map;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.integration.jei.renderer.InfoRenderers;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.*;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.LazyHolder;
import muramasa.antimatter.util.Utils;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {

    private static final ItemStack[] EMPTY_ITEM = new ItemStack[0];
    private static final FluidStack[] EMPTY_FLUID = new FluidStack[0];

    private final String id;
    private final B builder;
    private final Branch LOOKUP = new Branch();
    private final List<Recipe> RECIPES_TO_COMPILE = new ObjectArrayList<>();

    @Nullable
    private GuiData GUI;
    @Nullable
    private Tier guiTier;
    @Nullable
    private Proxy PROXY;

    @OnlyIn(Dist.CLIENT)
    private IRecipeInfoRenderer infoRenderer;

    //Data allows you to set related data to the map, e.g. which tier the gui displays.
    public RecipeMap(String categoryId, B builder, Object... data) {
        this.id = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
        initMap(data);
        AntimatterAPI.register(RecipeMap.class, this);
    }

    //In the case of split stacks, merge the items, 2 aluminium dust in separate stacks -> 1 stack with additive count.
    public static ItemStack[] uniqueItems(ItemStack[] input) {
        List<ItemStack> list = new ObjectArrayList<>(input.length);
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
    public Tier getGuiTier() {
        return guiTier;
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public IRecipeInfoRenderer getInfoRenderer() {
        if (infoRenderer == null) return InfoRenderers.DEFAULT_RENDERER;
        return infoRenderer;
    }

    @OnlyIn(Dist.CLIENT)
    public void setInfoRenderer(IRecipeInfoRenderer renderer) {
        this.infoRenderer = renderer;
    }

    private void initMap(Object[] data) {
        for (Object obj : data) {
            if (obj instanceof Tier) {
                guiTier = (Tier) obj;
            }
            if (obj instanceof GuiData) {
                this.GUI = (GuiData) obj;
            }
            if (obj instanceof Proxy) {
                this.PROXY = (Proxy) obj;
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
        //Collectors.toSet is very important since there are duplicate recipes but they point to the same memory location
        //so == works to remove them.
        //Or maybe not, I'm not sure but let's make a set anyways
        return LOOKUP.getRecipes(filterHidden).collect(Collectors.toSet());
    }

    void add(Recipe recipe) {
        RECIPES_TO_COMPILE.add(recipe);
    }

    /**
     * Adds a recipe to this map. If the recipe is empty or collides with another recipe it is not added.
     *
     * @param recipe the recipe to add.
     */
    public void compileRecipe(Recipe recipe, Function<Item, Collection<ResourceLocation>> tagGetter) {
        if (recipe == null) return;
        Branch map = LOOKUP;

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
            for (RecipeIngredient inputItem : recipe.getInputItems()) {
                if (isIngredientSpecial(inputItem.get())) {
                    flag = true;
                    continue;
                }
                if (inputItem.get().hasNoMatchingItems() || (inputItem.get().getMatchingStacks().length == 1 && inputItem.get().getMatchingStacks()[0].getItem() == Items.BARRIER)) {
                    //Utils.onInvalidData("RECIPE WITH EM");
                    return;
                }
            }
        }
        if (flag) {
            recipe.sortInputItems();
        }
        List<List<AbstractMapIngredient>> items = fromRecipe(recipe, tagGetter);

        Recipe r = recurseItemTreeFind(items, map, rr -> true);
        if (r != null) {
            Utils.onInvalidData("RECIPE COLLISION! (Map: " + this.id + ")");
            return;
        }
        recurseItemTreeAdd(recipe, items, map, 0, 0);
    }

    protected List<List<AbstractMapIngredient>> buildFromFluids(List<FluidStack> ingredients) {
        if (ingredients.size() == 0) return new ObjectArrayList<>();
        List<List<AbstractMapIngredient>> ret = new ObjectArrayList<>(ingredients.size());
        for (FluidStack t : ingredients) {
            List<AbstractMapIngredient> inner = new ObjectArrayList<>(1 + t.getFluid().getTags().size());
            inner.add(new MapFluidIngredient(t));
            for (ResourceLocation rl : t.getFluid().getTags()) {
                inner.add(new MapTagIngredient(rl));
            }
            ret.add(inner);
        }
        return ret;
    }

    protected List<List<AbstractMapIngredient>> fromRecipe(Recipe r, Function<Item, Collection<ResourceLocation>> tagGetter) {
        List<List<AbstractMapIngredient>> items = r.hasInputItems() ? buildFromItems(r.getInputItems().stream().map(RecipeIngredient::get).collect(Collectors.toList()), tagGetter) : new ObjectArrayList<>();
        if (r.hasInputFluids()) items.addAll(buildFromFluids(Arrays.asList(r.getInputFluids())));
        return items;
    }

    protected List<List<AbstractMapIngredient>> buildFromItems(List<Ingredient> ingredients, Function<Item, Collection<ResourceLocation>> tagGetter) {
        List<List<AbstractMapIngredient>> ret = new ObjectArrayList<>(ingredients.size());
        for (Ingredient t : ingredients) {
            if (!isIngredientSpecial(t)) {
                Optional<ResourceLocation> rl = MapTagIngredient.findCommonTag(t, tagGetter);
                if (rl.isPresent()) {
                    ret.add(Collections.singletonList(new MapTagIngredient(rl.get())));
                } else {
                    List<AbstractMapIngredient> inner = new ObjectArrayList<>(t.getMatchingStacks().length);
                    for (ItemStack stack : t.getMatchingStacks()) {
                        inner.add(new MapItemIngredient(stack));
                    }
                    ret.add(inner);
                }
            } else {
                ret.add(Collections.singletonList(new SpecialIngredientWrapper(t)));
            }
        }
        return ret;
    }

    protected List<List<AbstractMapIngredient>> buildFromItemStacks(List<ItemStack> ingredients) {
        if (ingredients.size() == 0) return new ObjectArrayList<>();
        List<List<AbstractMapIngredient>> ret = new ObjectArrayList<>(ingredients.size());
        for (ItemStack t : ingredients) {
            List<AbstractMapIngredient> ls = new ObjectArrayList<>(1 + t.getItem().getTags().size());
            ls.add(new MapItemIngredient(t));
            for (ResourceLocation rl : t.getItem().getTags()) {
                ls.add(new MapTagIngredient(rl));
            }
            ret.add(ls);
        }
        return ret;
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
    boolean recurseItemTreeAdd(@Nonnull Recipe recipe, @Nonnull List<List<AbstractMapIngredient>> ingredients, @Nonnull Branch map, int index, int count) {
        if (count >= ingredients.size()) return true;
        if (index >= ingredients.size()) {
            throw new RuntimeException("Index out of bounds for recurseItemTreeAdd, should not happen");
        }
        //Loop through NUMBER_OF_INGREDIENTS times.
        List<AbstractMapIngredient> current = ingredients.get(index);
        Either<Recipe, Branch> r;
        for (AbstractMapIngredient obj : current) {
            if (!obj.isSpecial()) {
                //Either add the recipe or create a branch.
                r = map.NODES.compute(obj, (k, v) -> {
                    if (count == ingredients.size() - 1) {
                        v = Either.left(recipe);
                        return v;
                    } else if (v == null) {
                        Branch traverse = new Branch();
                        v = Either.right(traverse);
                    }
                    return v;
                });
                //At the end, return.
                if (count == ingredients.size() - 1) continue;
                //If left was present before. Shouldn't be needed?
                /*if (r.left().isPresent()) {
                    Utils.onInvalidData("COLLISION DETECTED!");
                    current.forEach(map.NODES::remove);
                    return false;
                }*/
                //should always be present but this gives no warning.
                if (r.right().map(m -> !recurseItemTreeAdd(recipe, ingredients, m, (index + 1) % ingredients.size(), count + 1)).orElse(false)) {
                    current.forEach(map.NODES::remove);
                    return false;
                }
            } else {
                if (count == ingredients.size() - 1) {
                    map.SPECIAL_NODES.add(new Tuple<>(obj, Either.left(recipe)));
                } else {
                    Branch branch = new Branch();
                    boolean ok = recurseItemTreeAdd(recipe, ingredients, branch, (index + 1) % ingredients.size(), count + 1);
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
    Recipe recurseItemTreeFind(@Nonnull List<List<AbstractMapIngredient>> items, @Nonnull Branch map, @Nonnull Predicate<Recipe> canHandle) {
        //Try each ingredient as a starting point, adding it to the skiplist.
        for (int i = 0; i < items.size(); i++) {
            Recipe r = recurseItemTreeFind(items, map, canHandle, i, 0, (1L << i));
            if (r != null) return r;
        }
        return null;
    }

    /**
     * Recursively finds a recipe
     *
     * @param items the items part
     * @param map   the current branch of the tree
     * @param index the index of the wrapper to get
     * @param count how deep we are in recursion, < items.length
     * @param skip  bitmap of items to skip, i.e. which items are used in the recursion.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(@Nonnull List<List<AbstractMapIngredient>> items, @Nonnull Branch map, @Nonnull Predicate<Recipe> canHandle, int index, int count, long skip) {
        if (count == items.size()) return null;
        List<AbstractMapIngredient> wr = items.get(index);
        //Iterate over current level of nodes.
        for (AbstractMapIngredient t : wr) {
            Either<Recipe, RecipeMap.Branch> result = map.NODES.get(t);
            if (result != null) {
                //Either return recipe or continue branch.
                Recipe r = result.map(left -> canHandle.test(left) ? left : null, right -> callback(items, right, canHandle, index, count, skip));
                if (r != null && canHandle.test(r)) return r;
            }
            if (map.SPECIAL_NODES.size() > 0) {
                //Iterate over special nodes.
                for (Tuple<AbstractMapIngredient, Either<Recipe, Branch>> tuple : map.SPECIAL_NODES) {
                    AbstractMapIngredient special = tuple.getA();
                    if (special.equals(t)) {
                        return tuple.getB().map(r -> canHandle.test(r) ? r : null, branch -> callback(items, branch, canHandle, index, count, skip));
                    }
                }
            }
        }
        return null;
    }

    private Recipe callback(@Nonnull List<List<AbstractMapIngredient>> items, @Nonnull Branch map, Predicate<Recipe> canHandle, int index, int count, long skip) {
        //We loop around items.size() if we reach the end.
        int counter = (index + 1) % items.size();
        while (counter != index) {
            //Have we already used this ingredient? If so, skip this one.
            if (((skip & (1L << counter)) == 0)) {
                //Recursive call.
                Recipe found = recurseItemTreeFind(items, map, canHandle, counter, count + 1, skip | (1L << counter));
                if (found != null) return found;
            }
            counter = (counter + 1) % items.size();
        }
        return null;
    }

    @Nullable
    public Recipe find(@Nonnull LazyOptional<MachineItemHandler<?>> itemHandler, @Nonnull LazyOptional<MachineFluidHandler<?>> fluidHandler, Predicate<Recipe> validator) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(EMPTY_ITEM), fluidHandler.map(MachineFluidHandler::getInputs).orElse(EMPTY_FLUID), validator);
    }

    @Nullable
    public Recipe find(@Nonnull ItemStack[] items, @Nonnull FluidStack[] fluids, @Nonnull Predicate<Recipe> canHandle) {
        //First, check if items and fluids are valid.
        if (items.length > Long.SIZE) {
            Utils.onInvalidData("ERROR! TOO LARGE INPUT IN RECIPEMAP, time to fix a real bitmap. Probably never get this!");
            return null;
        }
        if (items.length == 0 && fluids.length == 0) return null;
        //Filter out empty fluids.
        fluids = Arrays.stream(fluids).filter(t -> !t.isEmpty() || !(t.getFluid() == Fluids.EMPTY)).toArray(FluidStack[]::new);

        //Build input.
        List<List<AbstractMapIngredient>> inputs = buildFromItemStacks(Arrays.asList(uniqueItems(items)));
        inputs.addAll(buildFromFluids(Arrays.asList(fluids)));

        //Find recipe.
        long current = System.nanoTime();
        Recipe r = recurseItemTreeFind(inputs, LOOKUP, canHandle);
        Antimatter.LOGGER.info("Time to lookup (µs): " + ((System.nanoTime() - current) / 1000));

        return r;
    }

    @Nullable
    public Recipe find(@Nonnull LazyHolder<MachineItemHandler<?>> itemHandler, @Nonnull LazyHolder<MachineFluidHandler<?>> fluidHandler, @Nonnull Predicate<Recipe> validator) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(EMPTY_ITEM), fluidHandler.map(MachineFluidHandler::getInputs).orElse(EMPTY_FLUID), validator);
    }

    protected void reset() {
        this.LOOKUP.clear();
    }

    public void compile(RecipeManager reg, Function<Item, Collection<ResourceLocation>> tagGetter) {
        reset();
        if (RECIPES_TO_COMPILE.size() > 0) {
            //Recipes with special ingredients have to be compiled first as you cannot
            //verify that a special recipe collides with a regular, but the opposite works.
            List<Recipe> regular = new ObjectArrayList<>(RECIPES_TO_COMPILE.size());
            List<Recipe> special = new ObjectArrayList<>();
            for (Recipe recipe : RECIPES_TO_COMPILE) {
                if (recipe.hasSpecialIngredients()) {
                    special.add(recipe);
                } else {
                    regular.add(recipe);
                }
            }
            special.forEach(t -> compileRecipe(t, tagGetter));
            regular.forEach(t -> {
                try {
                    compileRecipe(t, tagGetter);
                } catch (NullPointerException e){
                    String recipe = (t.getOutputItems(false) != null ? Arrays.deepToString(t.getOutputItems(false)) : "") + (t.getOutputFluids() != null ? Arrays.deepToString(t.getOutputFluids()) : "") + " " +  this.getId();
                    Antimatter.LOGGER.error("Recipe " + recipe + " Is null");
                }

            });
        }
        if (PROXY != null) {
            List<IRecipe<?>> recipes = reg.getRecipesForType(PROXY.loc);
            recipes.forEach(recipe -> {
                Recipe r = PROXY.handler.apply(recipe, RB());
                if (r != null) compileRecipe(r, tagGetter);
            });
        }
    }

    /**
     * Whether or not an ingredient is hashable.
     * @param i ingredient
     * @return if it can be hashed.
     */
    public static boolean isIngredientSpecial(Ingredient i) {
        Class<? extends Ingredient> clazz = i.getClass();
        return i.getMatchingStacks().length == 0 && (clazz != Ingredient.class && clazz != CompoundIngredient.class && clazz != NBTIngredient.class);
    }

    /**
     * Static classes
     **/

    public static class Proxy {
        public final IRecipeType loc;
        public final BiFunction<IRecipe<?>, RecipeBuilder, Recipe> handler;

        public Proxy(IRecipeType<?> loc, BiFunction<IRecipe<?>, RecipeBuilder, Recipe> handler) {
            this.loc = loc;
            this.handler = handler;
        }
    }

    protected static class Branch {

        private final Object2ObjectMap<AbstractMapIngredient, Either<Recipe, Branch>> NODES = new Object2ObjectOpenHashMap<>();

        private final List<Tuple<AbstractMapIngredient, Either<Recipe, Branch>>> SPECIAL_NODES = new ObjectArrayList<>();

        public Stream<Recipe> getRecipes(boolean filterHidden) {
            Stream<Recipe> stream = NODES.values().stream().flatMap(t -> t.map(
                    Stream::of,
                    branch -> branch.getRecipes(filterHidden)
            ));
            if (SPECIAL_NODES.size() > 0) {
                stream = Stream.concat(stream, SPECIAL_NODES.stream().flatMap(t -> t.getB().map(Stream::of, branch -> branch.getRecipes(filterHidden))));
            }
            if (filterHidden) stream = stream.filter(t -> !t.isHidden());
            return stream;
        }

        public void clear() {
            NODES.clear();
            SPECIAL_NODES.clear();
        }
    }
}
