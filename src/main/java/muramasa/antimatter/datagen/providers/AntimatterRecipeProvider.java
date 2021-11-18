package muramasa.antimatter.datagen.providers;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static muramasa.antimatter.util.TagUtils.nc;

//Only extending RecipeProvider for static purposes.
public class AntimatterRecipeProvider extends RecipeProvider {

    protected final String providerDomain, providerName;

    public AntimatterRecipeProvider(String providerDomain, String providerName, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    @Override
    public void run(DirectoryCache cache) {

    }

    @Override
    public void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {

    }



/*
        mainMats.forEach(main -> {
            if (!main.has(INGOT) && !main.has(GEM)) return; // TODO: For time being
            String ingotGem = main.has(INGOT) ? "ingots" : "gems";
            String plate = main.has(PLATE) ? "plates" : ingotGem;
            final ITag<Item> ingotTag = TagUtils.getForgeItemTag(ingotGem + "/" + main.getId()), plateTag = TagUtils.getForgeItemTag(plate + "/" + main.getId()), mainRodTag = TagUtils.getForgeItemTag("rods/" + main.getId());
            final ICriterionInstance ingotTrigger = this.hasSafeItem(ingotTag), plateTrigger = this.hasSafeItem(plateTag), rodTrigger = this.hasSafeItem(mainRodTag);

            if (main.getToolTypes().contains(WRENCH))
                addStackRecipe(consumer, Ref.ID, WRENCH.getId() + "_" + main.getId(), "antimatter_wrenches",
                    "has_material_" + main.getId(), ingotTrigger, WRENCH.getToolStack(main, NULL), of('I', ingotTag, 'H', HAMMER.getTag()), "IHI", "III", " I ");

            if (main.getToolTypes().contains(MORTAR))
                addStackRecipe(consumer, Ref.ID, MORTAR.getId() + "_" + main.getId(), "antimatter_mortars",
                    "has_material_" + main.getId(), ingotTrigger, MORTAR.getToolStack(main, NULL), of('I', ingotTag, 'S', Tags.Items.STONE), " I ", "SIS", "SSS");

            if (main.getToolTypes().contains(CROWBAR)) {
                for (DyeColor colour : DyeColor.values()) {
                    int colourValue = colour.getMapColor().colorValue;
                    ItemStack crowbarStack = CROWBAR.getToolStack(main, NULL);
                    crowbarStack.getChildTag(Ref.TAG_TOOL_DATA).putInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR, colourValue);
                    addStackRecipe(consumer, Ref.ID, CROWBAR.getId() + "_" + main.getId() + "_" + colour.toString(), "antimatter_crowbars",
                            "has_material_" + main.getId(), rodTrigger, crowbarStack, of('H', HAMMER.getTag(), 'C', colour.getTag(), 'R', mainRodTag, 'F', FILE.getTag()), "HCR", "CRC", "RCF");
                }
            }

            for (Material handle : handleMats) {
                String handleId = handle.getId().equals("wood") ? "wooden" : handle.getId();
                final ITag<Item> rodTag = TagUtils.getForgeItemTag("rods/" + handleId);

                ImmutableMap<Character, Object> map1 = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? of('I', ingotTag, 'R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()) : of('I', ingotTag, 'R', rodTag, 'P', plateTag);
                ImmutableMap<Character, Object> map2 = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? of('R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()) : of('R', rodTag, 'P', plateTag);
                String[] strings = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? new String[]{"PII", "FRH", " R "} : new String[]{"PII", " R ", " R "};
                if (main.getToolTypes().contains(PICKAXE))
                    addStackRecipe(consumer, Ref.ID, PICKAXE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_pickaxes",
                        "has_material_" + main.getId(), ingotTrigger, PICKAXE.getToolStack(main, handle), map1, strings);
                strings = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? new String[]{"IIH", "PR ", "FR "} : new String[]{"II", "PR", " R"};
                if (main.getToolTypes().contains(AXE))
                   addStackRecipe(consumer, Ref.ID, AXE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_axes",
                        "has_material_" + main.getId(), ingotTrigger, AXE.getToolStack(main, handle), map1, strings);
                strings = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? new String[]{"PIH", "FR ", " R "} : new String[]{"PI", " R", " R"};
                if (main.getToolTypes().contains(HOE))
                    addStackRecipe(consumer, Ref.ID, HOE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_hoes",
                        "has_material_" + main.getId(), ingotTrigger, HOE.getToolStack(main, handle), map1, strings);
                strings = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? new String[]{"FPH", " R ", " R "} : new String[]{"P", "R", "R"};
                if (main.getToolTypes().contains(SHOVEL))
                    addStackRecipe(consumer, Ref.ID, SHOVEL.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_shovels",
                        "has_material_" + main.getId(), ingotTrigger, SHOVEL.getToolStack(main, handle), map2, strings);
                strings = main.getToolTypes().contains(HAMMER) && main.getToolTypes().contains(FILE) ? new String[]{" P ", "FPH", " R "} : new String[]{"P", "P", "R"};
                if (main.getToolTypes().contains(SWORD))
                    addStackRecipe(consumer, Ref.ID, SWORD.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_swords",
                        "has_material_" + main.getId(), ingotTrigger, SWORD.getToolStack(main, handle), map2, strings);

                if (main.getToolTypes().contains(HAMMER))
                    addStackRecipe(consumer, Ref.ID, HAMMER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_hammers",
                        "has_material_" + main.getId(), ingotTrigger, HAMMER.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "II ", "IIR", "II ");

                if (main.getToolTypes().contains(SAW))
                    addStackRecipe(consumer, Ref.ID, SAW.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_saws",
                        "has_material_" + main.getId(), plateTrigger, SAW.getToolStack(main, handle), of('P', plateTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PPR", "FH ");

                if (main.getToolTypes().contains(FILE))
                    addStackRecipe(consumer, Ref.ID, FILE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_files",
                        "has_material_" + main.getId(), plateTrigger, FILE.getToolStack(main, handle), of('P', plateTag, 'R', rodTag), "P", "P", "R");

                if (main.getToolTypes().contains(KNIFE))
                    addStackRecipe(consumer, Ref.ID, KNIFE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_knives",
                        "has_material_" + main.getId(), plateTrigger, KNIFE.getToolStack(main, handle), of('P', plateTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "FPH", " R ");

                if (main.getToolTypes().contains(SCREWDRIVER))
                    addStackRecipe(consumer, Ref.ID, SCREWDRIVER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_screwdrivers",
                        "has_material_" + main.getId(), rodTrigger, SCREWDRIVER.getToolStack(main, handle),
                        of('M', mainRodTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), " FM", " MH", "R  ");

                if (main.getToolTypes().contains(WIRE_CUTTER)) {
                    if (main.has(SCREW)){
                        addStackRecipe(consumer, Ref.ID, WIRE_CUTTER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_wire_cutters",
                                "has_material_" + main.getId(), plateTrigger, WIRE_CUTTER.getToolStack(main, handle),
                                b -> b.put('P', plateTag).put('R', rodTag).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                        .put('S', SCREWDRIVER.getTag()).put('W', getForgeItemTag("screws/" + main.getId())), "PFP", "HPS", "RWR");
                    } else {
                        addStackRecipe(consumer, Ref.ID, WIRE_CUTTER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_wire_cutters",
                                "has_material_" + main.getId(), plateTrigger, WIRE_CUTTER.getToolStack(main, handle),
                                b -> b.put('P', plateTag).put('R', rodTag).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                        .put('S', SCREWDRIVER.getTag()), "PFP", "HPS", "R R");
                    }
                }
            }
        });*/

    public void addConditionalRecipe(Consumer<IFinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(new ConfigCondition(configClass, configFieldName))
                .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }

    public AntimatterShapedRecipeBuilder getItemRecipe(String groupName, String criterionName, ICriterionInstance criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        return getStackRecipe(groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    public AntimatterShapedRecipeBuilder getStackRecipe(String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        return getStackRecipe(groupName, criterionName, criterion, Collections.singletonList(output), inputs, inputPattern);
    }

    public AntimatterShapedRecipeBuilder getStackRecipe(String groupName, String criterionName, ICriterionInstance criterion, List<ItemStack> output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        if (inputs.isEmpty()) Utils.onInvalidData("Inputs should not be empty!");
        if (inputPattern.length < 1 || inputPattern.length > 3)
            Utils.onInvalidData("Input pattern must have between 1 and 3 rows!");
        AntimatterShapedRecipeBuilder recipeBuilder = AntimatterShapedRecipeBuilder.shapedRecipe(output);
        recipeBuilder = resolveKeys(recipeBuilder, inputs);
        for (String s : inputPattern) {
            if (s.length() > 3) Utils.onInvalidData("Input pattern rows must have between 0 and 3 characters!");
            recipeBuilder = recipeBuilder.patternLine(s);
        }
        recipeBuilder = recipeBuilder.setGroup(groupName);
        recipeBuilder = recipeBuilder.addCriterion(criterionName, criterion);
        return recipeBuilder;
    }

    public void shapeless(Consumer<IFinishedRecipe> consumer, String recipeID, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, Object... inputs) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(output.getItem(), output.getCount()).unlockedBy(criterionName, criterion)
                .group(groupName);
        for (Object input : inputs) {
            try {
                if (input instanceof IItemProvider) {
                    builder.requires(((IItemProvider) input));
                } else if (input instanceof ITag) {
                    if (input instanceof ITag.INamedTag) {
                        builder.requires(nc(TagUtils.getItemTag(((ITag.INamedTag) input).getName())));
                    } else {
                        builder.requires((ITag<Item>) input);
                    }
                } else if (input instanceof Ingredient) {
                    builder.requires((Ingredient) input);
                }
            } catch (ClassCastException ex) {
                throw new RuntimeException("ERROR PARSING SHAPELESS RECIPE" + ex.getMessage());
            }
        }
        builder.save(consumer, new ResourceLocation(Ref.ID, output.getItem().toString() + "_" + recipeID));
    }

    public void addItemRecipe(Consumer<IFinishedRecipe> consumer, String groupName, String criterionName, ICriterionInstance criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        addStackRecipe(consumer, "", "", groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    public void addItemRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        addStackRecipe(consumer, recipeDomain, recipeName, groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    public void addStackRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, Function<ImmutableMap.Builder<Character, Object>, ImmutableMap.Builder<Character, Object>> inputs, String... inputPattern) {
        addStackRecipe(consumer, recipeDomain, recipeName, groupName, criterionName, criterion, output, inputs.apply(new ImmutableMap.Builder<>()).build(), inputPattern);
    }

    public void addStackRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        AntimatterShapedRecipeBuilder recipeBuilder = getStackRecipe(groupName, criterionName, criterion, output, inputs, inputPattern);
        if (recipeName.isEmpty()) recipeBuilder.build(consumer);
        else {
            if (recipeDomain.isEmpty()) recipeBuilder.build(consumer, recipeName);
            else recipeBuilder.build(consumer, fixLoc(recipeDomain, recipeName));
        }
    }

    public void addToolRecipe(String builder, Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, List<ItemStack> output, Function<ImmutableMap.Builder<Character, Object>, ImmutableMap.Builder<Character, Object>> inputs, String... inputPattern) {
        addToolRecipe(builder, consumer, recipeDomain, recipeName, groupName, criterionName, criterion, output, inputs.apply(new ImmutableMap.Builder<>()).build(), inputPattern);
    }

    public void addToolRecipe(String builder, Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, Function<ImmutableMap.Builder<Character, Object>, ImmutableMap.Builder<Character, Object>> inputs, String... inputPattern) {
        addToolRecipe(builder, consumer, recipeDomain, recipeName, groupName, criterionName, criterion, Collections.singletonList(output), inputs.apply(new ImmutableMap.Builder<>()).build(), inputPattern);
    }

    public void addToolRecipe(String builder, Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        addToolRecipe(builder, consumer, recipeDomain, recipeName, groupName, criterionName, criterion, Collections.singletonList(output), inputs, inputPattern);
    }

    public void addToolRecipe(String builder, Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, List<ItemStack> output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        if (output.isEmpty()) {
            Antimatter.LOGGER.warn("Material recipe " + recipeDomain + ":" + recipeName + "has an empty output.");
            return;
        }
        AntimatterShapedRecipeBuilder recipeBuilder = getStackRecipe(groupName, criterionName, criterion, output, inputs, inputPattern);
        if (recipeName.isEmpty()) recipeBuilder.build(consumer);
        else {
            if (recipeDomain.isEmpty()) recipeBuilder.buildTool(consumer, builder, recipeName);
            else recipeBuilder.buildTool(consumer, builder, fixLoc(recipeDomain, recipeName));
        }
    }

    @SuppressWarnings("unchecked")
    protected AntimatterShapedRecipeBuilder resolveKeys(AntimatterShapedRecipeBuilder incompleteBuilder, ImmutableMap<Character, Object> inputs) {
        for (Map.Entry<Character, Object> entry : inputs.entrySet()) {
            if (entry.getValue() instanceof IItemProvider) {
                incompleteBuilder = incompleteBuilder.key(entry.getKey(), (IItemProvider) entry.getValue());
            } else if (entry.getValue() instanceof ITag) {
                try {
                    //Wrap the tag using tag manager.
                    if (entry.getValue() instanceof ITag.INamedTag) {
                        ITag.INamedTag<Item> tag = (ITag.INamedTag<Item>) entry.getValue();
                        incompleteBuilder = incompleteBuilder.key(entry.getKey(), nc(tag));
                    } else {
                        incompleteBuilder = incompleteBuilder.key(entry.getKey(), (ITag<Item>) entry.getValue());
                    }
                } catch (ClassCastException e) {
                    Utils.onInvalidData("Tag inputs only allow Item Tags!");
                }
            } else if (entry.getValue() instanceof PropertyIngredient) {
                incompleteBuilder = incompleteBuilder.key(entry.getKey(), (PropertyIngredient) entry.getValue());
            } else if (entry.getValue() instanceof Ingredient) {
                incompleteBuilder = incompleteBuilder.key(entry.getKey(), (Ingredient) entry.getValue());
            }
        }
        return incompleteBuilder;
    }

    public String fixLoc(String providerDomain, String attach) {
        return providerDomain.concat(":").concat(attach);
    }

    @Override
    public String getName() {
        return providerName;
    }

    public ICriterionInstance hasSafeItem(ITag<Item> tag) {
        return RecipeProvider.has(tag);
    }

    public ICriterionInstance hasSafeItem(IItemProvider stack) {
        return RecipeProvider.has(stack);
    }
}
