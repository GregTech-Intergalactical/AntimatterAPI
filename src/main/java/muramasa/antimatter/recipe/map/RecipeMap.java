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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeMap<B extends RecipeBuilder> implements IAntimatterObject {
    public static class Proxy {
        public final IRecipeType loc;
        public final BiFunction<IRecipe, RecipeBuilder, Recipe> handler;

        public Proxy(IRecipeType loc, BiFunction<IRecipe, RecipeBuilder, Recipe> handler) {
            this.loc = loc;
            this.handler = handler;
        }
    }
    protected static class Branch {

        private final Object2ObjectMap<AbstractMapIngredient, Either<Recipe, Branch>> NODES = new Object2ObjectOpenHashMap<>();

        public Stream<Recipe> getRecipes(boolean filterHidden) {
            Stream<Recipe> stream = NODES.values().stream().flatMap(t -> t.map(
                    Stream::of,
                    branch -> branch.getRecipes(filterHidden)
            ));
            if (filterHidden) stream = stream.filter(t -> !t.isHidden());
            return stream;
        }
    }

    private final String id;
    private final B builder;
    private IRecipeInfoRenderer infoRenderer;
    @Nullable
    private GuiData GUI;
    //Root branch.
    protected final Branch LOOKUP = new Branch();
    @Nullable
    private Tier guiTier;
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
        if (infoRenderer == null) return InfoRenderers.DEFAULT_RENDERER;
        return infoRenderer;
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

    @OnlyIn(Dist.CLIENT)
    public void setInfoRenderer(IRecipeInfoRenderer renderer) {
        this.infoRenderer = renderer;
    }

    void add(Recipe recipe) {
        RECIPES_TO_COMPILE.add(recipe);
    }

    /**
     * Adds a recipe to this map. If the recipe is empty or collides with another recipe it is not added.
     * @param recipe the recipe to add.
     */
    public void compileRecipe(Recipe recipe) {
        if (recipe == null) return;
        Branch map = LOOKUP;
        List<List<AbstractMapIngredient>> items = fromRecipe(recipe);

        if (recipe.hasOutputItems()) {
            for (ItemStack stack : recipe.getOutputItems()) {
                if (stack.isEmpty()) {
                    Utils.onInvalidData("RECIPE WITH EMPTY OUTPUT ITEM");
                    return;
                }
            }
        }
        if (recipe.hasInputItems()) {
            for (RecipeIngredient inputItem : recipe.getInputItems()) {
                if (inputItem.get().hasNoMatchingItems() || (inputItem.get().getMatchingStacks().length == 1 && inputItem.get().getMatchingStacks()[0].getItem() == Items.BARRIER)) {
                    //Utils.onInvalidData("RECIPE WITH EM");
                    return;
                }
            }
        }
        Recipe r = recurseItemTreeFind(items, map);
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
            List<AbstractMapIngredient> inner = new ObjectArrayList<>(1+t.getFluid().getTags().size());
            inner.add(new MapFluidIngredient(t));
            for (ResourceLocation rl : t.getFluid().getTags()) {
                inner.add(new MapTagIngredient(rl));
            }
            ret.add(inner);
        }
        return ret;
    }

    protected List<List<AbstractMapIngredient>> fromRecipe(Recipe r) {
        List<List<AbstractMapIngredient>> items = buildFromItems(r.getInputItems().stream().map(RecipeIngredient::get).collect(Collectors.toList()));
        if (r.hasInputFluids()) items.addAll(buildFromFluids(Arrays.asList(r.getInputFluids())));
        return items;
    }

    protected List<List<AbstractMapIngredient>> buildFromItems(List<Ingredient> ingredients) {
        List<List<AbstractMapIngredient>> ret = new ObjectArrayList<>(ingredients.size());
        for (Ingredient t : ingredients) {
            Optional<ResourceLocation> rl = MapTagIngredient.findCommonTag(t);
            if (rl.isPresent()) {
                ret.add(Collections.singletonList(new MapTagIngredient(rl.get())));
            } else {
                List<AbstractMapIngredient> inner = new ObjectArrayList<>(t.getMatchingStacks().length);
                for (ItemStack stack : t.getMatchingStacks()) {
                    inner.add(new MapItemIngredient(stack));
                }
                ret.add(inner);
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
     * @param recipe the recipe to add.
     * @param ingredients list of input ingredients.
     * @param map the current place in the recursion.
     * @param index where in the ingredients list we are.
     * @param count how many added already.
     */
    boolean recurseItemTreeAdd(Recipe recipe, List<List<AbstractMapIngredient>> ingredients, @Nonnull Branch map, int index, int count) {
        if (count >= ingredients.size()) return true;
        if (index >= ingredients.size()) {
            throw new RuntimeException("Index out of bounds for recurseItemTreeAdd, should not happen");
        }
        //Loop through NUMBER_OF_INGREDIENTS times.
        List<AbstractMapIngredient> current = ingredients.get(index);
        Either<Recipe, Branch> r;
        for (AbstractMapIngredient obj : current) {
            r = map.NODES.compute(obj, (k,v) -> {
                if (count == ingredients.size() - 1) {
                    v = Either.left(recipe);
                    return v;
                } else if (v == null) {
                    Branch traverse = new Branch();
                    v = Either.right(traverse);
                }
                return v;
            });
            if (count == ingredients.size() - 1) continue;
            if (r.left().isPresent()) {
                Utils.onInvalidData("COLLISION DETECTED!");
                current.forEach(map.NODES::remove);
                return false;
            }
            //should always be present but this gives no warning.
            if (r.right().map(m -> !recurseItemTreeAdd(recipe, ingredients, m, (index + 1) % ingredients.size(), count + 1)).orElse(false)) {
                current.forEach(map.NODES::remove);
                return false;
            }
        }
        if (count == ingredients.size() - 1) return true;
        return true;
    }

    /**
     * Recursively finds a recipe, top level. call this to find a recipe
     * @param items the items part
     * @param map the root branch to search from.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(List<List<AbstractMapIngredient>> items, @Nonnull Branch map) {
        for (int i = 0; i < items.size(); i++) {
            Recipe r = recurseItemTreeFind(items, map, i, 0, (1L << i));
            if (r != null) return r;
        }
        return null;
    }
    /**
     * Recursively finds a recipe
     * @param items the items part
     * @param map the current branch of the tree
     * @param index the index of the wrapper to get
     * @param count how deep we are in recursion, < items.length
     * @param skip bitmap of items to skip, i.e. which items are used in the recursion.
     * @return a recipe
     */
    Recipe recurseItemTreeFind(List<List<AbstractMapIngredient>> items, @Nonnull Branch map, int index, int count, long skip) {
        if (count == items.size()) return null;
        List<AbstractMapIngredient> wr = items.get(index);
        for (AbstractMapIngredient t : wr) {
            Either<Recipe, RecipeMap.Branch> result = map.NODES.get(t);
            if (result == null) continue;
            Recipe r = result.map(left -> left, right -> {
                int counter = (index + 1) % items.size();
                while (counter != index) {
                    //Have we already used this ingredient? If so, skip this one.
                    if (((skip & (1L << counter)) == 0)) {
                        Recipe found = recurseItemTreeFind(items, right, counter, count + 1, skip | (1L << counter));
                        if (found != null) return found;
                    }
                    counter = (counter + 1) % items.size();
                }
                return null;
            });
            if (r != null) return r;
        }
        return null;
    }

    @Nullable
    public Recipe find(@Nonnull LazyOptional<MachineItemHandler<?>> itemHandler, @Nonnull LazyOptional<MachineFluidHandler<?>> fluidHandler) {
        return find(itemHandler.map(MachineItemHandler::getInputs).orElse(new ItemStack[0]), fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
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
    public Recipe find(@Nullable ItemStack[] items, @Nullable FluidStack[] fluids) {
        long current = System.nanoTime();
        //First, check if items and fluids are valid.
        if (items != null && items.length > Long.SIZE) {
            Utils.onInvalidData("ERROR! TOO LARGE INPUT IN RECIPEMAP, time to fix a real bitmap. Probably never get this!");
            return null;
        }
        if ((items == null ||items.length == 0) && (fluids == null || fluids.length == 0)) return null;
        //Filter out empty fluids.
        fluids = fluids == null ? null : Arrays.stream(fluids).filter(t -> !t.isEmpty() || !(t.getFluid() == Fluids.EMPTY)).toArray(FluidStack[]::new);
        //current = System.nanoTime() - current;
        List<List<AbstractMapIngredient>> ings = buildFromItemStacks(items != null ? Arrays.asList(uniqueItems(items)) : new ObjectArrayList<>());
        ings.addAll(buildFromFluids(fluids != null ? Arrays.asList(fluids) : Collections.emptyList()));
        //Antimatter.LOGGER.info("Time to validate (µs): " + ((System.nanoTime()-current) / 1000));
        current = System.nanoTime();
        Recipe r = recurseItemTreeFind(ings, LOOKUP);
        Antimatter.LOGGER.info("Time to lookup (µs): " + ((System.nanoTime()-current) / 1000));
        return r;

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
            List<IRecipe<?>> recipes = reg.getRecipesForType(PROXY.loc);
            recipes.stream().forEach(recipe -> {
                Recipe r = PROXY.handler.apply(recipe, RB());
                if (r != null) compileRecipe(r);
            });
        }
    }
}
