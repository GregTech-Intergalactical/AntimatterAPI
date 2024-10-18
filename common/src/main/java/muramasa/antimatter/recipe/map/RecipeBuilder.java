package muramasa.antimatter.recipe.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeTag;
import muramasa.antimatter.recipe.RecipeUtil;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RecipeBuilder {

    private static final Map<String, IRecipe> ID_MAP = new Object2ObjectArrayMap<>();

    private static String CURRENT_MOD_ID = Ref.SHARED_ID;

    private RecipeMap<? extends RecipeBuilder> recipeMap;
    protected List<ItemStack> itemsOutput = new ObjectArrayList<>();
    protected List<Ingredient> ingredientInput = new ObjectArrayList<>();
    protected List<FluidIngredient> fluidsInput = new ObjectArrayList<>();
    protected List<FluidHolder> fluidsOutput = new ObjectArrayList<>();
    protected int[] inputChances, outputChances;
    protected int duration, special;
    protected long power;
    protected int amps;
    protected boolean hidden, fake;
    protected Set<RecipeTag> tags = new ObjectOpenHashSet<>();
    protected ResourceLocation id;
    protected boolean recipeMapOnly = false;

    private Advancement.Builder advancementBuilder = null;

    public IRecipe add(String modid, String id) {
        id(modid, id);
        //addToMap(r);
        return build();
    }

    public IRecipe add(String id) {
        return add(recipeMap.getDomain(), id);
    }

    public static void clearList(){
        ID_MAP.clear();
    }

    public static Map<String, IRecipe> getIdMap() {
        return ID_MAP;
    }

    public static void setCurrentModId(String id){
        CURRENT_MOD_ID = id;
    }

    protected void addToMap(IRecipe r) {
        recipeMap.add(r);
    }

    /**
     * Builds a recipe without adding it to a map.
     *
     * @return the recipe.
     */
    public IRecipe build() {
        if (itemsOutput != null && itemsOutput.size() > 0 && !Utils.areItemsValid(itemsOutput.toArray(new ItemStack[0]))) {
            String id = this.id == null ? "": " Recipe ID: " + this.id;
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT ITEMS INVALID!" + id + " Recipe map ID:" + recipeMap.getId());
            return Utils.getEmptyRecipe();
        }
        if (fluidsOutput != null && fluidsOutput.size() > 0 && !Utils.areFluidsValid(fluidsOutput.toArray(new FluidHolder[0]))) {
            String id = this.id == null ? "": " Recipe ID: " + this.id;
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT FLUIDS INVALID!" + id + " Recipe map ID:" + recipeMap.getId());
            return Utils.getEmptyRecipe();
        }
        if (ingredientInput == null) ingredientInput = Collections.emptyList();
        if (this.amps < 1) this.amps = 1;
        if (!recipeMapOnly){
            ResourceLocation advancementID = advancementBuilder != null ? new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath()) : null;
            if (advancementBuilder != null){
                this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
            }
            AntimatterDynamics.FINISHED_RECIPE_CONSUMER.accept(new Result(this.id, advancementID));
        }
        if (amps < 1) amps = 1;
        Recipe recipe = new Recipe(
                ingredientInput,
                itemsOutput != null ? itemsOutput.toArray(new ItemStack[0]) : null,
                fluidsInput != null ? fluidsInput : Collections.emptyList(),
                fluidsOutput != null ? fluidsOutput.toArray(new FluidHolder[0]) : null,
                duration, power, special, amps
        );
        if (outputChances != null) recipe.addOutputChances(outputChances);
        if (inputChances != null) recipe.addInputChances(inputChances);
        recipe.setHidden(hidden);
        recipe.setFake(fake);
        recipe.addTags(new ObjectOpenHashSet<>(tags));
        if (recipeMapOnly){
            recipe.setId(this.id);
            recipe.setMapId(this.recipeMap.getId());
        }
        return recipe;
    }

    public void getID(){
        if (id == null){
            if (itemsOutput != null && itemsOutput.size() > 0){
                String id = AntimatterPlatformUtils.INSTANCE.getIdFromItem(itemsOutput.get(0).getItem()).toString() + "_recipe";
                checkID(id);
            } else if (fluidsOutput != null && fluidsOutput.size() > 0){
                String id = AntimatterPlatformUtils.INSTANCE.getIdFromFluid(fluidsOutput.get(0).getFluid()).toString() + "_recipe";
                checkID(id);
            } else if (!ingredientInput.isEmpty() && ingredientInput.get(0).getItems().length > 0){
                ItemStack stack = ingredientInput.get(0).getItems()[0];
                String id = AntimatterPlatformUtils.INSTANCE.getIdFromItem(stack.getItem()).toString() + "_recipe";
                checkID(id);
            } else if (!fluidsInput.isEmpty()){
                FluidIngredient ing = fluidsInput.get(0);
                String id;
                if (ing.getTag() != null){
                    id = ing.getTag().location().toString() + "_recipe";
                } else {
                    List<FluidHolder> list = Arrays.asList(ing.getStacks());
                    if (!list.isEmpty()){
                        id = AntimatterPlatformUtils.INSTANCE.getIdFromFluid(list.get(0).getFluid()).toString() + "_recipe";
                    } else {
                        id = "antimatter:unknown_in_" + recipeMap.getId();
                    }
                }
                checkID(id);
            }
        }
    }

    private void checkID(String id) {
        if (ID_MAP.containsKey(id)){
            String newID;
            int i = 1;
            do {
                newID = id + "_" + i;
                i++;
            } while (ID_MAP.containsKey(newID));
            id = newID;
        }
        this.id = new ResourceLocation(id);
    }

    public IRecipe add(String id, long duration, long power, long special) {
        return add(id, duration, power, special, 1);
    }

    public IRecipe add(String domain, String id, long duration, long power, long special, int amps) {
        this.duration = (int) duration;
        this.power = power;
        this.special = (int) special;
        this.amps = amps;
        return add(domain, id);
    }

    public IRecipe add(String id, long duration, long power, long special, int amps) {
        return add(CURRENT_MOD_ID, id, duration, power, special, amps);
    }

    public IRecipe add(String id, long duration, long power) {
        return add(id, duration, power, this.special);
    }

    public IRecipe add(String id, long duration) {
        return add(id, duration, 0, this.special);
    }

    public RecipeBuilder ii(ItemLike... stacks) {
        ingredientInput.addAll(Arrays.stream(stacks).map(Ingredient::of).toList());
        return this;
    }

    public RecipeBuilder ii(Ingredient... stacks) {
        ingredientInput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder ii(List<Ingredient> stacks) {
        ingredientInput.addAll(stacks);
        return this;
    }

    public RecipeBuilder io(ItemStack... stacks) {
        itemsOutput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder io(Item... stacks) {
        itemsOutput.addAll(Arrays.stream(stacks).map(Item::getDefaultInstance).toList());
        return this;
    }

    public RecipeBuilder io(List<ItemStack> stacks) {
        itemsOutput.addAll(stacks);
        return this;
    }

    public RecipeBuilder fi(FluidHolder... stacks) {
        fluidsInput.addAll(Arrays.stream(stacks).map(FluidIngredient::of).toList());
        return this;
    }

    public RecipeBuilder fi(FluidIngredient... stacks) {
        fluidsInput.addAll(Arrays.asList(stacks));
        return this;
    }


    public RecipeBuilder fi(List<FluidHolder> stacks) {
        fluidsInput.addAll(stacks.stream().map(FluidIngredient::of).toList());
        return this;
    }

    public RecipeBuilder fo(FluidHolder... stacks) {
        fluidsOutput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder fo(List<FluidHolder> stacks) {
        fluidsOutput.addAll(stacks);
        return this;
    }

    public RecipeBuilder id(ResourceLocation id){
        this.id = id;
        return this;
    }

    public RecipeBuilder id(String modid, String name){
        return id(new ResourceLocation(modid, recipeMap.getId() + "/" + name));
    }

    public RecipeBuilder id(String name){
        return id(recipeMap.getDomain(), name);
    }


    public RecipeBuilder outputChances(double... values) {
        int[] newChances = new int[values.length];
        for (int i = 0; i < values.length; i++){
            double chance = values[i];
            newChances[i] = (int) (chance * 10000);
        }
        return outputChances(newChances);
    }

    /**
     * 1000 = 10%, 7500 = 75%, 10 = 0.1%, 75 = .75% etc
     **/
    public RecipeBuilder outputChances(int... values){
        outputChances = values;
        return this;
    }

    public RecipeBuilder inputChances(double... values) {
        int[] newChances = new int[values.length];
        for (int i = 0; i < values.length; i++){
            double chance = values[i];
            newChances[i] = (int) (chance * 10000);
        }
        return inputChances(newChances);
    }

    /**
     * 1000 = 10%, 7500 = 75%, 10 = 0.1%, 75 = .75% etc
     **/
    public RecipeBuilder inputChances(int... values){
        inputChances = values;
        return this;
    }



    public RecipeBuilder hide() {
        hidden = true;
        return this;
    }

    public RecipeBuilder fake(){
        fake = true;
        return this;
    }

    public RecipeBuilder recipeMapOnly(){
        recipeMapOnly = true;
        return this;
    }

    public RecipeBuilder tags(RecipeTag... tags) {
        this.tags = new ObjectOpenHashSet<>(tags);
        return this;
    }

    public RecipeBuilder addCriterion(String name, CriterionTriggerInstance criterionIn) {
        if (this.advancementBuilder == null) advancementBuilder = Advancement.Builder.advancement();
        this.advancementBuilder.addCriterion(name, criterionIn);
        return this;
    }

    public RecipeBuilder clearItemInputs(){
        ingredientInput = new ObjectArrayList<>();
        inputChances = null;
        return this;
    }

    public RecipeBuilder clearItemOutputs(){
        itemsOutput = new ObjectArrayList<>();
        outputChances = null;
        return this;
    }

    public RecipeBuilder clearFluidInputs(){
        fluidsInput = new ObjectArrayList<>();
        return this;
    }

    public RecipeBuilder clearFluidOutputs(){
        fluidsOutput = new ObjectArrayList<>();
        return this;
    }

    public void clear() {
        itemsOutput = new ObjectArrayList<>();
        ingredientInput = new ObjectArrayList<>();
        fluidsInput = new ObjectArrayList<>();
        fluidsOutput = new ObjectArrayList<>();
        outputChances = null;
        inputChances = null;
        duration = special = 0;
        power = 0;
        hidden = false;
        fake = false;
        recipeMapOnly = false;
        tags.clear();
    }

    public RecipeMap<?> getMap() {
        return recipeMap;
    }

    public void setMap(RecipeMap<?> recipeMap) {
        this.recipeMap = recipeMap;
    }

    private class Result implements FinishedRecipe{
        ResourceLocation id;
        ResourceLocation advancementID = null;
        public Result(ResourceLocation id){
            this.id = id;
        }

        public Result(ResourceLocation id, ResourceLocation advancementID){
            this.id = id;
            this.advancementID = advancementID;
        }
        @Override
        public void serializeRecipeData(JsonObject json) {
            json.addProperty("map", recipeMap.getId());
            if (recipeMap.getRecipeSerializer() != null){
                recipeMap.getRecipeSerializer().toJson(id, json, RecipeBuilder.this);
                return;
            }
            JsonArray array = new JsonArray();
            for (Ingredient ingredient : ingredientInput) {
                array.add(ingredient.toJson());
            }
            if (!array.isEmpty()){
                json.add("inputItems", array);
            }
            array = new JsonArray();
            for (ItemStack stack : itemsOutput){
                array.add(RecipeUtil.itemstackToJson(stack));
            }
            if (!array.isEmpty()){
                json.add("outputItems", array);
            }
            array = new JsonArray();
            for (FluidIngredient f : fluidsInput) {
                array.add(f.toJson());
            }
            if (!array.isEmpty()){
                json.add("inputFluids", array);
            }
            array = new JsonArray();
            for (FluidHolder stack : fluidsOutput){
                array.add(RecipeUtil.fluidstackToJson(stack));
            }
            if (!array.isEmpty()){
                json.add("outputFluids", array);
            }
            json.addProperty("eu", power);
            json.addProperty("duration", duration);
            json.addProperty("amps", amps);
            json.addProperty("special", special);
            array = new JsonArray();
            if (outputChances != null) {
                for (int d : outputChances){
                    array.add(d);
                }
            }
            if (!array.isEmpty()){
                json.add("outputChances", array);
            }
            array = new JsonArray();
            if (inputChances != null) {
                for (int d : inputChances){
                    array.add(d);
                }
            }
            if (!array.isEmpty()){
                json.add("inputChances", array);
            }
            json.addProperty("hidden", hidden);
            json.addProperty("fake", fake);
            array = new JsonArray();
            for (RecipeTag tag : tags){
                array.add(tag.getLoc().toString());
            }
            if (!array.isEmpty()){
                json.add("tags", array);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return AntimatterRecipeSerializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            if (advancementBuilder != null){
                return advancementBuilder.serializeToJson();
            }
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementID;
        }
    }
}
