package muramasa.antimatter.datagen.providers;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.ICraftingLoader;
import muramasa.antimatter.datagen.builder.AntimatterCookingRecipeBuilder;
import muramasa.antimatter.datagen.builder.AntimatterShapedRecipeBuilder;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.PipeItemBlock;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTag.*;
import static muramasa.antimatter.recipe.RecipeBuilders.*;
import static muramasa.antimatter.util.TagUtils.getForgeItemTag;
import static muramasa.antimatter.util.TagUtils.nc;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

//Only extending RecipeProvider for static purposes.
public class AntimatterRecipeProvider extends RecipeProvider implements IAntimatterProvider {

    protected final String providerDomain, providerName;
    protected final List<ICraftingLoader> craftingLoaders = new ObjectArrayList<>();

    public static AntimatterRecipeProvider DEFAULT;

    public AntimatterRecipeProvider(String providerDomain, String providerName, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
    }

    @Override
    public void act(DirectoryCache cache) {

    }

    @Override
    public void run() {

    }

    @Override
    public boolean shouldRun() {
        return false;
    }

    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        if (providerDomain.equals(Ref.ID)) {
            registerMaterialRecipes(consumer, providerDomain);
        }
        registerToolRecipes(consumer, providerDomain);
        registerPipeRecipes(consumer, providerDomain);
        registerOther(consumer, providerDomain);
        craftingLoaders.forEach(cl -> cl.loadRecipes(consumer, this));
    }

    protected void registerOther(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        final ICriterionInstance in = this.hasSafeItem(WRENCH.getTag());
        if (providerDomain.equals(Ref.ID)) {
            addToolRecipe(DUST_BUILDER.get(DUST.getId()), consumer, Ref.ID, "dust_small", "antimatter_dusts",
                    "has_wrench", in, DUST.all().stream().filter(t -> t.has(DUST_SMALL)).map(t -> DUST.get(t, 1)).collect(Collectors.toList()), of('D', PropertyIngredient.builder("primary").types(DUST_SMALL).tags(DUST).build()), "DD", "DD");
            addToolRecipe(DUST_BUILDER.get(DUST.getId()), consumer, Ref.ID, "dust_tiny", "antimatter_dusts",
                    "has_wrench", in, DUST.all().stream().filter(t -> t.has(DUST_TINY)).map(t -> DUST.get(t, 1)).collect(Collectors.toList()), of('D', PropertyIngredient.builder("primary").types(DUST_TINY).tags(DUST).build()), "DDD", "DDD", "DDD");
        }
    }

    protected void registerMaterialRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        AntimatterAPI.all(BlockOre.class, o -> {
            if (o.getOreType() != ORE) return;
            if (!o.getMaterial().getSmeltInto().has(INGOT)) return;
            if (o.getMaterial().needsBlastFurnace()) return;
            Item ingot = INGOT.get(o.getMaterial().getSmeltInto());
            ITag.INamedTag<Item> oreTag = TagUtils.getForgeItemTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()));
            ITag.INamedTag<Item> ingotTag = TagUtils.getForgeItemTag("ingots/".concat(o.getMaterial().getSmeltInto().getId()));
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(oreTag, 1).get(), new ItemStack(ingot, o.getMaterial().getSmeltingMulti()), 2.0F, 100)
                    .addCriterion("has_material_" + o.getMaterial().getId(), hasItem(ingotTag))
                    .build(consumer, fixLoc(providerDomain, o.getId().concat("_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(oreTag, 1).get(), new ItemStack(ingot, o.getMaterial().getSmeltingMulti()), 2.0F, 200)
                    .addCriterion("has_material_" + o.getMaterial().getId(), hasItem(ingotTag))
                    .build(consumer, fixLoc(providerDomain, o.getId().concat("_to_ingot_smelting")));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.has(RAW_ORE) && m.getSmeltInto().has(INGOT) && !m.needsBlastFurnace()).forEach(m -> {
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1).get(), new ItemStack(INGOT.get(m.getSmeltInto()), m.getSmeltingMulti()), 2.0F, 100)
                    .addCriterion("has_material_" + m.getId(), hasItem(INGOT.getMaterialTag(m.getSmeltInto())))
                    .build(consumer, fixLoc(providerDomain, m.getId().concat("_raw_ore_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1).get(), new ItemStack(INGOT.get(m.getSmeltInto()), m.getSmeltingMulti()), 2.0F, 200)
                    .addCriterion("has_material_" + m.getId(), hasItem(INGOT.getMaterialTag(m.getSmeltInto())))
                    .build(consumer, fixLoc(providerDomain, m.getId().concat("_raw_ore_to_ingot_smelting")));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.has(DUST)).forEach(mat -> {
            Item dust = DUST.get(mat);
            if (mat.has(ROCK)) {
                ITag<Item> rockTag = nc(TagUtils.getForgeItemTag("rocks/".concat(mat.getId())));
                Item rock = ROCK.get(mat);
                Item smallDust = DUST_SMALL.get(mat);
                ShapelessRecipeBuilder.shapelessRecipe(dust)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_rock_" + mat.getId(), this.hasItem(rockTag))
                        .setGroup("rocks_grind_to_dust").build(consumer, fixLoc(providerDomain, rock.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));

                ShapelessRecipeBuilder.shapelessRecipe(smallDust)
                        .addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_rock_" + mat.getId(), this.hasItem(getForgeItemTag("rocks/".concat(mat.getId()))))
                        .setGroup("rocks_grind_to_small_dust").build(consumer, fixLoc(providerDomain, rock.getRegistryName().getPath() + "_grind_to_" + smallDust.getRegistryName().getPath()));
            }
            if (mat.has(INGOT, GRINDABLE)) {
                Item ingot = INGOT.get(mat);
                ITag<Item> ingotTag = nc(TagUtils.getForgeItemTag("ingots/".concat(mat.getId())));
                ShapelessRecipeBuilder.shapelessRecipe(dust).addIngredient(ingotTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_ingot_" + mat.getId(), this.hasItem(nc(TagUtils.getForgeItemTag("ingots/".concat(mat.getId())))))
                        .setGroup("ingots_grind_to_dust")
                        .build(consumer, fixLoc(providerDomain, ingot.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));
            }
        });
    }

    protected void registerPipeRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        if (providerDomain.equals(Ref.ID)) {
            final ICriterionInstance in = this.hasSafeItem(WRENCH.getTag());
            List<ItemStack> stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.TINY)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.TINY), 12)).collect(Collectors.toList());
            Material[] mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.TINY.getId()), consumer, Ref.ID, "pipe_item_tiny", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "H W", "PPP");

            stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.SMALL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.SMALL), 6)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.SMALL.getId()), consumer, Ref.ID, "pipe_item_small", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PWP", "P P", "PHP");

            stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.NORMAL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.NORMAL), 2)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.NORMAL.getId()), consumer, Ref.ID, "pipe_item_normal", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "W H", "PPP");

            stacks = AntimatterAPI.all(ItemPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.LARGE)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.LARGE), 1)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(ITEM_PIPE_BUILDER.get(PipeSize.LARGE.getId()), consumer, Ref.ID, "pipe_item_large", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PHP", "P P", "PWP");

            stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.TINY)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.TINY), 12)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.TINY.getId()), consumer, Ref.ID, "pipe_fluid_tiny", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "H W", "PPP");

            stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.SMALL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.SMALL), 6)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.SMALL.getId()), consumer, Ref.ID, "pipe_fluid_small", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PWP", "P P", "PHP");

            stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.NORMAL)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.NORMAL), 2)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.NORMAL.getId()), consumer, Ref.ID, "pipe_fluid_normal", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PPP", "W H", "PPP");

            stacks = AntimatterAPI.all(FluidPipe.class).stream().filter(t -> t.getSizes().contains(PipeSize.LARGE)).filter(t -> t.getMaterial().has(PLATE)).map(t -> new ItemStack(t.getBlock(PipeSize.LARGE), 1)).collect(Collectors.toList());
            mats = stacks.stream().map(t -> ((PipeItemBlock) t.getItem()).getPipe().getType().getMaterial()).toArray(Material[]::new);
            if (stacks.size() > 0)
                addToolRecipe(FLUID_PIPE_BUILDER.get(PipeSize.LARGE.getId()), consumer, Ref.ID, "pipe_fluid_large", "antimatter_pipes",
                        "has_wrench", in, stacks, of('H', HAMMER.getTag(), 'W', WRENCH.getTag(), 'P', PropertyIngredient.builder("primary").types(PLATE).mats(mats).build()), "PHP", "P P", "PWP");
        }
    }

    protected void registerToolRecipes(Consumer<IFinishedRecipe> consumer, String providerDomain) {
        if (providerDomain.equals(Ref.ID)) {
            final ICriterionInstance in = this.hasSafeItem(WRENCH.getTag());

            if (AntimatterAPI.isModLoaded(Ref.MOD_TOP)) {
                addToolRecipe(PROBE_BUILDER.get(HELMET.getId()), consumer, Ref.ID, "helmet_with_probe", "antimatter_armor",
                        "has_helmet", this.hasSafeItem(HELMET.getToolStack(NULL).getItem()), Collections.singletonList(HELMET.getToolStack(NULL)), of('H', PropertyIngredient.builder("helmet").itemStacks(HELMET.getToolStack(NULL).getItem()).build(), 'P', ForgeRegistries.ITEMS.getValue(new ResourceLocation(Ref.MOD_TOP, "probe"))), "HP");
            }

            addToolRecipe(TOOL_BUILDER.get(HAMMER.getId()), consumer, Ref.ID, HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                    "has_wrench", in, Collections.singletonList(HAMMER.getToolStack(NULL, NULL)), of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(HAMMER, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
            addToolRecipe(TOOL_BUILDER.get(SOFT_HAMMER.getId()), consumer, Ref.ID, SOFT_HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                    "has_wrench", in, Collections.singletonList(SOFT_HAMMER.getToolStack(NULL, NULL)), of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(SOFT_HAMMER, true).tags(RUBBERTOOLS).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
            if (Material.get("wood") != NULL && Material.get("wood").getToolTypes().contains(SOFT_HAMMER)) {
                addToolRecipe(WOOD_TOOL_BUILDER.get(SOFT_HAMMER.getId()), consumer, Ref.ID, SOFT_HAMMER.getId() + "_wood_" + "recipe", "antimatter_tools",
                        "has_wrench", in, Collections.singletonList(SOFT_HAMMER.getToolStack(Material.get("wood"), NULL)), of('I', ItemTags.PLANKS, 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
            }

            addToolRecipe(ARMOR_BUILDER.get(HELMET.getId()), consumer, Ref.ID, HELMET.getId() + "_recipe", "antimatter_helmets",
                    "has_wrench", in, Collections.singletonList(HELMET.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "III", "IHI");
            addToolRecipe(ARMOR_BUILDER.get(CHESTPLATE.getId()), consumer, Ref.ID, CHESTPLATE.getId() + "_recipe", "antimatter_chestplates",
                    "has_wrench", in, Collections.singletonList(CHESTPLATE.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "IHI", "III", "III");
            addToolRecipe(ARMOR_BUILDER.get(LEGGINGS.getId()), consumer, Ref.ID, LEGGINGS.getId() + "_recipe", "antimatter_leggings",
                    "has_wrench", in, Collections.singletonList(LEGGINGS.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "III", "IHI", "I I");
            addToolRecipe(ARMOR_BUILDER.get(BOOTS.getId()), consumer, Ref.ID, BOOTS.getId() + "_recipe", "antimatter_boots",
                    "has_wrench", in, Collections.singletonList(BOOTS.getToolStack(NULL)), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(ARMOR).build(), 'H', HAMMER.getTag()), "I I", "IHI");

            /*addToolRecipe(TOOL_BUILDER.get(PLUNGER.getId()), consumer, muramasa.antimatter.Ref.ID, PLUNGER.getId() + "_recipe", "antimatter_plungers",
                    "has_wrench", in, Collections.singletonList(PLUNGER.getToolStack(NULL, NULL)),
                    of('W', WIRE_CUTTER.getTag(), 'I',  PropertyIngredient.of(INGOT, "primary"), 'S', Tags.Items.SLIMEBALLS, 'R', PropertyIngredient.builder("secondary").types(ROD).tags(RUBBERTOOLS).build(), 'F', FILE.getTag()), "WIS", " RI", "R F");*/

            addToolRecipe(TOOL_BUILDER.get(WRENCH.getId()), consumer, Ref.ID, WRENCH.getId() + "_recipe", "antimatter_wrenches",
                    "has_wrench", in, WRENCH.getToolStack(NULL, NULL), of('I', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(WRENCH, true).build(), 'H', HAMMER.getTag()), "IHI", "III", " I ");

            addToolRecipe(TOOL_BUILDER.get(MORTAR.getId()), consumer, Ref.ID, MORTAR.getId() + "_recipe", "antimatter_mortars",
                    "has_wrench", in, MORTAR.getToolStack(NULL, NULL), of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(MORTAR, true).build(), 'S', Tags.Items.STONE), " I ", "SIS", "SSS");

            addToolRecipe(TOOL_BUILDER.get(FILE.getId()), consumer, Ref.ID, FILE.getId() + "_recipe", "antimatter_files",
                    "has_wrench", in, FILE.getToolStack(NULL, NULL), of('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(FILE, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()), "P", "P", "R");

            addToolRecipe(TOOL_BUILDER.get(SCREWDRIVER.getId()), consumer, Ref.ID, SCREWDRIVER.getId() + "_recipe", "antimatter_screwdrivers",
                    "has_wrench", in, SCREWDRIVER.getToolStack(NULL, NULL),
                    of('M', PropertyIngredient.builder("primary").types(ROD).tool(SCREWDRIVER, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag()), " FM", " MH", "R  ");

            addToolRecipe(TOOL_BUILDER.get(SAW.getId()), consumer, Ref.ID, SAW.getId() + "_recipe", "antimatter_saws",
                    "has_wrench", in, SAW.getToolStack(NULL, NULL), of('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(SAW, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag()), "PPR", "FH ");

            addToolRecipe(TOOL_BUILDER.get(WIRE_CUTTER.getId()), consumer, Ref.ID, WIRE_CUTTER.getId() + "_recipe_noscrew", "antimatter_files",
                    "has_wrench", in, WIRE_CUTTER.getToolStack(NULL, NULL), b ->
                            b.put('P', PropertyIngredient.builder("primary").inverse().tool(SCREWDRIVER, true).types(PLATE, GEM).tags(SCREW).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                    .put('S', SCREWDRIVER.getTag())
                    , "PFP", "HPS", "R R");
            addToolRecipe(TOOL_BUILDER.get(WIRE_CUTTER.getId()), consumer, Ref.ID, WIRE_CUTTER.getId() + "_recipe_screw", "antimatter_files",
                    "has_wrench", in, WIRE_CUTTER.getToolStack(NULL, NULL), b ->
                            b.put('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(SCREW).tool(SCREWDRIVER, true).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag()).put('H', HAMMER.getTag())
                                    .put('S', SCREWDRIVER.getTag()).put('W', PropertyIngredient.of(SCREW, "primary"))
                    , "PFP", "HPS", "RWR");

            addToolRecipe(TOOL_BUILDER.get(BRANCH_CUTTER.getId()), consumer, Ref.ID, BRANCH_CUTTER.getId() + "_recipe_noscrew", "antimatter_files",
                    "has_wrench", in, BRANCH_CUTTER.getToolStack(NULL, NULL), b ->
                            b.put('P', PropertyIngredient.builder("primary").inverse().tool(SCREWDRIVER, true).types(PLATE, GEM).tags(SCREW).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag())
                                    .put('S', SCREWDRIVER.getTag())
                    , "PFP", "PSP", "R R");
            addToolRecipe(TOOL_BUILDER.get(BRANCH_CUTTER.getId()), consumer, Ref.ID, BRANCH_CUTTER.getId() + "_recipe_screw", "antimatter_files",
                    "has_wrench", in, BRANCH_CUTTER.getToolStack(NULL, NULL), b ->
                            b.put('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tags(SCREW).tool(SCREWDRIVER, true).build()).put('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build()).put('F', FILE.getTag())
                                    .put('S', SCREWDRIVER.getTag()).put('W', PropertyIngredient.of(SCREW, "primary"))
                    , "PFP", "PSP", "RWR");

            Function<AntimatterToolType, ImmutableMap<Character, Object>> map1 = type -> of('I', PropertyIngredient.builder("primary").types(INGOT, GEM).tool(type, true).build(), 'R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(type, true).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag());

            Function<AntimatterToolType, ImmutableMap<Character, Object>> map2 = type -> of('R', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(type, true).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag());

            String[] strings1 = new String[]{"PII", "FRH", " R "};
            String[] strings3 = new String[]{" P ", "FPH", " R "};

            String[] strings2 = new String[]{"FPH", " R ", " R "};
            String[] strings2Gem = new String[]{"FGH", " R ", " R "};

            addToolRecipe(TOOL_BUILDER.get(PICKAXE.getId()), consumer, Ref.ID, PICKAXE.getId() + "_with", "antimatter_pickaxes",
                    "has_wrench", in, PICKAXE.getToolStack(NULL, NULL), map1.apply(PICKAXE), strings1);

            addToolRecipe(TOOL_BUILDER.get(SHOVEL.getId()), consumer, Ref.ID, SHOVEL.getId() + "_with", "antimatter_shovels",
                    "has_wrench", in, SHOVEL.getToolStack(NULL, NULL), map2.apply(SHOVEL), strings2);

            addToolRecipe(TOOL_BUILDER.get(AXE.getId()), consumer, Ref.ID, AXE.getId() + "_with", "antimatter_axes",
                    "has_wrench", in, AXE.getToolStack(NULL, NULL), map1.apply(AXE), "PIH", "PR ", "FR ");


            addToolRecipe(TOOL_BUILDER.get(SWORD.getId()), consumer, Ref.ID, SWORD.getId() + "_with", "antimatter_swords",
                    "has_wrench", in, SWORD.getToolStack(NULL, NULL), map2.apply(SWORD), " P ", "FPH", " R ");

            addToolRecipe(TOOL_BUILDER.get(HOE.getId()), consumer, Ref.ID, HOE.getId() + "_with", "antimatter_swords",
                    "has_wrench", in, HOE.getToolStack(NULL, NULL), map1.apply(HOE), "PIH", "FR ", " R ");

            addToolRecipe(CROWBAR_BUILDER.get(CROWBAR.getId()), consumer, Ref.ID, CROWBAR.getId() + "_recipe", "antimatter_crowbars",
                    "has_wrench", in, CROWBAR.getToolStack(NULL, NULL), of('H', HAMMER.getTag(), 'C', PropertyIngredient.builder("secondary").itemTags(Tags.Items.DYES).build(), 'R', PropertyIngredient.builder("primary").types(ROD).tool(CROWBAR, true).build(), 'F', FILE.getTag()), "HCR", "CRC", "RCF");

            addToolRecipe(TOOL_BUILDER.get(KNIFE.getId()), consumer, Ref.ID, KNIFE.getId() + "_with", "antimatter_knives",
                    "has_wrench", in, KNIFE.getToolStack(NULL, NULL), of('P', PropertyIngredient.builder("primary").types(PLATE, GEM).tool(KNIFE, true).build(), 'S', PropertyIngredient.builder("secondary").types(ROD).tags(HANDLE).build(), 'F', FILE.getTag(), 'H', HAMMER.getTag()), "FP", "HS");

            // List<Material> handleMats = AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(HANDLE))).collect(Collectors.toList());

            addToolRecipe(TOOL_BUILDER.get(PLUNGER.getId()), consumer, Ref.ID, PLUNGER.getId() + "_", "antimatter_plungers",
                    "has_wrench", in, PLUNGER.getToolStack(NULL, NULL),
                    of('W', WIRE_CUTTER.getTag(), 'R', PropertyIngredient.builder("primary").tool(PLUNGER, true).types(ROD).build(), 'I', PropertyIngredient.builder("secondary").types(PLATE).tags(HANDLE, RUBBERTOOLS).build(), 'F', FILE.getTag()), "WII", " RI", "R F");
        }


        if (true) return;

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
    }

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
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(output.getItem(), output.getCount()).addCriterion(criterionName, criterion)
                .setGroup(groupName);
        for (Object input : inputs) {
            try {
                if (input instanceof IItemProvider) {
                    builder.addIngredient(((IItemProvider) input));
                } else if (input instanceof ITag) {
                    if (input instanceof ITag.INamedTag) {
                        builder.addIngredient(nc(TagUtils.getItemTag(((ITag.INamedTag) input).getName())));
                    } else {
                        builder.addIngredient((ITag<Item>) input);
                    }
                } else if (input instanceof Ingredient) {
                    builder.addIngredient((Ingredient) input);
                }
            } catch (ClassCastException ex) {
                throw new RuntimeException("ERROR PARSING SHAPELESS RECIPE" + ex.getMessage());
            }
        }
        builder.build(consumer, new ResourceLocation(Ref.ID, output.getItem().toString() + "_" + recipeID));
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

    protected String fixLoc(String providerDomain, String attach) {
        return providerDomain.concat(":").concat(attach);
    }

    @Override
    public String getName() {
        return providerName;
    }

    public ICriterionInstance hasSafeItem(ITag<Item> tag) {
        return RecipeProvider.hasItem(tag);
    }

    public ICriterionInstance hasSafeItem(IItemProvider stack) {
        return RecipeProvider.hasItem(stack);
    }
}
