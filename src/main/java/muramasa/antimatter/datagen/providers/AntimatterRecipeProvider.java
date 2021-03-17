package muramasa.antimatter.datagen.providers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.ICraftingLoader;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.*;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTag.GRINDABLE;
import static muramasa.antimatter.material.MaterialTag.RUBBERTOOLS;
import static muramasa.antimatter.util.Utils.*;

//Only extending RecipeProvider for static purposes.
public class AntimatterRecipeProvider extends RecipeProvider implements IAntimatterProvider {

    protected final String providerDomain, providerName;
    protected final List<ICraftingLoader> craftingLoaders = new ObjectArrayList<>();
    private static final Map<String, AntimatterRecipeProvider> PROVIDERS = new Object2ObjectOpenHashMap<>();

    public AntimatterRecipeProvider(String providerDomain, String providerName, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        PROVIDERS.put(providerDomain,this);
    }

    @Override
    public void act(DirectoryCache cache) {

    }

    @Override
    public void run() {
        /*Set<ResourceLocation> set = Sets.newHashSet();

        registerRecipes(recipe -> {
            if (set.add(recipe.getID())) {
                DynamicResourcePack.addRecipe(recipe);
            }
        });*/
    }

    @Override
    public Types staticDynamic() {
        return Types.DYNAMIC;
    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    private static Map<IRecipeType<?>, Map<ResourceLocation, IRecipe>> MAP = new Object2ObjectOpenHashMap<>();

    private static Map<IRecipeType<?>, Map<ResourceLocation, IRecipe>> CACHE = new Object2ObjectOpenHashMap<>();

    public static void runRecipes(Consumer<IRecipe<?>> rec) {
        Set<ResourceLocation> set = Sets.newHashSet();
        PROVIDERS.forEach((id,t) -> t.registerRecipes(c -> {
            if (set.add(c.getID())) {
                IRecipe<?> r = c.getSerializer().read(c.getID(), c.getRecipeJson());
                rec.accept(r);
            }
        }));
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        registerMaterialRecipes(consumer, providerDomain);
        registerToolRecipes(consumer, providerDomain);
        craftingLoaders.forEach(cl -> cl.loadRecipes(consumer,this));
    }

    protected void registerMaterialRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        AntimatterAPI.all(BlockOre.class, providerDomain, o -> {
            if (o.getOreType() != ORE) return;
            if (!o.getMaterial().has(INGOT)) return;
            Item ingot = INGOT.get(o.getMaterial());
            ITag.INamedTag<Item> oreTag = Utils.getForgeItemTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()));
            ITag.INamedTag<Item> ingotTag = Utils.getForgeItemTag("ingots/".concat(o.getMaterial().getId()));
            CookingRecipeBuilder.blastingRecipe(Ingredient.fromTag(oreTag), ingot, 2.0F, 200)
                    .addCriterion("has_material_" + o.getMaterial().getId(), this.hasItem(ingotTag))
                    .build(consumer, fixLoc(providerDomain, o.getId().concat("_to_ingot")));
        });
        AntimatterAPI.all(Material.class, providerDomain).stream().filter(m -> m.has(DUST)).forEach(mat -> {
            Item dust = DUST.get(mat);
            if (mat.has(ROCK)) {
                ITag.INamedTag<Item> rockTag = getForgeItemTag("rocks/".concat(mat.getId()));
                Item rock = ROCK.get(mat);
                Item smallDust = DUST_SMALL.get(mat);
                ShapelessRecipeBuilder.shapelessRecipe(dust)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(MORTAR.getTag())
                        .addCriterion("has_rock_" + mat.getId(), this.hasItem(rockTag))
                        .setGroup("rocks_grind_to_dust").build(consumer, fixLoc(providerDomain, rock.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));

                ShapelessRecipeBuilder.shapelessRecipe(smallDust)
                        .addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(MORTAR.getTag())
                        .addCriterion("has_rock_" + mat.getId(), this.hasItem(getForgeItemTag("rocks/".concat(mat.getId()))))
                        .setGroup("rocks_grind_to_small_dust").build(consumer, fixLoc(providerDomain, rock.getRegistryName().getPath() + "_grind_to_" + smallDust.getRegistryName().getPath()));
            }
            if (mat.has(INGOT, GRINDABLE)) {
                Item ingot = INGOT.get(mat);
                ITag.INamedTag<Item> ingotTag = getForgeItemTag("ingots/".concat(mat.getId()));
                ShapelessRecipeBuilder.shapelessRecipe(dust).addIngredient(ingotTag).addIngredient(MORTAR.getTag())
                        .addCriterion("has_ingot_" + mat.getId(), this.hasItem(getForgeItemTag("ingots/".concat(mat.getId()))))
                        .setGroup("ingots_grind_to_dust")
                        .build(consumer, fixLoc(providerDomain,ingot.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));
            }
        });
    }

    protected void registerToolRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        List<Material> mainMats = AntimatterAPI.all(Material.class, providerDomain).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(TOOLS))).collect(Collectors.toList());
        List<Material> handleMats = AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.isHandle())).collect(Collectors.toList());

        handleMats.forEach(handle -> AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(RUBBERTOOLS))).forEach(rubber -> {
            ITag.INamedTag<Item> ingotTag = getForgeItemTag("ingots/" + rubber.getId()), rodTag = getForgeItemTag("rods/" + handle.getId());
            addStackRecipe(consumer, Ref.ID, PLUNGER.getId() + "_" + handle.getId() + "_" + rubber.getId(), "antimatter_plungers",
                    "has_material_" + rubber.getId(), hasSafeItem(ingotTag), PLUNGER.getToolStack(handle, rubber),
                    of('W', WIRE_CUTTER.getTag(), 'I', ingotTag, 'S', Tags.Items.SLIMEBALLS, 'R', rodTag, 'F', FILE.getTag()), "WIS", " RI", "R F");
        }));

        mainMats.forEach(main -> {
            if (!main.has(INGOT)) return; // TODO: For time being
            final ITag.INamedTag<Item> ingotTag = getForgeItemTag("ingots/" + main.getId()), plateTag = getForgeItemTag("plates/" + main.getId()), mainRodTag = getForgeItemTag("rods/" + main.getId());
            final Supplier<ICriterionInstance> ingotTrigger = this.hasSafeItem(ingotTag), plateTrigger = this.hasSafeItem(plateTag), rodTrigger = this.hasSafeItem(mainRodTag);

            addStackRecipe(consumer, Ref.ID, WRENCH.getId() + "_" + main.getId(), "antimatter_wrenches",
                    "has_material_" + main.getId(), ingotTrigger, WRENCH.getToolStack(main, NULL), of('I', ingotTag, 'H', HAMMER.getTag()), "IHI", "III", " I ");

            addStackRecipe(consumer, Ref.ID, MORTAR.getId() + "_" + main.getId(), "antimatter_mortars",
                    "has_material_" + main.getId(), ingotTrigger, MORTAR.getToolStack(main, NULL), of('I', ingotTag, 'S', Tags.Items.STONE), " I ", "SIS", "SSS");

            for (DyeColor colour : DyeColor.values()) {
                int colourValue = colour.getMapColor().colorValue;
                ItemStack crowbarStack = CROWBAR.getToolStack(main, NULL);
                crowbarStack.getChildTag(Ref.TAG_TOOL_DATA).putInt(Ref.KEY_TOOL_DATA_SECONDARY_COLOUR, colourValue);
                addStackRecipe(consumer, Ref.ID, CROWBAR.getId() + "_" + main.getId() + "_" + colour.toString(), "antimatter_crowbars",
                        "has_material_" + main.getId(), rodTrigger, crowbarStack, of('H', HAMMER.getTag(), 'C', colour.getTag(), 'R', mainRodTag, 'F', FILE.getTag()), "HCR", "CRC", "RCF");
            }

            for (Material handle : handleMats) {
                String handleId = handle.getId().equals("wood") ? "wooden" : handle.getId();
                final ITag.INamedTag<Item> rodTag = getForgeItemTag("rods/" + handleId);

                addStackRecipe(consumer, Ref.ID, PICKAXE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_pickaxes",
                        "has_material_" + main.getId(), ingotTrigger, PICKAXE.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PII", "FRH", " R ");

                addStackRecipe(consumer, Ref.ID, AXE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_axes",
                        "has_material_" + main.getId(), ingotTrigger, AXE.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "IIH", "PR ", "FR ");

                addStackRecipe(consumer, Ref.ID, HOE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_hoes",
                        "has_material_" + main.getId(), ingotTrigger, HOE.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PIH", "FR ", " R ");

                addStackRecipe(consumer, Ref.ID, SHOVEL.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_shovels",
                        "has_material_" + main.getId(), ingotTrigger, SHOVEL.getToolStack(main, handle), of('R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "FPH", " R ", " R ");

                addStackRecipe(consumer, Ref.ID, SWORD.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_swords",
                        "has_material_" + main.getId(), ingotTrigger, SWORD.getToolStack(main, handle), of('R', rodTag, 'P', plateTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), " P ", "FPH", " R ");

                addStackRecipe(consumer, Ref.ID, HAMMER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_hammers",
                        "has_material_" + main.getId(), ingotTrigger, HAMMER.getToolStack(main, handle), of('I', ingotTag, 'R', rodTag), "II ", "IIR", "II ");

                addStackRecipe(consumer, Ref.ID, SAW.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_saws",
                        "has_material_" + main.getId(), plateTrigger, SAW.getToolStack(main, handle), of('P', plateTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PPR", "FH ");

                addStackRecipe(consumer, Ref.ID, FILE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_files",
                        "has_material_" + main.getId(), plateTrigger, FILE.getToolStack(main, handle), of('P', plateTag, 'R', rodTag), "P", "P", "R");

                addStackRecipe(consumer, Ref.ID, KNIFE.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_knives",
                        "has_material_" + main.getId(), plateTrigger, KNIFE.getToolStack(main, handle), of('P', plateTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), "FPH", " R ");

                addStackRecipe(consumer, Ref.ID, SCREWDRIVER.getId() + "_" + main.getId() + "_" + handle.getId(), "antimatter_screwdrivers",
                        "has_material_" + main.getId(), rodTrigger, SCREWDRIVER.getToolStack(main, handle),
                        of('M', mainRodTag, 'R', rodTag, 'F', FILE.getTag(), 'H', HAMMER.getTag()), " FM", " MH", "R  ");

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
        });
    }

    public void addConditionalRecipe(Consumer<IFinishedRecipe> consumer, AntimatterShapedRecipeBuilder builtRecipe, Class configClass, String configFieldName, String recipeDomain, String recipeName) {
        ConditionalRecipe.builder().addCondition(new ConfigCondition(configClass, configFieldName))
        .addRecipe(builtRecipe::build).build(consumer, recipeDomain, recipeName);
    }

    public AntimatterShapedRecipeBuilder getItemRecipe(String groupName, String criterionName, Supplier<ICriterionInstance> criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        return getStackRecipe(groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    public AntimatterShapedRecipeBuilder getStackRecipe(String groupName, String criterionName, Supplier<ICriterionInstance> criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        if (inputs.isEmpty()) Utils.onInvalidData("Inputs should not be empty!");
        if (inputPattern.length < 1 || inputPattern.length > 3) Utils.onInvalidData("Input pattern must have between 1 and 3 rows!");
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

    public void shapeless(Consumer<IFinishedRecipe> consumer, String recipeID, String groupName, String criterionName, Supplier<ICriterionInstance> criterion, ItemStack output, Object... inputs) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(output.getItem(),output.getCount()).addCriterion(criterionName,criterion.get())
                .setGroup(groupName);
        for (Object input : inputs) {
            try {
                if (input instanceof IItemProvider) {
                    builder.addIngredient(((IItemProvider)input));
                } else if (input instanceof ITag) {
                    builder.addIngredient((ITag<Item>)input);
                } else if (input instanceof Ingredient) {
                    builder.addIngredient((Ingredient) input);
                }
            } catch (ClassCastException ex) {
                throw new RuntimeException("ERROR PARSING SHAPELESS RECIPE" + ex.getMessage());
            }
        }
        builder.build(consumer, new ResourceLocation(Ref.ID, output.getItem().toString()+"_"+recipeID));
    }

    public void addItemRecipe(Consumer<IFinishedRecipe> consumer, String groupName, String criterionName, Supplier<ICriterionInstance> criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        addStackRecipe(consumer, "", "", groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    public void addItemRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, Supplier<ICriterionInstance> criterion, IItemProvider output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        addStackRecipe(consumer, recipeDomain, recipeName, groupName, criterionName, criterion, new ItemStack(output), inputs, inputPattern);
    }

    public void addStackRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, Supplier<ICriterionInstance> criterion, ItemStack output, Function<ImmutableMap.Builder<Character, Object>, ImmutableMap.Builder<Character, Object>> inputs, String... inputPattern) {
        addStackRecipe(consumer, recipeDomain, recipeName, groupName, criterionName, criterion, output, inputs.apply(new ImmutableMap.Builder<>()).build(), inputPattern);
    }

    public void addStackRecipe(Consumer<IFinishedRecipe> consumer, String recipeDomain, String recipeName, String groupName, String criterionName, Supplier<ICriterionInstance> criterion, ItemStack output, ImmutableMap<Character, Object> inputs, String... inputPattern) {
        AntimatterShapedRecipeBuilder recipeBuilder = getStackRecipe(groupName, criterionName, criterion, output, inputs, inputPattern);
        if (recipeName.isEmpty()) recipeBuilder.build(consumer);
        else {
            if (recipeDomain.isEmpty()) recipeBuilder.build(consumer, recipeName);
            else recipeBuilder.build(consumer, fixLoc(recipeDomain, recipeName));
        }
    }

    @SuppressWarnings("unchecked")
    protected AntimatterShapedRecipeBuilder resolveKeys(AntimatterShapedRecipeBuilder incompleteBuilder, ImmutableMap<Character, Object> inputs) {
        for (Map.Entry<Character, Object> entry : inputs.entrySet()) {
            if (entry.getValue() instanceof IItemProvider) {
                incompleteBuilder = incompleteBuilder.key(entry.getKey(), (IItemProvider) entry.getValue());
            }
            else if (entry.getValue() instanceof ITag) {
                try {
                    incompleteBuilder = incompleteBuilder.key(entry.getKey(), (ITag<Item>) entry.getValue());
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

    public Supplier<ICriterionInstance> hasSafeItem(ITag<Item> tag) {
        return () -> RecipeProvider.hasItem(tag);
    }

    public Supplier<ICriterionInstance> hasSafeItem(IItemProvider stack) {
        return () -> RecipeProvider.hasItem(stack);
    }
}
