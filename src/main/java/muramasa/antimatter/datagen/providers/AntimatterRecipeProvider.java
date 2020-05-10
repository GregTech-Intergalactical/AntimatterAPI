package muramasa.antimatter.datagen.providers;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.Data.SAW;
import static muramasa.antimatter.material.MaterialTag.RUBBERTOOLS;
import static muramasa.antimatter.material.MaterialType.*;
import static muramasa.antimatter.util.Utils.getForgeItemTag;

public class AntimatterRecipeProvider extends RecipeProvider {

    private final String providerDomain, providerName;

    public AntimatterRecipeProvider(String providerDomain, String providerName, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        registerMaterialRecipes(consumer, providerDomain);
        registerToolRecipes(consumer, providerDomain);
    }

    protected void registerMaterialRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        //        RegistrationHelper.getMaterialsForDomain(Ref.ID).stream().filter(m -> m.has(DUST)).forEach(mat -> {
//            Item dust = DUST.get(mat);
//            if (mat.has(ROCK)) {
//                Tag<Item> rockTag = getForgeItemTag("rocks/".concat(mat.getId()));
//                Item rock = ROCK.get(mat);
//                Item smallDust = DUST_SMALL.get(mat);
//                ShapelessRecipeBuilder.shapelessRecipe(dust)
//                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
//                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
//                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(MORTAR.getTag())
//                        .addCriterion("has_rock_" + mat.getId(), this.hasItem(rockTag))
//                        .setGroup("rocks_grind_to_dust").build(consumer, sigh(rock.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));
//
//                ShapelessRecipeBuilder.shapelessRecipe(smallDust)
//                        .addIngredient(rockTag).addIngredient(rockTag)
//                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(MORTAR.getTag())
//                        .addCriterion("has_rock_" + mat.getId(), this.hasItem(getForgeItemTag("rocks/".concat(mat.getId()))))
//                        .setGroup("rocks_grind_to_small_dust").build(consumer, sigh(rock.getRegistryName().getPath() + "_grind_to_" + smallDust.getRegistryName().getPath()));
//            }
//            if (mat.has(INGOT, GRINDABLE)) {
//                Item ingot = INGOT.get(mat);
//                Tag<Item> ingotTag = getForgeItemTag("ingots/".concat(mat.getId()));
//                ShapelessRecipeBuilder.shapelessRecipe(dust).addIngredient(ingotTag).addIngredient(MORTAR.getTag())
//                        .addCriterion("has_ingot_" + mat.getId(), this.hasItem(getForgeItemTag("ingots/".concat(mat.getId()))))
//                        .setGroup("ingots_grind_to_dust")
//                        .build(consumer, sigh(ingot.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));
//            }
//        });
    }

    protected void registerToolRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        List<Material> mainMats = AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(TOOLS))).collect(Collectors.toList());
        List<Material> handleMats = AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.isHandle())).collect(Collectors.toList());

        handleMats.forEach(handle -> {
            AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(RUBBERTOOLS))).forEach(rubber -> {
                Tag<Item> plateTag = getForgeItemTag("plates/" + rubber.getId()), rodTag = getForgeItemTag("rods/" + handle.getId());
                addStackRecipe(consumer, Ref.ID, PLUNGER.getId() + "_" + handle.getId() + "_" + rubber.getId(), "antimatter_plungers",
                        "has_material_" + rubber.getId(), this.hasItem(plateTag), PLUNGER.getToolStack(handle, rubber),
                        of('W', WIRE_CUTTER.getTag(), 'P', plateTag, 'S', Tags.Items.SLIMEBALLS, 'R', rodTag, 'F', FILE.getTag()), "WPS", " RP", "R F");
            });
        });

        mainMats.forEach(main -> {
            if (!main.has(INGOT)) return; // TODO: For time being
            final Tag<Item> ingotTag = getForgeItemTag("ingots/" + main.getId()), plateTag = getForgeItemTag("plates/" + main.getId()), mainRodTag = getForgeItemTag("rods/" + main.getId());
            final InventoryChangeTrigger.Instance ingotTrigger = this.hasItem(ingotTag), plateTrigger = this.hasItem(plateTag), rodTrigger = this.hasItem(mainRodTag);

            addStackRecipe(consumer, Ref.ID, WRENCH.getId() + "_" + main.getId(), "antimatter_wrenches",
                    "has_material_" + main.getId(), ingotTrigger, WRENCH.getToolStack(main, NULL), of('I', ingotTag, 'H', HAMMER.getTag()), "IHI", "III", " I "); // CHANGED

            addStackRecipe(consumer, Ref.ID, MORTAR.getId() + "_" + main.getId(), "antimatter_mortars",
                    "has_material_" + main.getId(), ingotTrigger, MORTAR.getToolStack(main, NULL), of('I', ingotTag, 'S', Tags.Items.STONE), " I ", "SIS", "SSS");

            for (DyeColor colour : DyeColor.values()) {
                int colourValue = colour.getMapColor().colorValue;
                ItemStack crowbarStack = CROWBAR.getToolStack(main, NULL);
                crowbarStack.getChildTag(Ref.TAG_TOOL_DATA).putInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR, colourValue);
                addStackRecipe(consumer, Ref.ID, CROWBAR.getId() + "_" + main.getId() + "_" + colour.getName(), "antimatter_crowbars",
                        "has_material_" + main.getId(), rodTrigger, crowbarStack, of('H', HAMMER.getTag(), 'C', colour.getTag(), 'R', mainRodTag, 'F', FILE.getTag()), "HCR", "CRC", "RCF");
            }

            for (Material handle : handleMats) {
                String handleId = handle.getId() == "wood" ? "wooden" : handle.getId();
                final Tag<Item> rodTag = getForgeItemTag("rods/" + handleId);

                addStackRecipe(consumer, Ref.ID, PICKAXE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_pickaxes",
                        "has_material_" + main.getId(), ingotTrigger, PICKAXE.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "III", " R ", " R ");

                addStackRecipe(consumer, Ref.ID, AXE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_axes",
                        "has_material_" + main.getId(), ingotTrigger, AXE.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "II ", "IR ", " R "); // CHANGED

                addStackRecipe(consumer, Ref.ID, HOE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_hoes",
                        "has_material_" + main.getId(), ingotTrigger, HOE.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "II ", " R ", " R "); // CHANGED

                addStackRecipe(consumer, Ref.ID, SHOVEL.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_shovels",
                        "has_material_" + main.getId(), ingotTrigger, SHOVEL.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "I", "R", "R");

                addStackRecipe(consumer, Ref.ID, SWORD.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_swords",
                        "has_material_" + main.getId(), ingotTrigger, SWORD.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "I", "I", "R");

                addStackRecipe(consumer, Ref.ID, HAMMER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_hammers",
                        "has_material_" + main.getId(), ingotTrigger, HAMMER.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "II ", "IIR", "II "); // CHANGED

                addStackRecipe(consumer, Ref.ID, SAW.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_saws",
                        "has_material_" + main.getId(), plateTrigger, SAW.getToolStack(main, handle), of('P', plateTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PPR", "FH ");

                addStackRecipe(consumer, Ref.ID, FILE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_files",
                        "has_material_" + main.getId(), plateTrigger, FILE.getToolStack(main, handle), of('P', plateTag, 'R', rodTag), "P", "P", "R");

                addStackRecipe(consumer, Ref.ID, KNIFE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_knives",
                        "has_material_" + main.getId(), plateTrigger, KNIFE.getToolStack(main, handle), of('P', plateTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "FPH", " R ");

                addStackRecipe(consumer, Ref.ID, SCREWDRIVER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_screwdrivers",
                        "has_material_" + main.getId(), rodTrigger, SCREWDRIVER.getToolStack(main, handle),
                        of('M', mainRodTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), " FM", " MH", "R  ");

                addStackRecipe(consumer, Ref.ID, WIRE_CUTTER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_wire_cutters",
                        "has_material_" + main.getId(), plateTrigger, WIRE_CUTTER.getToolStack(main, handle),
                        b -> b.put('P', plateTag).put('R', rodTag).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                .put('S', SCREWDRIVER.getTag()).put('W', getForgeItemTag("screws/" + main.getId())), "PFP", "HPS", "RWR");
            }
        });
    }

    protected void addConditionalRecipe(Consumer<IFinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(new ConfigCondition(configClass, configFieldName))
        .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }

    protected AntimatterShapedRecipeBuilder getItemRecipe(String groupName, String criterionName, ICriterionInstance criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        return getStackRecipe(groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    protected AntimatterShapedRecipeBuilder getStackRecipe(String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        if (inputs.isEmpty()) Utils.onInvalidData("Inputs should not be empty!");
        if (inputPattern.length < 1 || inputPattern.length > 3) Utils.onInvalidData("Input pattern must have between 1 and 3 rows!");
        AntimatterShapedRecipeBuilder recipeBuilder = AntimatterShapedRecipeBuilder.shapedRecipe(output);
        recipeBuilder = resolveKeys(recipeBuilder, inputs);
        for (int i = 0; i < inputPattern.length; i++) {
            if (inputPattern[i].length() > 3) Utils.onInvalidData("Input pattern rows must have between 0 and 3 characters!");
            recipeBuilder = recipeBuilder.patternLine(inputPattern[i]);
        }
        recipeBuilder = recipeBuilder.setGroup(groupName);
        recipeBuilder = recipeBuilder.addCriterion(criterionName, criterion);
        return recipeBuilder;
    }

    protected void addItemRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        addStackRecipe(consumer, recipeDomain, recipeName, groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    protected void addStackRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, Function<ImmutableMap.Builder<Character, Object>, ImmutableMap.Builder<Character, Object>> inputs, String... inputPattern) {
        addStackRecipe(consumer, recipeDomain, recipeName, groupName, criterionName, criterion, output, inputs.apply(new ImmutableMap.Builder<>()).build(), inputPattern);
    }

    protected void addStackRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, ICriterionInstance criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        AntimatterShapedRecipeBuilder recipeBuilder = getStackRecipe(groupName, criterionName, criterion, output, inputs, inputPattern);
        if (recipeName.isEmpty()) recipeBuilder.build(consumer);
        else {
            if (recipeDomain.isEmpty()) recipeBuilder.build(consumer, recipeName);
            else recipeBuilder.build(consumer, fixLoc(recipeDomain, recipeName));
        }
    }

    protected AntimatterShapedRecipeBuilder resolveKeys(AntimatterShapedRecipeBuilder incompleteBuilder, ImmutableMap<Character, Object> inputs) {
        for (Map.Entry<Character, Object> entry : inputs.entrySet()) {
            if (entry.getValue() instanceof IItemProvider) {
                incompleteBuilder = incompleteBuilder.key(entry.getKey(), (IItemProvider) entry.getValue());
            }
            else if (entry.getValue() instanceof Tag) {
                try {
                    incompleteBuilder = incompleteBuilder.key(entry.getKey(), (Tag<Item>) entry.getValue());
                }
                catch (ClassCastException e) {
                    Utils.onInvalidData("Tag inputs only allow Item Tags!");
                }
            }
            else if (entry.getValue() instanceof Ingredient) {
                incompleteBuilder = incompleteBuilder.key(entry.getKey(), (Ingredient) entry.getValue());
            }
        }
        return incompleteBuilder;
    }

    protected String fixLoc(String providerDomain, String attach) {
        return providerDomain.concat(":").concat(attach);
    }

    @Override
    public String getName() {
        return providerName;
    }

}
